package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Autor;
import model.LivroAutor;
import model.Repositorio;
import model.Repositorios;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.control.Alert;
import javafx.scene.control.Control;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para a tela de CRUD de Autores.
 *
 * @version 1.0
 */
public class AutorController extends AbstractPessoaController<model.Autor, view.Autor> implements Initializable {

    // --- Componentes FXML específicos de Autor ---
    @FXML private TableColumn<view.Autor, String> nacionalidadeCol;
    @FXML private TextField nacionalidadeField;
    @FXML private TextArea biografiaArea;

    /**
     * Inicializa o controlador, mapeando as colunas da tabela e chamando o inicializador da classe pai.
     *
     * @param location  A localização usada para resolver caminhos relativos para o objeto raiz, ou null se a localização não for conhecida.
     * @param resources Os recursos usados para localizar o objeto raiz, ou null se o objeto raiz não foi localizado.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Mapeia colunas herdadas e específicas
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomeCol.setCellValueFactory(new PropertyValueFactory<>("nome"));
        nacionalidadeCol.setCellValueFactory(new PropertyValueFactory<>("nacionalidade"));
        
        super.initialize();
    }

    @Override
    protected Repositorio<Autor, Integer> getRepositorio() {
        return Repositorios.AUTOR;
    }

    @Override
    protected view.Autor modelToView(Autor model) {
        return new view.Autor(model.getId(), model.getNome(), model.getNacionalidade());
    }

    @Override
    protected Autor viewToModel(Autor autor) {
        if (autor == null) {
            autor = new Autor();
        }
        super.viewToModelComum(autor);

        autor.setNacionalidade(nacionalidadeField.getText());
        autor.setBiografia(biografiaArea.getText());

        return autor;
    }

    @Override
    protected void preencherCampos(view.Autor autorView) {
        Autor autorModel = getRepositorio().loadFromId(autorView.getId());
        
        super.preencherCamposComuns(autorModel);

        nacionalidadeField.setText(autorModel.getNacionalidade());
        biografiaArea.setText(autorModel.getBiografia());
    }

    @Override
    protected void limparCampos() {
        super.limparCamposComuns();
        nacionalidadeField.clear();
        biografiaArea.clear();
    }

    @Override
    protected void desabilitarCampos(boolean desabilitado) {
        super.desabilitarCamposComuns(desabilitado);
        nacionalidadeField.setDisable(desabilitado);
        biografiaArea.setDisable(desabilitado);
    }

    @Override
    protected Integer getIdFromViewModel(view.Autor viewModel) {
        return viewModel.getId();
    }

    @Override
    protected List<Control> getCamposObrigatorios() {
        return List.of(nomeField, nacionalidadeField);
    }

    /**
     * Deleta um autor, removendo primeiro as associações com livros.
     * @return `true` se a operação for bem-sucedida, `false` caso contrário.
     */
    @Override
    protected boolean doDelete() {
        view.Autor viewItem = tabela.getSelectionModel().getSelectedItem();
        if (viewItem == null) return false;

        try {
            Autor modelItem = getRepositorio().loadFromId(getIdFromViewModel(viewItem));
            if (modelItem == null) return false;

            // 1. Deletar associações em LivroAutor
            List<LivroAutor> associacoes = new ArrayList<>(modelItem.getLivros());
            if (!associacoes.isEmpty()) {
                Repositorios.LIVRO_AUTOR.getDao().delete(associacoes);
            }

            // 2. Deletar o Autor
            getRepositorio().delete(modelItem);
            return true;
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro ao Deletar o Autor");
            alert.setHeaderText("Não foi possível excluir o autor.");
            alert.setContentText("Causa: " + e.getMessage());
            alert.showAndWait();
            return false;
        }
    }
}