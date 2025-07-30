package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import model.*;
import model.LivroAutor;
import model.Resenha;
import model.Emprestimo;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controlador para a tela de CRUD de Livros.
 * @version 1.4
 */
public class LivroController extends AbstractCrudController<model.Livro, view.Livro, Integer> implements Initializable {

    // ... (Declarações FXML)
    @FXML private TableColumn<view.Livro, Integer> idCol;
    @FXML private TableColumn<view.Livro, String> tituloCol;
    @FXML private TableColumn<view.Livro, String> autoresCol;
    @FXML private TableColumn<view.Livro, String> categoriaCol;
    @FXML private TableColumn<view.Livro, Integer> anoCol;
    @FXML private TextField idField, tituloField, isbnField, edicaoField, anoField, numPaginasField;
    @FXML private TextArea sinopseArea;
    @FXML private ComboBox<Categoria> categoriaComboBox;
    @FXML private ListView<Autor> autoresDisponiveisListView;
    @FXML private ListView<Autor> autoresAssociadosListView;
    @FXML private Button addAutorButton, removeAutorButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        tituloCol.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        autoresCol.setCellValueFactory(new PropertyValueFactory<>("autoresNomes"));
        categoriaCol.setCellValueFactory(new PropertyValueFactory<>("categoriaNome"));
        anoCol.setCellValueFactory(new PropertyValueFactory<>("anoPublicacao"));
        super.initialize();
    }

    @Override
    public void refreshView() {
        super.refreshView();
        
        Categoria selecionada = categoriaComboBox.getSelectionModel().getSelectedItem();
        ObservableList<Categoria> categorias = FXCollections.observableArrayList(Repositorios.CATEGORIA.loadAll());
        categoriaComboBox.setItems(categorias);
        if (selecionada != null) {
            categoriaComboBox.getSelectionModel().select(selecionada);
        }

        List<Autor> todosAutores = Repositorios.AUTOR.loadAll();
        List<Autor> autoresAssociados = new ArrayList<>(autoresAssociadosListView.getItems());
        List<Autor> autoresDisponiveis = todosAutores.stream()
            .filter(autor -> !autoresAssociados.contains(autor))
            .collect(Collectors.toList());
        autoresDisponiveisListView.setItems(FXCollections.observableArrayList(autoresDisponiveis));
    }

    /**
     * Sobrescreve o método de criação para tratar erros de forma correta.
     */
    @Override
    protected boolean doCreate() {
        try {
            Livro novoLivro = viewToModel(null);
            Livro livroSalvo = getRepositorio().create(novoLivro);
            if (livroSalvo == null) return false;
            
            salvarAutoresAssociados(livroSalvo);
            getRepositorio().getDao().refresh(livroSalvo);
            JmsPublisher.publicarMensagem("CREATE", livroSalvo);
            return true;
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erro ao Salvar Livro");
            alert.setHeaderText("Não foi possível criar o livro.");
            alert.setContentText("Causa: " + e.getMessage());
            alert.showAndWait();
            return false;
        }
    }

    /**
     * Sobrescreve o método de atualização para tratar erros de forma correta.
     */
    @Override
    protected boolean doUpdate() {
        view.Livro viewItem = tabela.getSelectionModel().getSelectedItem();
        if (viewItem == null) return false;
        try {
            Livro modelItem = getRepositorio().loadFromId(getIdFromViewModel(viewItem));
            modelItem = viewToModel(modelItem);
            getRepositorio().update(modelItem);

            salvarAutoresAssociados(modelItem);
            getRepositorio().getDao().refresh(modelItem);
            JmsPublisher.publicarMensagem("UPDATE", modelItem);
            return true;
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erro ao Atualizar Livro");
            alert.setHeaderText("Não foi possível atualizar o livro.");
            alert.setContentText("Causa: " + e.getMessage());
            alert.showAndWait();
            return false;
        }
    }

    /**
     * Sobrescreve o método de exclusão para tratar erros de forma correta.
     */
    @Override
    protected boolean doDelete() {
        view.Livro viewItem = tabela.getSelectionModel().getSelectedItem();
        if (viewItem == null) return false;

        try {
            Livro modelItem = getRepositorio().loadFromId(getIdFromViewModel(viewItem));
            if (modelItem == null) return false;

            JmsPublisher.publicarMensagem("DELETE", modelItem);

            // 1. Deletar associações em LivroAutor
            List<LivroAutor> associacoesAutores = new ArrayList<>(modelItem.getAutores());
            if (!associacoesAutores.isEmpty()) {
                Repositorios.LIVRO_AUTOR.getDao().delete(associacoesAutores);
            }

            // 2. Deletar Resenhas associadas
            List<Resenha> resenhas = Repositorios.RESENHA.getDao().queryBuilder()
                .where().eq("livro_id", modelItem.getId()).query();
            if (!resenhas.isEmpty()) {
                Repositorios.RESENHA.getDao().delete(resenhas);
            }

            // 3. Deletar Empréstimos associados
            List<Emprestimo> emprestimos = Repositorios.EMPRESTIMO.getDao().queryBuilder()
                .where().eq("livro_id", modelItem.getId()).query();
            if (!emprestimos.isEmpty()) {
                Repositorios.EMPRESTIMO.getDao().delete(emprestimos);
            }

            // 4. Deletar o Livro
            getRepositorio().delete(modelItem);
            return true;
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erro ao Deletar o Livro");
            alert.setHeaderText("Não foi possível excluir o livro.");
            alert.setContentText("Causa: " + e.getMessage());
            alert.showAndWait();
            return false;
        }
    }

    // ... (O resto da classe permanece o mesmo da versão anterior)
    private void salvarAutoresAssociados(Livro livro) throws SQLException {
        List<LivroAutor> associacoesAntigas = Repositorios.LIVRO_AUTOR.getDao().queryBuilder()
            .where().eq("livro_id", livro.getId()).query();
        if (associacoesAntigas != null && !associacoesAntigas.isEmpty()) {
            Repositorios.LIVRO_AUTOR.getDao().delete(associacoesAntigas);
        }
        for (Autor autor : autoresAssociadosListView.getItems()) {
            LivroAutor novaAssociacao = new LivroAutor(livro, autor);
            Repositorios.LIVRO_AUTOR.create(novaAssociacao);
        }
    }
    @Override
    protected Repositorio<Livro, Integer> getRepositorio() { return Repositorios.LIVRO; }
    @Override
    protected view.Livro modelToView(Livro model) {
        String categoriaNome = (model.getCategoria() != null) ? model.getCategoria().getNome() : "N/D";
        String autoresNomes = model.getAutores().stream()
                .map(la -> la.getAutor().getNome())
                .collect(Collectors.joining(", "));
        return new view.Livro(model.getId(), model.getTitulo(), categoriaNome, autoresNomes, model.getAnoPublicacao());
    }
    @Override
    protected Livro viewToModel(Livro livro) {
        if (livro == null) livro = new Livro();
        livro.setTitulo(tituloField.getText());
        livro.setIsbn(isbnField.getText());
        livro.setEdicao(edicaoField.getText());
        livro.setSinopse(sinopseArea.getText());
        livro.setAnoPublicacao(Integer.parseInt(anoField.getText()));
        livro.setNumPaginas(Integer.parseInt(numPaginasField.getText()));
        livro.setCategoria(categoriaComboBox.getSelectionModel().getSelectedItem());
        return livro;
    }
    @Override
    protected void preencherCampos(view.Livro livroView) {
        Livro livroModel = getRepositorio().loadFromId(livroView.getId());
        if (livroModel == null) {
            limparCampos();
            return;
        }
        idField.setText(String.valueOf(livroModel.getId()));
        tituloField.setText(livroModel.getTitulo());
        isbnField.setText(livroModel.getIsbn());
        edicaoField.setText(livroModel.getEdicao());
        anoField.setText(String.valueOf(livroModel.getAnoPublicacao()));
        numPaginasField.setText(String.valueOf(livroModel.getNumPaginas()));
        sinopseArea.setText(livroModel.getSinopse());
        categoriaComboBox.setValue(livroModel.getCategoria());
        List<Autor> todosAutores = Repositorios.AUTOR.loadAll();
        List<Autor> autoresAssociados = livroModel.getAutores().stream()
            .map(LivroAutor::getAutor).collect(Collectors.toList());
        autoresAssociadosListView.setItems(FXCollections.observableArrayList(autoresAssociados));
        List<Autor> autoresDisponiveis = todosAutores.stream()
            .filter(autor -> !autoresAssociados.contains(autor)).collect(Collectors.toList());
        autoresDisponiveisListView.setItems(FXCollections.observableArrayList(autoresDisponiveis));
    }
    @Override
    protected void limparCampos() {
        idField.clear();
        tituloField.clear();
        isbnField.clear();
        edicaoField.clear();
        anoField.clear();
        numPaginasField.clear();
        sinopseArea.clear();
        categoriaComboBox.getSelectionModel().clearSelection();
        autoresAssociadosListView.getItems().clear();
        autoresDisponiveisListView.setItems(FXCollections.observableArrayList(Repositorios.AUTOR.loadAll()));
    }
    @Override
    protected void desabilitarCampos(boolean desabilitado) {
        tituloField.setDisable(desabilitado);
        isbnField.setDisable(desabilitado);
        edicaoField.setDisable(desabilitado);
        anoField.setDisable(desabilitado);
        numPaginasField.setDisable(desabilitado);
        sinopseArea.setDisable(desabilitado);
        categoriaComboBox.setDisable(desabilitado);
        autoresDisponiveisListView.setDisable(desabilitado);
        autoresAssociadosListView.setDisable(desabilitado);
        addAutorButton.setDisable(desabilitado);
        removeAutorButton.setDisable(desabilitado);
    }
    @Override
    protected Integer getIdFromViewModel(view.Livro viewModel) {
        return viewModel.getId();
    }
    @FXML
    private void onAddAutor() {
        Autor selecionado = autoresDisponiveisListView.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            autoresDisponiveisListView.getItems().remove(selecionado);
            autoresAssociadosListView.getItems().add(selecionado);
        }
    }
    @FXML
    private void onRemoveAutor() {
        Autor selecionado = autoresAssociadosListView.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            autoresAssociadosListView.getItems().remove(selecionado);
            autoresDisponiveisListView.getItems().add(selecionado);
        }
    }

    @Override
    protected List<Control> getCamposObrigatorios() {
        return List.of(
            tituloField,
            anoField,
            numPaginasField,
            categoriaComboBox,
            autoresAssociadosListView
        );
    }
}