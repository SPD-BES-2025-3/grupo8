package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.*;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controlador para a tela de CRUD de Livros, com lógica especial
 * para a relação Muitos-para-Muitos com Autores.
 *
 * @version 1.0
 */
public class LivroController extends AbstractCrudController<model.Livro, view.Livro, Integer> implements Initializable {

    // Componentes FXML específicos de Livro
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

    /**
     * Inicializa o controlador, mapeando colunas e carregando dados iniciais.
     *
     * @param location  A localização usada para resolver caminhos relativos para o objeto raiz, ou null se a localização não for conhecida.
     * @param resources Os recursos usados para localizar o objeto raiz, ou null se o objeto raiz não foi localizado.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        tituloCol.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        autoresCol.setCellValueFactory(new PropertyValueFactory<>("autoresNomes"));
        categoriaCol.setCellValueFactory(new PropertyValueFactory<>("categoriaNome"));
        anoCol.setCellValueFactory(new PropertyValueFactory<>("anoPublicacao"));

        categoriaComboBox.setItems(FXCollections.observableArrayList(Repositorios.CATEGORIA.loadAll()));
        autoresDisponiveisListView.setItems(FXCollections.observableArrayList(Repositorios.AUTOR.loadAll()));
        
        super.initialize();
    }

    @Override
    protected Repositorio<Livro, Integer> getRepositorio() {
        return Repositorios.LIVRO;
    }

    @Override
    protected view.Livro modelToView(Livro model) {
        String categoriaNome = (model.getCategoria() != null) ? model.getCategoria().getNome() : "N/D";

        // Verifica se a coleção de autores não é nula antes de processá-la
        String autoresNomes = "";
        if (model.getAutores() != null) {
            autoresNomes = model.getAutores().stream()
                    .map(livroAutor -> livroAutor.getAutor().getNome())
                    .collect(Collectors.joining(", "));
        }

        return new view.Livro(model.getId(), model.getTitulo(), categoriaNome, autoresNomes, model.getAnoPublicacao());
    }

    @Override
    protected Livro viewToModel(Livro livro) {
        if (livro == null) {
            livro = new Livro();
        }
        livro.setTitulo(tituloField.getText());
        livro.setIsbn(isbnField.getText());
        livro.setEdicao(edicaoField.getText());
        livro.setSinopse(sinopseArea.getText());
        try {
            livro.setAnoPublicacao(Integer.parseInt(anoField.getText()));
            livro.setNumPaginas(Integer.parseInt(numPaginasField.getText()));
        } catch (NumberFormatException e){
            livro.setAnoPublicacao(0);
            livro.setNumPaginas(0);
        }
        livro.setCategoria(categoriaComboBox.getSelectionModel().getSelectedItem());
        
        return livro;
    }
    
    /**
     * Sobrescreve o método `doCreate` para lidar com a relação N:M com Autores.
     */
    @Override
    protected boolean doCreate() {
        try {
            Livro novoLivro = viewToModel(null);
            Livro livroSalvo = getRepositorio().create(novoLivro);
            
            salvarAutoresAssociados(livroSalvo);
            
            // Recarrega o livro do banco para garantir que a coleção de autores esteja populada
            Livro livroCompleto = getRepositorio().loadFromId(livroSalvo.getId());
            
            tabela.getItems().add(modelToView(livroCompleto));
            tabela.getSelectionModel().selectLast();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erro ao Salvar: " + e.getMessage()).show();
        }
        return true;
    }
    
    /**
     * Sobrescreve o método `doUpdate` para lidar com a relação N:M com Autores.
     */
    @Override
    protected boolean doUpdate() {
        view.Livro viewItem = tabela.getSelectionModel().getSelectedItem();
        try {
            Livro modelItem = getRepositorio().loadFromId(getIdFromViewModel(viewItem));
            modelItem = viewToModel(modelItem);
            getRepositorio().update(modelItem);

            salvarAutoresAssociados(modelItem);
            
            // Recarrega o livro do banco para garantir que as associações estejam atualizadas na view
            Livro livroCompleto = getRepositorio().loadFromId(modelItem.getId());
            
            int index = tabela.getItems().indexOf(viewItem);
            tabela.getItems().set(index, modelToView(livroCompleto));
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erro ao Atualizar: " + e.getMessage()).show();
        }
        return true;
    }

    /**
     * Salva as associações entre o livro e os autores selecionados.
     * @param livro O livro a ser associado.
     * @throws SQLException Se ocorrer um erro de SQL.
     */
    private void salvarAutoresAssociados(Livro livro) throws SQLException {
        List<LivroAutor> associacoesAntigas = Repositorios.LIVRO_AUTOR.getDao().queryBuilder()
            .where().eq("livro_id", livro.getId()).query();
        if (associacoesAntigas != null && !associacoesAntigas.isEmpty()){
            Repositorios.LIVRO_AUTOR.getDao().delete(associacoesAntigas);
        }

        for (Autor autor : autoresAssociadosListView.getItems()) {
            LivroAutor novaAssociacao = new LivroAutor(livro, autor);
            Repositorios.LIVRO_AUTOR.create(novaAssociacao);
        }
    }

    @Override
    protected void preencherCampos(view.Livro livroView) {
        Livro livroModel = getRepositorio().loadFromId(livroView.getId());
        
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
        
        ObservableList<Autor> autoresDisponiveis = FXCollections.observableArrayList();
        for(Autor autor : todosAutores){
            boolean encontrado = false;
            for(Autor associado : autoresAssociados){
                if (autor.getId() == associado.getId()){
                    encontrado = true;
                    break;
                }
            }
            if(!encontrado){
                autoresDisponiveis.add(autor);
            }
        }
        autoresDisponiveisListView.setItems(autoresDisponiveis);
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

    /**
     * Ação do botão "Adicionar Autor". Move um autor da lista de disponíveis para associados.
     */
    @FXML
    private void onAddAutor() {
        Autor selecionado = autoresDisponiveisListView.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            autoresDisponiveisListView.getItems().remove(selecionado);
            autoresAssociadosListView.getItems().add(selecionado);
        }
    }

    /**
     * Ação do botão "Remover Autor". Move um autor da lista de associados para disponíveis.
     */
    @FXML
    private void onRemoveAutor() {
        Autor selecionado = autoresAssociadosListView.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            autoresAssociadosListView.getItems().remove(selecionado);
            autoresDisponiveisListView.getItems().add(selecionado);
        }
    }

    /**
     * Sobrescreve o método refreshView para também atualizar os ComboBoxes e ListViews,
     * preservando a seleção do usuário sempre que possível.
     */
    @Override
    public void refreshView() {
        super.refreshView(); // Atualiza a tabela principal

        // --- Atualiza ComboBox de Categoria ---
        if (categoriaComboBox != null) {
            Categoria selecionada = categoriaComboBox.getSelectionModel().getSelectedItem();
            ObservableList<Categoria> categorias = FXCollections.observableArrayList(Repositorios.CATEGORIA.loadAll());
            categoriaComboBox.setItems(categorias);
            // Tenta restaurar a seleção
            if (selecionada != null) {
                categoriaComboBox.getSelectionModel().select(selecionada);
            }
        }

        // --- Atualiza ListView de Autores ---
        if (autoresDisponiveisListView != null) {
            List<Autor> todosAutores = Repositorios.AUTOR.loadAll();
            List<Autor> autoresAssociados = new ArrayList<>(autoresAssociadosListView.getItems());
            
            List<Autor> autoresDisponiveis = todosAutores.stream()
                .filter(autor -> !autoresAssociados.contains(autor))
                .collect(Collectors.toList());
            autoresDisponiveisListView.setItems(FXCollections.observableArrayList(autoresDisponiveis));
        }
    }
}