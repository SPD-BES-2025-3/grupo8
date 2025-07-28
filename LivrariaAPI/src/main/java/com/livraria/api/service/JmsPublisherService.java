package com.livraria.api.service;

import com.livraria.api.model.Livro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import com.google.gson.Gson;

import javax.jms.TextMessage;

@Service
public class JmsPublisherService {

    // Fila de saída: da API para o Integrador
    private static final String QUEUE_NAME = "API_PARA_DESKTOP_QUEUE";

    @Autowired
    private JmsTemplate jmsTemplate;

    public void publicarMensagem(String operacao, Livro livro) {
        try {
            String jsonPayload = new Gson().toJson(livro);

            jmsTemplate.send(QUEUE_NAME, messageCreator -> {
                TextMessage message = messageCreator.createTextMessage(jsonPayload);
                message.setStringProperty("operacao", operacao);
                message.setStringProperty("entidade", "Livro");
                message.setStringProperty("idApi", livro.getId());
                return message;
            });
            System.out.println("[API Publisher] Mensagem JMS enviada para a fila '" + QUEUE_NAME + "'. Operação: " + operacao);
        } catch (Exception e) {
            System.err.println("ERRO: Falha ao publicar mensagem da API. " + e.getMessage());
        }
    }
}