package view;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import java.util.Date;

/**
 * ViewModel para a entidade Emprestimo, usado para exibição na TableView do JavaFX.
 *
 * @version 1.0
 */
public class Emprestimo {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty livroTitulo;
    private final SimpleStringProperty usuarioNome;
    private final SimpleObjectProperty<Date> dtEmprestimo;
    private final SimpleObjectProperty<Date> dtPrevistaDevolucao;
    private final SimpleStringProperty status;

    /**
     * Construtor da classe Emprestimo.
     * @param id O ID do empréstimo.
     * @param livroTitulo O título do livro.
     * @param usuarioNome O nome do usuário.
     * @param dtEmprestimo A data de empréstimo.
     * @param dtPrevistaDevolucao A data prevista de devolução.
     * @param status O status do empréstimo.
     */
    public Emprestimo(int id, String livroTitulo, String usuarioNome, Date dtEmprestimo, Date dtPrevistaDevolucao, String status) {
        this.id = new SimpleIntegerProperty(id);
        this.livroTitulo = new SimpleStringProperty(livroTitulo);
        this.usuarioNome = new SimpleStringProperty(usuarioNome);
        this.dtEmprestimo = new SimpleObjectProperty<>(dtEmprestimo);
        this.dtPrevistaDevolucao = new SimpleObjectProperty<>(dtPrevistaDevolucao);
        this.status = new SimpleStringProperty(status);
    }

    // Getters para as propriedades do JavaFX
    /**
     * Retorna o ID do empréstimo.
     * @return O ID.
     */
    public int getId() { return id.get(); }
    /**
     * Retorna o título do livro.
     * @return O título do livro.
     */
    public String getLivroTitulo() { return livroTitulo.get(); }
    /**
     * Retorna o nome do usuário.
     * @return O nome do usuário.
     */
    public String getUsuarioNome() { return usuarioNome.get(); }
    /**
     * Retorna a data de empréstimo.
     * @return A data de empréstimo.
     */
    public Date getDtEmprestimo() { return dtEmprestimo.get(); }
    /**
     * Retorna a data prevista de devolução.
     * @return A data prevista de devolução.
     */
    public Date getDtPrevistaDevolucao() { return dtPrevistaDevolucao.get(); }
    /**
     * Retorna o status do empréstimo.
     * @return O status.
     */
    public String getStatus() { return status.get(); }
}