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

import java.util.List;

/**
 * Controlador abstrato que implementa a lógica CRUD (Create, Read, Update, Delete)
 * para uma entidade genérica.
 *
 * @param <E>  O tipo da entidade do modelo.
 * @param <V>  O tipo do ViewModel para a tabela.
 * @param <ID> O tipo do ID da entidade.
 * @version 1.0
 */
public abstract class AbstractCrudController<E, V, ID> {

    private enum Action { NONE, NOVO, ATUALIZAR, DELETAR }
    private Action pendingAction = Action.NONE;

    @FXML protected TableView<V> tabela;
    @FXML protected Button novoButton;
    @FXML protected Button atualizarButton;
    @FXML protected Button deletarButton;
    @FXML protected Button confirmarButton;
    @FXML protected Button cancelarButton;
    @FXML protected Text confirmacaoText;

    protected abstract Repositorio<E, ID> getRepositorio();
    protected abstract V modelToView(E entidade);
    protected abstract E viewToModel(E entidade);
    protected abstract void preencherCampos(V item);
    protected abstract void limparCampos();
    protected abstract void desabilitarCampos(boolean desabilitado);
    protected abstract ID getIdFromViewModel(V viewModel);

    /**
     * Inicializa o controlador, configurando listeners e o estado inicial da interface.
     */
    public void initialize() {
        tabela.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> handleItemSelected(newSelection));

        tabela.setItems(loadAllItems());
        resetToInitialState();
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
     * Ação do botão "Confirmar". Executa a ação pendente (Criar, Atualizar ou Deletar).
     */
    @FXML
    protected void onConfirmarButtonAction() {
        switch (pendingAction) {
            case NOVO:      doCreate(); break;
            case ATUALIZAR: doUpdate(); break;
            case DELETAR:   doDelete(); break;
            default: break;
        }
        resetToInitialState();
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
     */
    protected void doCreate() {
        try {
            E novaEntidade = viewToModel(null);
            E entidadeSalva = getRepositorio().create(novaEntidade);
            tabela.getItems().add(modelToView(entidadeSalva));
            tabela.getSelectionModel().selectLast();
        } catch (Exception e) {
            new Alert(AlertType.ERROR, "Erro ao Salvar: " + e.getMessage()).show();
        }
    }

    /**
     * Executa a operação de atualização (Update).
     */
    protected void doUpdate() {
        V viewItem = tabela.getSelectionModel().getSelectedItem();
        try {
            E modelItem = getRepositorio().loadFromId(getIdFromViewModel(viewItem));
            modelItem = viewToModel(modelItem);
            getRepositorio().update(modelItem);

            int index = tabela.getItems().indexOf(viewItem);
            tabela.getItems().set(index, modelToView(modelItem));
        } catch (Exception e) {
            new Alert(AlertType.ERROR, "Erro ao Atualizar: " + e.getMessage()).show();
        }
    }

    /**
     * Executa a operação de exclusão (Delete).
     */
    protected void doDelete() {
        V viewItem = tabela.getSelectionModel().getSelectedItem();
        E modelItem = getRepositorio().loadFromId(getIdFromViewModel(viewItem));
        getRepositorio().delete(modelItem);
        tabela.getItems().remove(viewItem);
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
     * Reseta a interface para o estado inicial.
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
     * Lida com a seleção de um item na tabela.
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
}