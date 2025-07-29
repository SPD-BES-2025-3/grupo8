package com.livraria.api.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.livraria.api.model.Livro;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

/**
 * Publicador de mensagens JMS para a API, seguindo o estilo estático do Desktop.
 */
public class JmsPublisher {

    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String QUEUE_NAME = "API_PARA_DESKTOP_QUEUE";
    
    // ObjectMapper é a biblioteca padrão do Spring para JSON.
    // Criamos uma instância estática para reutilização.
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // Registra o módulo para lidar com tipos de data/hora do Java 8+, como LocalDateTime
        objectMapper.registerModule(new JavaTimeModule());
    }

    public static void publicarMensagem(String operacao, Livro livro) {
        ConnectionFactory factory = new ActiveMQConnectionFactory("admin", "admin", BROKER_URL);
        try (Connection connection = factory.createConnection();
             Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {

            Destination destination = session.createQueue(QUEUE_NAME);
            MessageProducer producer = session.createProducer(destination);
            
            // Usando ObjectMapper (Jackson) para serializar, que lida bem com datas
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