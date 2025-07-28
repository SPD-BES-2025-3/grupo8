package com.livraria.integrador.routes;

import com.livraria.integrador.processor.ApiToDesktopProcessor;
import com.livraria.integrador.processor.DesktopToApiProcessor;
import com.livraria.integrador.processor.PersistenciaCanonicoProcessor;
import com.livraria.integrador.processor.RemoveMapeamentoProcessor;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.model.ChoiceDefinition;

public class LivrariaRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        // 1. Configura a conexão com o broker ActiveMQ
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        factory.setUserName("admin");
        factory.setPassword("admin");
        getContext().addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(factory));

        // =================================================================
        // ROTA 1: FLUXO DESKTOP (ORM) -> API (ODM)
        // =================================================================
        ((ChoiceDefinition) from("jms:queue:DESKTOP_PARA_API_QUEUE")
            .routeId("rota-desktop-para-api")
            .log(">> Mensagem da fila do DESKTOP recebida. Entidade: ${header.entidade}, Operação: ${header.operacao}")
            .choice()
                .when(header("entidade").isEqualTo("Livro"))
                    .process(new DesktopToApiProcessor())
                    .setHeader("Content-Type", constant("application/json"))
                    .choice()
                        .when(header("operacao").isEqualTo("CREATE"))
                            .log("Enviando POST para /api/livros com corpo: ")
                            .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                            .to("http://localhost:8080/api/livros?bridgeEndpoint=true&throwExceptionOnFailure=false")
                        .when(header("operacao").isEqualTo("UPDATE"))
                            .log("Enviando PUT para /api/livros/${header.idApi} com corpo: ")
                            .setHeader(Exchange.HTTP_METHOD, constant("PUT"))
                            .toD("http://localhost:8080/api/livros/${header.idApi}?bridgeEndpoint=true&throwExceptionOnFailure=false")
                        .when(header("operacao").isEqualTo("DELETE"))
                            .log("Enviando DELETE para /api/livros/${header.idApi}")
                            .setHeader(Exchange.HTTP_METHOD, constant("DELETE"))
                            .toD("http://localhost:8080/api/livros/${header.idApi}?bridgeEndpoint=true&throwExceptionOnFailure=false")
                    .end()
                    
                    // *** CORREÇÃO DEFINITIVA AQUI: Chamada direta ao processador final ***
                    // Ele agora é responsável por verificar o sucesso e processar a resposta.
                    .process(new PersistenciaCanonicoProcessor()))
                .otherwise()
                    .log("AVISO: Entidade '${header.entidade}' vinda do desktop não possui rota de integração.")
            .end();

        // =================================================================
        // ROTA 2: FLUXO API (ODM) -> DESKTOP (ORM)
        // =================================================================
        from("jms:queue:API_PARA_DESKTOP_QUEUE")
            .routeId("rota-api-para-desktop")
            .log("<< Mensagem da fila da API recebida. Entidade: ${header.entidade}, Operação: ${header.operacao}")
            .process(new ApiToDesktopProcessor())
            .log("Operação realizada no banco de dados do Desktop.");
    }
}