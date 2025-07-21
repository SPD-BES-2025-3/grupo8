package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.*;

import java.net.URL;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Controlador para a tela de CRUD de Resenhas.
 *
 * @version 1.0
 */
public class ResenhaController extends AbstractCrudController<model.Resenha, view.Resenha, Integer> implements Initializable {

    // Componentes FXML
    @FXML private TableColumn<view.Resenha, Integer> idCol;
    @FXML private TableColumn<view.Resenha, String> livroCol;
    @FXML private TableColumn<view.Resenha, String> usuarioCol;
    @FXML private TableColumn<view.Resenha, Integer> notaCol;
    @FXML private TableColumn<view.Resenha, String> textoCol;
    
    @FXML private TextField idField;
    @FXML private ComboBox<Livro> livroComboBox;
    @FXML private ComboBox<Usuario> usuarioComboBox;
    @FXML private DatePicker dtAvaliacaoPicker;
    @FXML private TextField notaField;
    @FXML private TextArea textoArea;

    /**
     * Inicializa o controlador, mapeando colunas e populando ComboBoxes.
     *
     * @param location  A localização usada para resolver caminhos relativos para o objeto raiz, ou null se a localização não for conhecida.
     * @param resources Os recursos usados para localizar o objeto raiz, ou null se o objeto raiz não foi localizado.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Mapeia colunas da tabela
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        livroCol.setCellValueFactory(new PropertyValueFactory<>("livroTitulo"));
        usuarioCol.setCellValueFactory(new PropertyValueFactory<>("usuarioNome"));
        notaCol.setCellValueFactory(new PropertyValueFactory<>("nota"));
        textoCol.setCellValueFactory(new PropertyValueFactory<>("texto"));

        // Popula os ComboBoxes
        livroComboBox.setItems(FXCollections.observableArrayList(Repositorios.LIVRO.loadAll()));
        usuarioComboBox.setItems(FXCollections.observableArrayList(Repositorios.USUARIO.loadAll()));

        super.initialize();
    }

    @Override
    protected Repositorio<Resenha, Integer> getRepositorio() {
        return Repositorios.RESENHA;
    }

    @Override
    protected view.Resenha modelToView(Resenha model) {
        String livroTitulo = (model.getLivro() != null) ? model.getLivro().getTitulo() : "N/D";
        String usuarioNome = (model.getUsuario() != null) ? model.getUsuario().getNome() : "N/D";
        String textoPreview = (model.getTexto() != null && model.getTexto().length() > 50) ? model.getTexto().substring(0, 50) + "..." : model.getTexto();
        
        return new view.Resenha(model.getId(), livroTitulo, usuarioNome, model.getNota(), textoPreview);
    }

    @Override
    protected Resenha viewToModel(Resenha resenha) {
        if (resenha == null) {
            resenha = new Resenha();
        }
        resenha.setLivro(livroComboBox.getSelectionModel().getSelectedItem());
        resenha.setUsuario(usuarioComboBox.getSelectionModel().getSelectedItem());
        resenha.setTexto(textoArea.getText());
        
        try {
            resenha.setNota(Integer.parseInt(notaField.getText()));
        } catch (NumberFormatException e) {
            resenha.setNota(0); // Valor padrão
        }

        if (dtAvaliacaoPicker.getValue() != null) {
            resenha.setDtAvaliacao(Date.from(dtAvaliacaoPicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        } else {
            resenha.setDtAvaliacao(new Date()); // Usa a data atual se nenhuma for selecionada
        }
        return resenha;
    }

    @Override
    protected void preencherCampos(view.Resenha resenhaView) {
        Resenha resenhaModel = getRepositorio().loadFromId(resenhaView.getId());

        idField.setText(String.valueOf(resenhaModel.getId()));
        livroComboBox.setValue(resenhaModel.getLivro());
        usuarioComboBox.setValue(resenhaModel.getUsuario());
        notaField.setText(String.valueOf(resenhaModel.getNota()));
        textoArea.setText(resenhaModel.getTexto());

        if (resenhaModel.getDtAvaliacao() != null) {
            dtAvaliacaoPicker.setValue(resenhaModel.getDtAvaliacao().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        } else {
            dtAvaliacaoPicker.setValue(null);
        }
    }

    @Override
    protected void limparCampos() {
        idField.clear();
        livroComboBox.getSelectionModel().clearSelection();
        usuarioComboBox.getSelectionModel().clearSelection();
        dtAvaliacaoPicker.setValue(null);
        notaField.clear();
        textoArea.clear();
    }

    @Override
    protected void desabilitarCampos(boolean desabilitado) {
        livroComboBox.setDisable(desabilitado);
        usuarioComboBox.setDisable(desabilitado);
        dtAvaliacaoPicker.setDisable(desabilitado);
        notaField.setDisable(desabilitado);
        textoArea.setDisable(desabilitado);
    }

    @Override
    protected Integer getIdFromViewModel(view.Resenha viewModel) {
        return viewModel.getId();
    }
}