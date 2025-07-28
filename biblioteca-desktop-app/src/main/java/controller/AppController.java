package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import model.Usuario;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controlador principal da aplicação que gerencia as abas
 * e as exibe com base no cargo do usuário logado.
 *
 * @version 1.5
 */
public class AppController {
    
    @FXML private TabPane tabPane;
    @FXML private Tab tabEmprestimos;
    @FXML private Tab tabLivros;
    @FXML private Tab tabResenhas;
    @FXML private Tab tabUsuarios;
    @FXML private Tab tabAutores;
    @FXML private Tab tabCategorias;
    @FXML private Tab tabCargos;

    /**
     * Recebe o usuário logado, define a variável estática e configura a UI.
     * @param usuario O usuário autenticado.
     */
    public void initData(Usuario usuario) {
        AbstractCrudController.usuarioLogado = usuario;
        
        Platform.runLater(() -> {
            configurarVisibilidadeAbas(usuario);
            carregarAbasVisiveis();
        });
    }

    /**
     * Configura a visibilidade das abas com base no cargo do usuário.
     * @param usuario O usuário logado.
     */
    private void configurarVisibilidadeAbas(Usuario usuario) {
        // Bloco de verificação melhorado para diagnóstico
        if (tabPane == null) {
            System.err.println("Erro Crítico: O componente tabPane do FXML não foi injetado. Verifique se o fx:id=\"tabPane\" está correto no arquivo app.fxml.");
            return;
        }
        if (usuario == null) {
            System.err.println("Erro Crítico: O objeto 'usuario' recebido após o login está nulo.");
            return;
        }
        if (usuario.getCargo() == null) {
            System.err.println("Erro de Dados: O usuário '" + usuario.getNome() + "' não possui um cargo associado no banco de dados.");
            return;
        }

        String cargo = usuario.getCargo().getName();
        List<Tab> abasParaRemover = new ArrayList<>();

        switch (cargo) {
            case "Bibliotecario":
                abasParaRemover.add(tabUsuarios);
                break;
            case "Cliente":
                abasParaRemover.add(tabUsuarios);
                abasParaRemover.add(tabLivros);
                abasParaRemover.add(tabAutores);
                abasParaRemover.add(tabCategorias);
                break;
            case "Administrador":
                // Nenhuma aba para remover
                break;
            default:
                // Se o cargo não for reconhecido, remove todas as abas por segurança
                abasParaRemover.addAll(tabPane.getTabs());
                break;
        }
        tabPane.getTabs().removeAll(abasParaRemover);
    }

    /**
     * Carrega o conteúdo FXML apenas para as abas que permaneceram visíveis.
     */
    private void carregarAbasVisiveis() {
        if (tabPane == null) return;
        for (Tab tab : tabPane.getTabs()) {
            try {
                if (tab.getContent() == null) {
                    switch (tab.getId()) {
                        case "tabEmprestimos": carregarTab(tab, "/view/emprestimo.fxml"); break;
                        case "tabLivros":      carregarTab(tab, "/view/livro.fxml"); break;
                        case "tabResenhas":    carregarTab(tab, "/view/resenha.fxml"); break;
                        case "tabUsuarios":    carregarTab(tab, "/view/usuario.fxml"); break;
                        case "tabAutores":     carregarTab(tab, "/view/autor.fxml"); break;
                        case "tabCategorias":  carregarTab(tab, "/view/categoria.fxml"); break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Carrega o conteúdo de uma aba a partir de um arquivo FXML.
     * @param tab A aba a ser carregada.
     * @param fxmlPath O caminho para o arquivo FXML.
     * @throws IOException Se ocorrer um erro ao carregar o FXML.
     */
    private void carregarTab(Tab tab, String fxmlPath) throws IOException {
        URL resource = getClass().getResource(fxmlPath);
        if (resource == null) {
            System.err.println("Arquivo FXML não encontrado: " + fxmlPath);
            return;
        }
        Parent content = FXMLLoader.load(resource);
        tab.setContent(content);
    }

    @FXML
    private void handleRefresh() {
        AbstractCrudController.refreshAllViews();
    }
}