package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Categoria;
import model.Repositorio;
import model.Repositorios;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.control.Control;

/**
 * Controlador para a tela de CRUD de Categorias.
 *
 * @version 1.0
 */
public class CategoriaController extends AbstractCrudController<model.Categoria, view.Categoria, Integer> implements Initializable {

    // Componentes FXML injetados
    @FXML private TableColumn<view.Categoria, Integer> idCol;
    @FXML private TableColumn<view.Categoria, String> nomeCol;
    @FXML private TableColumn<view.Categoria, String> descricaoCol;
    
    @FXML private TextField idField;
    @FXML private TextField nomeField;
    @FXML private TextField descricaoField;

    /**
     * Inicializa o controlador, mapeando as colunas da tabela.
     *
     * @param location  A localização usada para resolver caminhos relativos para o objeto raiz, ou null se a localização não for conhecida.
     * @param resources Os recursos usados para localizar o objeto raiz, ou null se o objeto raiz não foi localizado.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Mapeia as colunas da tabela para as propriedades do ViewModel
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomeCol.setCellValueFactory(new PropertyValueFactory<>("nome"));
        descricaoCol.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        
        // Chama o inicializador da classe pai
        super.initialize();
    }

    @Override
    protected Repositorio<Categoria, Integer> getRepositorio() {
        return Repositorios.CATEGORIA;
    }

    @Override
    protected view.Categoria modelToView(Categoria model) {
        return new view.Categoria(model.getId(), model.getNome(), model.getDescricao());
    }

    @Override
    protected Categoria viewToModel(Categoria categoria) {
        if (categoria == null) {
            categoria = new Categoria();
        }
        categoria.setNome(nomeField.getText());
        categoria.setDescricao(descricaoField.getText());
        return categoria;
    }

    @Override
    protected void preencherCampos(view.Categoria categoria) {
        idField.setText(String.valueOf(categoria.getId()));
        nomeField.setText(categoria.getNome());
        descricaoField.setText(categoria.getDescricao());
    }

    @Override
    protected void limparCampos() {
        idField.clear();
        nomeField.clear();
        descricaoField.clear();
    }

    @Override
    protected void desabilitarCampos(boolean desabilitado) {
        nomeField.setDisable(desabilitado);
        descricaoField.setDisable(desabilitado);
    }

    @Override
    protected Integer getIdFromViewModel(view.Categoria viewModel) {
        return viewModel.getId();
    }

    @Override
    protected List<Control> getCamposObrigatorios() {
        return List.of(nomeField);
    }
}