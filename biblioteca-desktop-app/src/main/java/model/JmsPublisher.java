package model;

import com.google.gson.Gson;
import model.dto.LivroSyncDto; // Importe o novo DTO
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

public class JmsPublisher {

    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String QUEUE_NAME = "DESKTOP_PARA_API_QUEUE";

    public static void publicarMensagem(String operacao, Object entidade) {
        ConnectionFactory factory = new ActiveMQConnectionFactory("admin", "admin", BROKER_URL);
        try (Connection connection = factory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {

            Destination destination = session.createQueue(QUEUE_NAME);
            MessageProducer producer = session.createProducer(destination);

            Object objetoParaSerializar = entidade;

            // *** CORREÇÃO AQUI: Se a entidade for um Livro, use o DTO ***
            if (entidade instanceof Livro) {
                objetoParaSerializar = LivroSyncDto.fromLivro((Livro) entidade);
            }
            
            String jsonPayload = new Gson().toJson(objetoParaSerializar);
            
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
            // Imprime a causa raiz, que é mais informativa
            if (e.getCause() != null) {
                e.getCause().printStackTrace();
            } else {
                e.printStackTrace();
            }
        }
    }
}