package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;
import model.Repositorio;
import model.Repositorios;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.Usuario;

/**
 * Controlador abstrato que implementa a lógica CRUD (Create, Read, Update, Delete)
 * com validação genérica de campos e atualização automática entre telas.
 *
 * @param <E>  O tipo da entidade do modelo (ex: Livro, Autor).
 * @param <V>  O tipo do ViewModel para a tabela (ex: view.Livro, view.Autor).
 * @param <ID> O tipo do ID da entidade (ex: Integer).
 * @version 1.5
 */
public abstract class AbstractCrudController<E, V, ID> {

    /**
     * Lista estática que rastreia todas as instâncias ativas de controladores CRUD.
     * Usada para notificar todas as telas sobre atualizações de dados.
     */
    private static final List<AbstractCrudController<?, ?, ?>> activeControllers = new ArrayList<>();
    protected static Usuario usuarioLogado;
    /**
     * Enum para controlar a ação pendente do usuário (Criar, Atualizar, Deletar).
     */
    private enum Action { NONE, NOVO, ATUALIZAR, DELETAR }
    private Action pendingAction = Action.NONE;

    @FXML protected TableView<V> tabela;
    @FXML protected Button novoButton;
    @FXML protected Button atualizarButton;
    @FXML protected Button deletarButton;
    @FXML protected Button confirmarButton;
    @FXML protected Button cancelarButton;
    @FXML protected Text confirmacaoText;

    // --- MÉTODOS ABSTRATOS (a serem implementados pelas classes filhas) ---

    /**
     * Retorna o repositório específico para a entidade.
     *
     * @return O repositório.
     */
    protected abstract Repositorio<E, ID> getRepositorio();

    /**
     * Converte um objeto do modelo para um objeto do view model.
     *
     * @param entidade A entidade do modelo.
     * @return O view model.
     */
    protected abstract V modelToView(E entidade);

    /**
     * Converte um objeto do view model para um objeto do modelo.
     *
     * @param entidade A entidade do modelo.
     * @return O modelo.
     */
    protected abstract E viewToModel(E entidade);

    /**
     * Preenche os campos da tela com os dados de um item.
     *
     * @param item O item a ser exibido.
     */
    protected abstract void preencherCampos(V item);

    /**
     * Limpa todos os campos da tela.
     */
    protected abstract void limparCampos();

    /**
     * Habilita ou desabilita os campos da tela.
     *
     * @param desabilitado `true` para desabilitar, `false` para habilitar.
     */
    protected abstract void desabilitarCampos(boolean desabilitado);

    /**
     * Retorna o ID de um view model.
     *
     * @param viewModel O view model.
     * @return O ID.
     */
    protected abstract ID getIdFromViewModel(V viewModel);

    /**
     * Retorna uma lista de controles (TextFields, ComboBoxes, etc.)
     * que são de preenchimento obrigatório para a validação.
     * @return Uma lista de controles a serem validados.
     */
    protected abstract List<Control> getCamposObrigatorios();

    /**
     * Método opcional para ser sobrescrito por controladores que possuem um ComboBox de usuário.
     * @return O ComboBox de usuário da tela, ou null se não houver um.
     */
    protected ComboBox<Usuario> getUsuarioComboBox() {
        return null;
    }

    /**
     * Método opcional para ser sobrescrito por controladores que precisam de filtro por usuário.
     * @return O nome da coluna de chave estrangeira do usuário (ex: "usuario_id").
     */
    protected String getUsuarioForeignKeyColumnName() {
        return null;
    }

    /**
     * Inicializa o controlador, adicionando-o à lista de controladores ativos,
     * configurando listeners e carregando os dados iniciais.
     */
    public void initialize() {
        if (!activeControllers.contains(this)) {
            activeControllers.add(this);
        }
        tabela.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> handleItemSelected(newSelection));

        configurarInterfacePorUsuario();
        refreshView();
        resetToInitialState();
    }

     /**
     * Configura componentes específicos da interface (como o ComboBox de usuário)
     * com base no perfil do usuário logado.
     */
    private void configurarInterfacePorUsuario() {
        ComboBox<Usuario> usuarioComboBox = getUsuarioComboBox();
        if (usuarioComboBox != null && usuarioLogado != null) {
            if ("Cliente".equals(usuarioLogado.getCargo().getName())) {
                usuarioComboBox.setItems(FXCollections.observableArrayList(usuarioLogado));
                usuarioComboBox.getSelectionModel().select(usuarioLogado);
                usuarioComboBox.setDisable(true);
            } else {
                usuarioComboBox.setDisable(false);
            }
        }
    }

    /**
     * Recarrega os dados da tabela a partir do banco de dados.
     * Este método pode ser sobrescrito para atualizar outros componentes, como ComboBoxes.
     */
    public void refreshView() {
        if (tabela != null) {
            tabela.setItems(loadAllItems());
            tabela.refresh();
        }

        // Atualiza a lista de usuários para Admin/Bibliotecário
        ComboBox<Usuario> usuarioComboBox = getUsuarioComboBox();
        if (usuarioComboBox != null && usuarioLogado != null && !"Cliente".equals(usuarioLogado.getCargo().getName())) {
            Usuario selecionado = usuarioComboBox.getSelectionModel().getSelectedItem();
            usuarioComboBox.setItems(FXCollections.observableArrayList(Repositorios.USUARIO.loadAll()));
            if (selecionado != null) {
                usuarioComboBox.getSelectionModel().select(selecionado);
            }
        }
    }

    /**
     * Valida uma lista de controles para garantir que não estão vazios.
     * @return {@code true} se todos os campos forem válidos, {@code false} caso contrário.
     */
    private boolean validarCampos() {
        for (Control control : getCamposObrigatorios()) {
            boolean campoInvalido = false;
            if (control instanceof TextInputControl && ((TextInputControl) control).getText().trim().isEmpty()) {
                campoInvalido = true;
            } else if (control instanceof ComboBox && ((ComboBox<?>) control).getSelectionModel().isEmpty()) {
                campoInvalido = true;
            } else if (control instanceof ListView && ((ListView<?>) control).getItems().isEmpty()) {
                campoInvalido = true;
            }

            if (campoInvalido) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Erro de Validação");
                alert.setHeaderText("Campos Obrigatórios Vazios");
                alert.setContentText("Por favor, preencha todos os campos necessários antes de confirmar.");
                alert.showAndWait();
                return false;
            }
        }
        return true;
    }

    @FXML
    protected void onConfirmarButtonAction() {
        if (pendingAction == Action.NOVO || pendingAction == Action.ATUALIZAR) {
            if (!validarCampos()) {
                return; // Aborta a operação se a validação falhar
            }
        }

        boolean success = false;
        switch (pendingAction) {
            case NOVO:      success = doCreate(); break;
            case ATUALIZAR: success = doUpdate(); break;
            case DELETAR:   success = doDelete(); break;
            default: break;
        }

        if (success) {
            for (AbstractCrudController<?, ?, ?> controller : activeControllers) {
                controller.refreshView();
            }
        }

        resetToInitialState();
    }

    @FXML
    protected void onNovoButtonAction() {
        tabela.getSelectionModel().clearSelection();
        limparCampos();
        setPendingAction(Action.NOVO);
    }

    @FXML
    protected void onAtualizarButtonAction() {
        if (tabela.getSelectionModel().getSelectedItem() != null) {
            setPendingAction(Action.ATUALIZAR);
        }
    }

    @FXML
    protected void onDeletarButtonAction() {
        if (tabela.getSelectionModel().getSelectedItem() != null) {
            setPendingAction(Action.DELETAR);
        }
    }

    @FXML
    protected void onCancelarButtonAction() {
        resetToInitialState();
    }

    /**
     * Executa a ação de criar um novo registro.
     * @return `true` se a operação for bem-sucedida, `false` caso contrário.
     */
    protected boolean doCreate() {
        try {
            E novaEntidade = viewToModel(null);
            E entidadeSalva = getRepositorio().create(novaEntidade);
            return entidadeSalva != null;
        } catch (Exception e) {
            new Alert(AlertType.ERROR, "Erro ao Salvar: " + e.getMessage()).showAndWait();
            return false;
        }
    }

    /**
     * Executa a ação de atualizar um registro.
     * @return `true` se a operação for bem-sucedida, `false` caso contrário.
     */
    protected boolean doUpdate() {
        V viewItem = tabela.getSelectionModel().getSelectedItem();
        try {
            E modelItem = getRepositorio().loadFromId(getIdFromViewModel(viewItem));
            modelItem = viewToModel(modelItem);
            getRepositorio().update(modelItem);
            return true;
        } catch (Exception e) {
            new Alert(AlertType.ERROR, "Erro ao Atualizar: " + e.getMessage()).showAndWait();
            return false;
        }
    }

    /**
     * Executa a ação de deletar um registro.
     * @return `true` se a operação for bem-sucedida, `false` caso contrário.
     */
    protected boolean doDelete() {
        V viewItem = tabela.getSelectionModel().getSelectedItem();
        try {
            E modelItem = getRepositorio().loadFromId(getIdFromViewModel(viewItem));
            if (modelItem == null) return false;
            getRepositorio().delete(modelItem);
            return true;
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erro ao Deletar");
            alert.setHeaderText("Não foi possível excluir o item.");
            alert.setContentText("Verifique se ele não está sendo usado em outro cadastro (ex: um autor associado a um livro).");
            alert.showAndWait();
            return false;
        }
    }

    /**
     * Carrega todos os itens, aplicando o filtro de "Cliente" quando aplicável.
     * Esta lógica agora é centralizada e genérica.
     * @return A lista de itens.
     */
    protected ObservableList<V> loadAllItems() {
        ObservableList<V> lista = FXCollections.observableArrayList();
        List<E> listaDoBanco;
        String userColumn = getUsuarioForeignKeyColumnName();

        try {
            if (usuarioLogado != null && "Cliente".equals(usuarioLogado.getCargo().getName()) && userColumn != null) {
                listaDoBanco = getRepositorio().getDao().queryBuilder()
                    .where().eq(userColumn, usuarioLogado.getId()).query();
            } else {
                listaDoBanco = getRepositorio().loadAll();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            listaDoBanco = Collections.emptyList();
        }

        for (E itemModel : listaDoBanco) {
            lista.add(modelToView(itemModel));
        }
        return lista;
    }

    /**
     * Define a ação pendente e atualiza a interface.
     * @param action A ação a ser definida.
     */
    private void setPendingAction(Action action) {
        pendingAction = action;
        boolean isConfirmationMode = (action != Action.NONE);
        novoButton.setDisable(isConfirmationMode);
        atualizarButton.setDisable(isConfirmationMode || tabela.getSelectionModel().getSelectedItem() == null);
        deletarButton.setDisable(isConfirmationMode || tabela.getSelectionModel().getSelectedItem() == null);
        confirmarButton.setDisable(!isConfirmationMode);
        cancelarButton.setDisable(!isConfirmationMode);
        desabilitarCampos(action != Action.NOVO && action != Action.ATUALIZAR);
        switch(action) {
            case NOVO:      confirmacaoText.setText("Ação Pendente: Criar novo item"); break;
            case ATUALIZAR: confirmacaoText.setText("Ação Pendente: Atualizar item"); break;
            case DELETAR:   confirmacaoText.setText("Ação Pendente: DELETAR item!"); break;
            case NONE:      confirmacaoText.setText("Ação Pendente: Nenhuma"); break;
        }
    }

    /**
     * Reseta o estado inicial da tela.
     */
    private void resetToInitialState() {
        setPendingAction(Action.NONE);
        handleItemSelected(tabela.getSelectionModel().getSelectedItem());
        if (tabela.getSelectionModel().getSelectedItem() == null) {
            limparCampos();
            desabilitarCampos(true);
        }
    }

    /**
     * Lida com a seleção de um item na tabela.
     * @param item O item selecionado.
     */
    private void handleItemSelected(V item) {
        if (pendingAction != Action.NONE) return;
        if (item != null) {
            preencherCampos(item);
            atualizarButton.setDisable(false);
            deletarButton.setDisable(false);
            desabilitarCampos(true);
        } else {
            limparCampos();
            atualizarButton.setDisable(true);
            deletarButton.setDisable(true);
        }
    }

    public static void refreshAllViews() {
        for (AbstractCrudController<?, ?, ?> controller : activeControllers) {
            controller.refreshView();
        }
    }
}