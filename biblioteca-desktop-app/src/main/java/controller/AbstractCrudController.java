package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import model.Repositorio;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador abstrato que implementa a lógica CRUD (Create, Read, Update, Delete)
 * para uma entidade genérica. Gerencia o estado da interface e notifica outras
 * telas sobre alterações para manter a consistência dos dados.
 *
 * @param <E>  O tipo da entidade do modelo (ex: Livro, Autor).
 * @param <V>  O tipo do ViewModel para a tabela (ex: view.Livro, view.Autor).
 * @param <ID> O tipo do ID da entidade (ex: Integer).
 * @version 1.1
 */
public abstract class AbstractCrudController<E, V, ID> {

    /**
     * Lista estática que rastreia todas as instâncias ativas de controladores CRUD.
     * Usada para notificar todas as telas sobre atualizações de dados.
     */
    private static final List<AbstractCrudController<?, ?, ?>> activeControllers = new ArrayList<>();

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

    // Métodos Abstratos (devem ser implementados pelas classes filhas)
    protected abstract Repositorio<E, ID> getRepositorio();
    protected abstract V modelToView(E entidade);
    protected abstract E viewToModel(E entidade);
    protected abstract void preencherCampos(V item);
    protected abstract void limparCampos();
    protected abstract void desabilitarCampos(boolean desabilitado);
    protected abstract ID getIdFromViewModel(V viewModel);

    /**
     * Inicializa o controlador. Adiciona-se à lista de controladores ativos,
     * configura listeners e carrega os dados iniciais.
     */
    public void initialize() {
        activeControllers.add(this);

        tabela.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> handleItemSelected(newSelection));

        refreshView();
        resetToInitialState();
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
    }

    /**
     * Ação do botão "Novo". Prepara a interface para a criação de um novo item.
     */
    @FXML
    protected void onNovoButtonAction() {
        tabela.getSelectionModel().clearSelection();
        limparCampos();
        setPendingAction(Action.NOVO);
    }

    /**
     * Ação do botão "Atualizar". Prepara a interface para a atualização do item selecionado.
     */
    @FXML
    protected void onAtualizarButtonAction() {
        if (tabela.getSelectionModel().getSelectedItem() != null) {
            setPendingAction(Action.ATUALIZAR);
        }
    }

    /**
     * Ação do botão "Deletar". Prepara a interface para a exclusão do item selecionado.
     */
    @FXML
    protected void onDeletarButtonAction() {
        if (tabela.getSelectionModel().getSelectedItem() != null) {
            setPendingAction(Action.DELETAR);
        }
    }

    /**
     * Ação do botão "Confirmar". Executa a ação pendente e, se bem-sucedida,
     * notifica todos os outros controladores para atualizarem suas exibições.
     */
    @FXML
    protected void onConfirmarButtonAction() {
        boolean success = false;
        switch (pendingAction) {
            case NOVO:      success = doCreate(); break;
            case ATUALIZAR: success = doUpdate(); break;
            case DELETAR:   success = doDelete(); break;
            default: break;
        }
        resetToInitialState();

        if (success) {
            for (AbstractCrudController<?, ?, ?> controller : activeControllers) {
                controller.refreshView();
            }
        }
    }

    /**
     * Ação do botão "Cancelar". Cancela a ação pendente e retorna ao estado inicial.
     */
    @FXML
    protected void onCancelarButtonAction() {
        resetToInitialState();
    }

    /**
     * Executa a operação de criação (Create).
     * @return {@code true} se a operação foi bem-sucedida, {@code false} caso contrário.
     */
    protected boolean doCreate() {
        try {
            E novaEntidade = viewToModel(null);
            E entidadeSalva = getRepositorio().create(novaEntidade);
            return entidadeSalva != null;
        } catch (Exception e) {
            new Alert(AlertType.ERROR, "Erro ao Salvar: " + e.getMessage()).show();
            return false;
        }
    }

    /**
     * Executa a operação de atualização (Update).
     * @return {@code true} se a operação foi bem-sucedida, {@code false} caso contrário.
     */
    protected boolean doUpdate() {
        V viewItem = tabela.getSelectionModel().getSelectedItem();
        try {
            E modelItem = getRepositorio().loadFromId(getIdFromViewModel(viewItem));
            modelItem = viewToModel(modelItem);
            getRepositorio().update(modelItem);
            return true;
        } catch (Exception e) {
            new Alert(AlertType.ERROR, "Erro ao Atualizar: " + e.getMessage()).show();
            return false;
        }
    }

    /**
     * Executa a operação de exclusão (Delete).
     * @return {@code true} se a operação foi bem-sucedida, {@code false} caso contrário.
     */
    protected boolean doDelete() {
        V viewItem = tabela.getSelectionModel().getSelectedItem();
        E modelItem = getRepositorio().loadFromId(getIdFromViewModel(viewItem));
        getRepositorio().delete(modelItem);
        return true;
    }
    
    /**
     * Carrega todos os itens do repositório e os converte para a lista de ViewModels.
     * @return Uma lista observável de ViewModels.
     */
    private ObservableList<V> loadAllItems() {
        ObservableList<V> lista = FXCollections.observableArrayList();
        List<E> listaDoBanco = getRepositorio().loadAll();
        for (E itemModel : listaDoBanco) {
            lista.add(modelToView(itemModel));
        }
        return lista;
    }

    /**
     * Define a ação pendente e ajusta o estado dos botões e campos.
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
     * Reseta a interface para o estado inicial, limpando seleções e campos.
     */
    private void resetToInitialState() {
        setPendingAction(Action.NONE);
        if (tabela.getSelectionModel().getSelectedItem() == null) {
            limparCampos();
            atualizarButton.setDisable(true);
            deletarButton.setDisable(true);
        } else {
            handleItemSelected(tabela.getSelectionModel().getSelectedItem());
        }
        desabilitarCampos(true);
    }

    /**
     * Lida com a seleção de um item na tabela, preenchendo os campos e habilitando/desabilitando botões.
     * @param item O item selecionado.
     */
    private void handleItemSelected(V item) {
        if (pendingAction != Action.NONE) return;

        if (item != null) {
            preencherCampos(item);
            atualizarButton.setDisable(false);
            deletarButton.setDisable(false);
        } else {
            limparCampos();
            atualizarButton.setDisable(true);
            deletarButton.setDisable(true);
        }
        desabilitarCampos(true);
    }
}