package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador principal da aplicação que gerencia as abas
 * e carrega os FXMLs correspondentes a cada uma.
 *
 * @version 1.0
 */
public class AppController implements Initializable {
    // Declaração de todas as abas definidas no FXML
    @FXML private Tab tabEmprestimos;
    @FXML private Tab tabLivros;
    @FXML private Tab tabResenhas;
    @FXML private Tab tabUsuarios;
    @FXML private Tab tabAutores;
    @FXML private Tab tabCategorias;
    @FXML private Tab tabCargos;

    /**
     * Inicializa o controlador, carregando cada FXML em sua respectiva aba.
     *
     * @param location  A localização usada para resolver caminhos relativos para o objeto raiz, ou null se a localização não for conhecida.
     * @param resources Os recursos usados para localizar o objeto raiz, ou null se o objeto raiz não foi localizado.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Carrega cada FXML em sua respectiva aba
        try {
            carregarTab(tabUsuarios, "/view/usuario.fxml");
            carregarTab(tabAutores, "/view/autor.fxml");
            carregarTab(tabCategorias, "/view/categoria.fxml");
            carregarTab(tabLivros, "/view/livro.fxml");
            carregarTab(tabResenhas, "/view/resenha.fxml");
            carregarTab(tabEmprestimos, "/view/emprestimo.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método auxiliar para carregar um arquivo FXML dentro de uma aba.
     * @param tab A aba onde o conteúdo será inserido.
     * @param fxmlPath O caminho para o arquivo FXML a ser carregado.
     * @throws IOException Se o arquivo FXML não for encontrado.
     */
    private void carregarTab(Tab tab, String fxmlPath) throws IOException {
        URL fxmlLocation = getClass().getResource(fxmlPath);
        if (fxmlLocation == null) {
            throw new IOException("Não foi possível encontrar o arquivo FXML: " + fxmlPath);
        }
        Parent content = FXMLLoader.load(fxmlLocation);
        tab.setContent(content);
    }
}