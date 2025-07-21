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
 * Controlador para a tela de CRUD de Empréstimos.
 *
 * @version 1.0
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
    @FXML private TextField statusField;

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
        dtEmprestimoCol.setCellValueFactory(new PropertyValueFactory<>("dtEmprestimo"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Popula os ComboBoxes
        livroComboBox.setItems(FXCollections.observableArrayList(Repositorios.LIVRO.loadAll()));
        usuarioComboBox.setItems(FXCollections.observableArrayList(Repositorios.USUARIO.loadAll()));

        super.initialize();
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
        emprestimo.setStatus(statusField.getText());

        // Converte LocalDate para Date
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
        statusField.setText(emprestimoModel.getStatus());

        // Converte Date para LocalDate
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
        statusField.clear();
    }

    @Override
    protected void desabilitarCampos(boolean desabilitado) {
        livroComboBox.setDisable(desabilitado);
        usuarioComboBox.setDisable(desabilitado);
        dtEmprestimoPicker.setDisable(desabilitado);
        dtPrevistaPicker.setDisable(desabilitado);
        dtRealPicker.setDisable(desabilitado);
        statusField.setDisable(desabilitado);
    }

    @Override
    protected Integer getIdFromViewModel(view.Emprestimo viewModel) {
        return viewModel.getId();
    }

    // --- Métodos utilitários para conversão de datas ---

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
}