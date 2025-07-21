package controller;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import model.Pessoa;

import java.time.ZoneId;
import java.util.Date;

/**
 * Controlador abstrato que estende AbstractCrudController e adiciona
 * lógica para campos comuns a entidades do tipo Pessoa (como Autor e Usuário).
 *
 * @param <E> O tipo da entidade do modelo, que deve estender Pessoa.
 * @param <V> O tipo do ViewModel para a tabela.
 * @version 1.0
 */
public abstract class AbstractPessoaController<E extends Pessoa, V> extends AbstractCrudController<E, V, Integer> {

    // --- Componentes FXML comuns a Usuario e Autor ---

    // Colunas da tabela
    @FXML protected TableColumn<V, Integer> idCol;
    @FXML protected TableColumn<V, String> nomeCol;

    // Campos do formulário
    @FXML protected TextField idField;
    @FXML protected TextField nomeField;
    @FXML protected DatePicker dtNascimentoPicker;

    /**
     * Preenche os campos de Pessoa a partir de um objeto do modelo.
     * @param pessoa O objeto Pessoa com os dados.
     */
    protected void preencherCamposComuns(Pessoa pessoa) {
        idField.setText(String.valueOf(pessoa.getId()));
        nomeField.setText(pessoa.getNome());
        if (pessoa.getDtNascimento() != null) {
            dtNascimentoPicker.setValue(pessoa.getDtNascimento().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        } else {
            dtNascimentoPicker.setValue(null);
        }
    }

    /**
     * Preenche um objeto do modelo com os dados dos campos comuns da tela.
     * @param pessoa O objeto Pessoa a ser preenchido.
     * @return O objeto Pessoa preenchido.
     */
    protected E viewToModelComum(E pessoa) {
        pessoa.setNome(nomeField.getText());
        if (dtNascimentoPicker.getValue() != null) {
            pessoa.setDtNascimento(Date.from(dtNascimentoPicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        } else {
            pessoa.setDtNascimento(null);
        }
        return pessoa;
    }

    /**
     * Limpa os campos comuns de Pessoa.
     */
    protected void limparCamposComuns() {
        idField.clear();
        nomeField.clear();
        dtNascimentoPicker.setValue(null);
    }

    /**
     * Desabilita os campos comuns de Pessoa.
     * @param desabilitado `true` para desabilitar, `false` para habilitar.
     */
    protected void desabilitarCamposComuns(boolean desabilitado) {
        nomeField.setDisable(desabilitado);
        dtNascimentoPicker.setDisable(desabilitado);
    }
}