package com.livraria.integrador.routes;

import com.livraria.integrador.processor.ApiToDesktopProcessor;
import com.livraria.integrador.processor.DesktopToApiProcessor;
import com.livraria.integrador.processor.PersistenciaCanonicoProcessor;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;

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
        from("jms:queue:DESKTOP_PARA_API_QUEUE")
                .routeId("rota-desktop-para-api")
                .log(">> Mensagem da fila do DESKTOP recebida. Entidade: ${header.entidade}, Operação: ${header.operacao}")
                .choice()
                .when(header("entidade").isEqualTo("Livro"))
                .log("Roteando e processando Livro (Desktop -> API)")
                // Lógica da sub-rota agora está aqui dentro
                .process(new DesktopToApiProcessor()) // Transforma modelo Desktop -> API DTO
                .log("JSON transformado para API: ${body}")
                .setHeader("Content-Type", constant("application/json"))
                .toD("http://localhost:8080/api/livros/${header.idDesktop}?bridgeEndpoint=true&throwExceptionOnFailure=false")
                .choice()
                .when(header("CamelHttpResponseCode").in("200", "201"))
                .log("API respondeu com sucesso: ${header.CamelHttpResponseCode}")
                .process(new PersistenciaCanonicoProcessor()) // Persiste/Atualiza o mapeamento
                .otherwise()
                .log("ERRO: A API retornou um erro: ${header.CamelHttpResponseCode}. Corpo: ${body}")
                .endChoice()
                .otherwise()
                .log("AVISO: Entidade '${header.entidade}' vinda do desktop não possui rota de integração.");

        // =================================================================
        // ROTA 2: FLUXO API (ODM) -> DESKTOP (ORM)
        // =================================================================
        from("jms:queue:API_PARA_DESKTOP_QUEUE")
                .routeId("rota-api-para-desktop")
                .log("<< Mensagem da fila da API recebida. Entidade: ${header.entidade}, Operação: ${header.operacao}")
                .choice()
                .when(header("entidade").isEqualTo("Livro"))
                .log("Roteando e processando Livro (API -> Desktop)")
                // Lógica da sub-rota agora está aqui dentro
                .process(new ApiToDesktopProcessor())
                .log("Operação realizada no banco de dados do Desktop.")
                .otherwise()
                .log("AVISO: Entidade '${header.entidade}' vinda da API não é suportada.");
    }
}