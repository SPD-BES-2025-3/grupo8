package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Cargos;
import model.Emprestimo;
import model.Repositorio;
import model.Repositorios;
import model.Resenha;
import model.Usuario;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
/**
 * Controlador para a tela de CRUD de Usuários.
 *
 * @version 1.0
 */
public class UsuarioController extends AbstractPessoaController<model.Usuario, view.Usuario> implements Initializable {

    // --- Componentes FXML específicos de Usuario ---
    @FXML private TableColumn<view.Usuario, String> emailCol;
    @FXML private TableColumn<view.Usuario, String> cargoCol;
    @FXML private TableColumn<view.Usuario, Boolean> ativoCol;
    
    @FXML private TextField emailField;
    @FXML private PasswordField senhaField;
    @FXML private TextField telefoneField;
    @FXML private ComboBox<Cargos> cargoComboBox;
    @FXML private CheckBox isAtivoCheck;

    /**
     * Inicializa o controlador, mapeando colunas e populando ComboBoxes.
     *
     * @param location  A localização usada para resolver caminhos relativos para o objeto raiz, ou null se a localização não for conhecida.
     * @param resources Os recursos usados para localizar o objeto raiz, ou null se o objeto raiz não foi localizado.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Mapeia colunas herdadas e específicas
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomeCol.setCellValueFactory(new PropertyValueFactory<>("nome"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        cargoCol.setCellValueFactory(new PropertyValueFactory<>("cargoNome"));
        ativoCol.setCellValueFactory(new PropertyValueFactory<>("isAtivo"));

        cargoComboBox.setItems(FXCollections.observableArrayList(Repositorios.CARGO.loadAll()));
        super.initialize();
    }

    @Override
    protected Repositorio<Usuario, Integer> getRepositorio() {
        return Repositorios.USUARIO;
    }

    @Override
    protected view.Usuario modelToView(Usuario model) {
        String cargoNome = (model.getCargo() != null) ? model.getCargo().getName() : "N/D";
        return new view.Usuario(model.getId(), model.getNome(), model.getEmail(), model.getTelefone(), model.isAtivo(), cargoNome);
    }

    @Override
    protected Usuario viewToModel(Usuario usuario) {
        if (usuario == null) {
            usuario = new Usuario();
        }
        super.viewToModelComum(usuario);

        usuario.setEmail(emailField.getText());
        usuario.setTelefone(telefoneField.getText());
        if (senhaField.getText() != null && !senhaField.getText().isEmpty()) {
            usuario.setSenha(senhaField.getText());
        }
        usuario.setAtivo(isAtivoCheck.isSelected());
        usuario.setCargo(cargoComboBox.getSelectionModel().getSelectedItem());
        
        return usuario;
    }

    @Override
    protected void preencherCampos(view.Usuario usuarioView) {
        Usuario usuarioModel = getRepositorio().loadFromId(usuarioView.getId());
        
        super.preencherCamposComuns(usuarioModel);

        emailField.setText(usuarioModel.getEmail());
        telefoneField.setText(usuarioModel.getTelefone());
        senhaField.setText(usuarioModel.getSenha());
        isAtivoCheck.setSelected(usuarioModel.isAtivo());
        cargoComboBox.setValue(usuarioModel.getCargo());
    }

    @Override
    protected void limparCampos() {
        super.limparCamposComuns();
        emailField.clear();
        senhaField.clear();
        telefoneField.clear();
        cargoComboBox.getSelectionModel().clearSelection();
        isAtivoCheck.setSelected(false);
    }

    @Override
    protected void desabilitarCampos(boolean desabilitado) {
        super.desabilitarCamposComuns(desabilitado);
        emailField.setDisable(desabilitado);
        senhaField.setDisable(desabilitado);
        telefoneField.setDisable(desabilitado);
        cargoComboBox.setDisable(desabilitado);
        isAtivoCheck.setDisable(desabilitado);
    }

    @Override
    protected Integer getIdFromViewModel(view.Usuario viewModel) {
        return viewModel.getId();
    }

    @Override
    protected List<Control> getCamposObrigatorios() {
        return List.of(nomeField, emailField, senhaField);
    }

    /**
     * Deleta um usuário, removendo primeiro as associações com resenhas e empréstimos.
     * @return `true` se a operação for bem-sucedida, `false` caso contrário.
     */
    @Override
    protected boolean doDelete() {
        view.Usuario viewItem = tabela.getSelectionModel().getSelectedItem();
        if (viewItem == null) return false;

        try {
            Usuario modelItem = getRepositorio().loadFromId(getIdFromViewModel(viewItem));
            if (modelItem == null) return false;

            // 1. Deletar Resenhas associadas
            List<Resenha> resenhas = Repositorios.RESENHA.getDao().queryBuilder()
                .where().eq("usuario_id", modelItem.getId()).query();
            if (!resenhas.isEmpty()) {
                Repositorios.RESENHA.getDao().delete(resenhas);
            }

            // 2. Deletar Empréstimos associados
            List<Emprestimo> emprestimos = Repositorios.EMPRESTIMO.getDao().queryBuilder()
                .where().eq("usuario_id", modelItem.getId()).query();
            if (!emprestimos.isEmpty()) {
                Repositorios.EMPRESTIMO.getDao().delete(emprestimos);
            }

            // 3. Deletar o Usuário
            getRepositorio().delete(modelItem);
            return true;
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro ao Deletar o Usuário");
            alert.setHeaderText("Não foi possível excluir o usuário.");
            alert.setContentText("Causa: " + e.getMessage());
            alert.showAndWait();
            return false;
        }
    }
}