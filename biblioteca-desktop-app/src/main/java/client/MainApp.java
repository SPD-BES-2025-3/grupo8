package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Cargos;
import model.Repositorios;
import model.Usuario;
import controller.LoginController;

import java.sql.SQLException;
import java.util.List;

/**
 * Classe principal da aplicação que inicializa o JavaFX,
 * configura os dados iniciais e carrega a tela de login.
 *
 * @version 1.1
 */
public class MainApp extends Application {

    /**
     * Ponto de entrada da aplicação JavaFX.
     * @param primaryStage O palco principal da aplicação.
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            // --- LÓGICA DE INICIALIZAÇÃO DE DADOS ---
            setupInitialData();

            // --- CARREGAMENTO DA TELA DE LOGIN ---
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            VBox root = loader.load();

            // Passa o 'stage' para o LoginController para que ele possa abrir a próxima tela
            LoginController loginController = loader.getController();
            loginController.setStage(primaryStage);

            Scene scene = new Scene(root);
            primaryStage.setTitle("Login - Sistema de Biblioteca");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false); // Impede que a tela de login seja redimensionada
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro Crítico");
            alert.setHeaderText("Falha ao iniciar a aplicação.");
            alert.setContentText("Causa: " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Verifica e cria os dados iniciais (Cargos e usuário Admin) se não existirem.
     * @throws SQLException Se ocorrer um erro de SQL.
     */
    private void setupInitialData() throws SQLException {
        // 1. Cria os Cargos Padrão
        Cargos adminCargo = createCargoIfNotExists("Administrador");
        createCargoIfNotExists("Bibliotecario");
        createCargoIfNotExists("Cliente");

        // 2. Cria o Usuário Administrador Padrão
        // Verifica se já existe algum usuário com o email de admin
        List<Usuario> admins = Repositorios.USUARIO.getDao().queryForEq("email", "admin@admin");
        if (admins.isEmpty()) {
            System.out.println("Criando usuário administrador padrão...");
            Usuario adminUser = new Usuario();
            adminUser.setNome("Administrador Padrão");
            adminUser.setEmail("admin@admin");
            adminUser.setSenha("admin"); // Em um projeto real, isso seria um hash!
            adminUser.setAtivo(true);
            adminUser.setCargo(adminCargo); // Associa o cargo de Administrador
            
            Repositorios.USUARIO.create(adminUser);
            System.out.println("Usuário administrador criado com sucesso.");
        }
    }

    /**
     * Método auxiliar para criar um cargo se ele não existir no banco.
     * @param nome O nome do cargo a ser criado.
     * @return O objeto Cargo (novo ou existente).
     * @throws SQLException Se ocorrer um erro de SQL.
     */
    private Cargos createCargoIfNotExists(String nome) throws SQLException {
        // Tenta encontrar o cargo pelo nome
        List<Cargos> existing = Repositorios.CARGO.getDao().queryForEq("name", nome);
        if (existing.isEmpty()) {
            // Se não existir, cria um novo
            System.out.println("Criando cargo: " + nome);
            Cargos novoCargo = new Cargos();
            novoCargo.setName(nome);
            Repositorios.CARGO.create(novoCargo);
            return novoCargo;
        } else {
            // Se já existir, retorna o primeiro encontrado
            return existing.get(0);
        }
    }

    /**
     * Chamado quando a aplicação é fechada, garantindo que a conexão com o banco de dados seja encerrada.
     */
    @Override
    public void stop() {
        model.Repositorios.database.close();
        System.exit(0);
    }

    /**
     * Método principal que lança a aplicação.
     * @param args Argumentos de linha de comando.
     */
    public static void main(String[] args) {
        launch(args);
    }
}