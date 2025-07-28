package com.livraria.api.service;

import com.fasterxml.jackson.databind.ObjectMapper; // Importe o ObjectMapper do Jackson
import com.livraria.api.model.Livro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import javax.jms.TextMessage;

@Service
public class JmsPublisherService {

    private static final String QUEUE_NAME = "API_PARA_DESKTOP_QUEUE";

    @Autowired
    private JmsTemplate jmsTemplate;

    // O Spring Boot injeta automaticamente um ObjectMapper já configurado
    @Autowired
    private ObjectMapper objectMapper;

    public void publicarMensagem(String operacao, Livro livro) {
        try {
            // *** CORREÇÃO AQUI: Usando ObjectMapper (Jackson) em vez de Gson ***
            String jsonPayload = objectMapper.writeValueAsString(livro);
            
            jmsTemplate.send(QUEUE_NAME, messageCreator -> {
                TextMessage message = messageCreator.createTextMessage(jsonPayload);
                message.setStringProperty("operacao", operacao);
                message.setStringProperty("entidade", "Livro");
                message.setStringProperty("idApi", livro.getId());
                return message;
            });
            System.out.println("[API Publisher] Mensagem JMS enviada para a fila '" + QUEUE_NAME + "'. Operação: " + operacao);
        } catch (Exception e) {
            // O erro original acontecia aqui. Agora não deve mais.
            System.err.println("ERRO: Falha ao serializar e publicar mensagem da API. " + e.getMessage());
        }
    }
}