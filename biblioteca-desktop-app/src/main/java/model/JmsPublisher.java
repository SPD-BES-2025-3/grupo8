package model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder; // Importe o GsonBuilder
import model.dto.LivroSyncDto;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

public class JmsPublisher {

    private static final String BROKER_URL = "tcp://localhost:61617";
    private static final String QUEUE_NAME = "DESKTOP_PARA_API_QUEUE";

    // *** CORREÇÃO AQUI: Criando uma instância do Gson configurada para evitar problemas de encoding ***
    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    public static void publicarMensagem(String operacao, Object entidade) {
        ConnectionFactory factory = new ActiveMQConnectionFactory("admin", "admin", BROKER_URL);
        try (Connection connection = factory.createConnection();
             Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {

            Destination destination = session.createQueue(QUEUE_NAME);
            MessageProducer producer = session.createProducer(destination);

            Object objetoParaSerializar = entidade;
            if (entidade instanceof Livro) {
                objetoParaSerializar = LivroSyncDto.fromLivro((Livro) entidade);
            }
            
            // Usa a instância do Gson já configurada
            String jsonPayload = gson.toJson(objetoParaSerializar);
            
            TextMessage message = session.createTextMessage(jsonPayload);
            
            message.setStringProperty("operacao", operacao);
            message.setStringProperty("entidade", entidade.getClass().getSimpleName());
            if (entidade instanceof Livro) {
                message.setIntProperty("idDesktop", ((Livro) entidade).getId());
            }

            producer.send(message);
            System.out.println("[Desktop Publisher] Mensagem JMS enviada para a fila '" + QUEUE_NAME + "'. Operação: " + operacao);

        } catch (Exception e) {
            System.err.println("ERRO: Falha ao publicar mensagem JMS. Verifique se o ActiveMQ está rodando.");
            e.printStackTrace();
        }
    }
}