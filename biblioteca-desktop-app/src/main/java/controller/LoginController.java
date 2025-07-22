package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Repositorios;
import model.Usuario;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Controlador para a tela de login.
 * @version 1.1
 */
public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField senhaField;
    @FXML private Label errorMessageLabel;

    private Stage stage;

    /**
     * Define o palco (Stage) da tela de login.
     * @param stage O palco principal da aplicação.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Lida com o evento de clique no botão de login.
     */
    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String senha = senhaField.getText();

        if (email.isEmpty() || senha.isEmpty()) {
            errorMessageLabel.setText("Email e senha são obrigatórios.");
            return;
        }

        try {
            Usuario usuarioAutenticado = autenticar(email, senha);

            if (usuarioAutenticado != null) {
                abrirTelaPrincipal(usuarioAutenticado);
            } else {
                errorMessageLabel.setText("Email ou senha inválidos.");
            }
        } catch (SQLException e) {
            errorMessageLabel.setText("Erro de banco de dados. Contate o suporte.");
            e.printStackTrace();
        }
    }

    /**
     * Autentica um usuário com base no email e senha.
     * @param email O email do usuário.
     * @param senha A senha do usuário.
     * @return O objeto Usuario se a autenticação for bem-sucedida, caso contrário, null.
     * @throws SQLException Se ocorrer um erro de SQL.
     */
    private Usuario autenticar(String email, String senha) throws SQLException {
        List<Usuario> usuarios = Repositorios.USUARIO.getDao().queryForEq("email", email);
        if (!usuarios.isEmpty()) {
            Usuario usuario = usuarios.get(0);
            if (usuario.getSenha().equals(senha) && usuario.isAtivo()) {
                return usuario;
            }
        }
        return null;
    }

    /**
     * Abre a tela principal da aplicação após o login bem-sucedido.
     * @param usuario O usuário autenticado.
     */
    private void abrirTelaPrincipal(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/app.fxml"));
            VBox root = loader.load();

            // Passa o usuário logado para o AppController usando o novo método initData
            AppController appController = loader.getController();
            appController.initData(usuario);

            Scene scene = new Scene(root);
            stage.setTitle("Sistema de Gestão de Biblioteca - Logado como: " + usuario.getNome());
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            errorMessageLabel.setText("Erro ao carregar a tela principal.");
        }
    }
}