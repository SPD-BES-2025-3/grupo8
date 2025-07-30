package com.livraria.api.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature; // Importe esta classe
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.livraria.api.model.Livro;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

/**
 * Publicador de mensagens JMS para a API, seguindo o estilo estático do Desktop.
 */
public class JmsPublisher {

    private static final String BROKER_URL = "tcp://activemq:61616";
    private static final String QUEUE_NAME = "API_PARA_DESKTOP_QUEUE";
    
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Bloco estático para configurar o ObjectMapper uma única vez
    static {
        // Registra o módulo para lidar com tipos de data/hora do Java 8+
        objectMapper.registerModule(new JavaTimeModule());
        // *** CORREÇÃO AQUI: Desabilita a escrita de datas como timestamps/arrays ***
        // Isso força a serialização para o formato de string ISO 8601
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public static void publicarMensagem(String operacao, Livro livro) {
        ConnectionFactory factory = new ActiveMQConnectionFactory("admin", "admin", BROKER_URL);
        try (Connection connection = factory.createConnection();
             Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {

            Destination destination = session.createQueue(QUEUE_NAME);
            MessageProducer producer = session.createProducer(destination);
            
            // Agora o objectMapper irá gerar a data como uma string
            String jsonPayload = objectMapper.writeValueAsString(livro);
            
            TextMessage message = session.createTextMessage(jsonPayload);
            
            message.setStringProperty("operacao", operacao);
            message.setStringProperty("entidade", "Livro");
            message.setStringProperty("idApi", livro.getId());

            producer.send(message);
            System.out.println("[API Static Publisher] Mensagem JMS enviada para a fila '" + QUEUE_NAME + "'. Operação: " + operacao);

        } catch (Exception e) {
            System.err.println("ERRO: Falha ao publicar mensagem JMS da API. " + e.getMessage());
        }
    }
}