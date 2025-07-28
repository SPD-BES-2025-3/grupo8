package com.livraria.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.livraria.api.model.Livro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import javax.jms.TextMessage;

@Service
public class JmsProducerService {

    @Autowired
    private JmsTemplate jmsTemplate;

    private static final String QUEUE_NAME = "LIVRO_SYNC_ODM_TO_ORM";

    public void sendMessage(String operation, Livro livro) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            // O modelo Livro da API já é um DTO simples, podemos usá-lo diretamente
            String jsonPayload = mapper.writeValueAsString(livro);

            jmsTemplate.send(QUEUE_NAME, session -> {
                TextMessage message = session.createTextMessage(jsonPayload);
                message.setStringProperty("operation", operation);
                message.setStringProperty("livroApiId", livro.getId());
                return message;
            });
            System.out.println("[API Producer] Mensagem enviada: " + operation + " para Livro ID " + livro.getId());
        } catch (Exception e) {
            System.err.println("Falha ao enviar mensagem JMS da API: " + e.getMessage());
        }
    }
}