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
import java.util.List;

/**
 * Controlador para a tela de CRUD de Empréstimos.
 *
 * @version 1.1
 */
public class EmprestimoController extends AbstractCrudController<model.Emprestimo, view.Emprestimo, Integer> implements Initializable {

    // Componentes FXML
    @FXML private TableColumn<view.Emprestimo, Integer> idCol;
    @FXML private TableColumn<view.Emprestimo, String> livroCol;
    @FXML private TableColumn<view.Emprestimo, String> usuarioCol;
    @FXML private TableColumn<view.Emprestimo, Date> dtEmprestimoCol;
    @FXML private TableColumn<view.Emprestimo, String> statusCol;

    @FXML private TextField idField;
    @FXML private ComboBox<Livro> livroComboBox;
    @FXML private ComboBox<Usuario> usuarioComboBox;
    @FXML private DatePicker dtEmprestimoPicker;
    @FXML private DatePicker dtPrevistaPicker;
    @FXML private DatePicker dtRealPicker;
    @FXML private ComboBox<String> statusComboBox;

    /**
     * Inicializa o controlador, mapeando colunas e populando ComboBoxes.
     *
     * @param location  A localização usada para resolver caminhos relativos para o objeto raiz.
     * @param resources Os recursos usados para localizar o objeto raiz.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        livroCol.setCellValueFactory(new PropertyValueFactory<>("livroTitulo"));
        usuarioCol.setCellValueFactory(new PropertyValueFactory<>("usuarioNome"));
        dtEmprestimoCol.setCellValueFactory(new PropertyValueFactory<>("dtEmprestimo"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Popula o ComboBox de status
        statusComboBox.setItems(FXCollections.observableArrayList("Disponível", "Emprestado"));

        super.initialize();
    }
    
    /**
     * Sobrescreve o método refreshView para também atualizar os ComboBoxes de Livros e Usuários.
     */
    @Override
    public void refreshView() {
        super.refreshView(); // Atualiza a tabela principal
        if (livroComboBox != null) {
            livroComboBox.setItems(FXCollections.observableArrayList(Repositorios.LIVRO.loadAll()));
        }
        if (usuarioComboBox != null && usuarioLogado != null) {
            if (!"Cliente".equals(usuarioLogado.getCargo().getName())) {
                usuarioComboBox.setItems(FXCollections.observableArrayList(Repositorios.USUARIO.loadAll()));
            }
        }
    }

    @Override
    protected Repositorio<Emprestimo, Integer> getRepositorio() {
        return Repositorios.EMPRESTIMO;
    }

    @Override
    protected view.Emprestimo modelToView(Emprestimo model) {
        String livroTitulo = (model.getLivro() != null) ? model.getLivro().getTitulo() : "N/D";
        String usuarioNome = (model.getUsuario() != null) ? model.getUsuario().getNome() : "N/D";
        
        return new view.Emprestimo(
            model.getId(),
            livroTitulo,
            usuarioNome,
            model.getDtEmprestimo(),
            model.getDtPrevistaDevolucao(),
            model.getStatus()
        );
    }

    @Override
    protected Emprestimo viewToModel(Emprestimo emprestimo) {
        if (emprestimo == null) {
            emprestimo = new Emprestimo();
        }
        emprestimo.setLivro(livroComboBox.getSelectionModel().getSelectedItem());
        emprestimo.setUsuario(usuarioComboBox.getSelectionModel().getSelectedItem());
        emprestimo.setStatus(statusComboBox.getSelectionModel().getSelectedItem());

        emprestimo.setDtEmprestimo(getDateFromPicker(dtEmprestimoPicker));
        emprestimo.setDtPrevistaDevolucao(getDateFromPicker(dtPrevistaPicker));
        emprestimo.setDtDevolucaoReal(getDateFromPicker(dtRealPicker));
        
        return emprestimo;
    }

    @Override
    protected void preencherCampos(view.Emprestimo emprestimoView) {
        Emprestimo emprestimoModel = getRepositorio().loadFromId(emprestimoView.getId());

        idField.setText(String.valueOf(emprestimoModel.getId()));
        livroComboBox.setValue(emprestimoModel.getLivro());
        usuarioComboBox.setValue(emprestimoModel.getUsuario());
        statusComboBox.setValue(emprestimoModel.getStatus());

        setPickerFromDate(dtEmprestimoPicker, emprestimoModel.getDtEmprestimo());
        setPickerFromDate(dtPrevistaPicker, emprestimoModel.getDtPrevistaDevolucao());
        setPickerFromDate(dtRealPicker, emprestimoModel.getDtDevolucaoReal());
    }

    @Override
    protected void limparCampos() {
        idField.clear();
        livroComboBox.getSelectionModel().clearSelection();
        usuarioComboBox.getSelectionModel().clearSelection();
        dtEmprestimoPicker.setValue(null);
        dtPrevistaPicker.setValue(null);
        dtRealPicker.setValue(null);
        statusComboBox.getSelectionModel().clearSelection();
    }

    @Override
    protected void desabilitarCampos(boolean desabilitado) {
        livroComboBox.setDisable(desabilitado);
        usuarioComboBox.setDisable(desabilitado);
        dtEmprestimoPicker.setDisable(desabilitado);
        dtPrevistaPicker.setDisable(desabilitado);
        dtRealPicker.setDisable(desabilitado);
        statusComboBox.setDisable(desabilitado);
    }

    @Override
    protected Integer getIdFromViewModel(view.Emprestimo viewModel) {
        return viewModel.getId();
    }

    /**
     * Converte o LocalDate de um DatePicker para um objeto Date.
     * @param datePicker O DatePicker a ser convertido.
     * @return O objeto Date correspondente, ou null se o DatePicker estiver vazio.
     */
    private Date getDateFromPicker(DatePicker datePicker) {
        if (datePicker.getValue() == null) {
            return null;
        }
        return Date.from(datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Define o valor de um DatePicker a partir de um objeto Date.
     * @param datePicker O DatePicker a ser definido.
     * @param date O objeto Date a ser usado.
     */
    private void setPickerFromDate(DatePicker datePicker, Date date) {
        if (date == null) {
            datePicker.setValue(null);
        } else {
            datePicker.setValue(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
    }

     @Override
    protected List<Control> getCamposObrigatorios() {
        return List.of(
            livroComboBox,
            usuarioComboBox,
            dtEmprestimoPicker,
            dtPrevistaPicker,
            statusComboBox
        );
    }

    /**
     * Informa à classe pai qual ComboBox deve ser gerenciado.
     */
    @Override
    protected ComboBox<Usuario> getUsuarioComboBox() {
        return usuarioComboBox;
    }

    /**
     * Informa à classe pai o nome da coluna para usar no filtro de cliente.
     */
    @Override
    protected String getUsuarioForeignKeyColumnName() {
        return "usuario_id";
    }
}