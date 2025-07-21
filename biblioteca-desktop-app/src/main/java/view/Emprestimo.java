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

    public Emprestimo(int id, String livroTitulo, String usuarioNome, Date dtEmprestimo, Date dtPrevistaDevolucao, String status) {
        this.id = new SimpleIntegerProperty(id);
        this.livroTitulo = new SimpleStringProperty(livroTitulo);
        this.usuarioNome = new SimpleStringProperty(usuarioNome);
        this.dtEmprestimo = new SimpleObjectProperty<>(dtEmprestimo);
        this.dtPrevistaDevolucao = new SimpleObjectProperty<>(dtPrevistaDevolucao);
        this.status = new SimpleStringProperty(status);
    }

    // Getters para as propriedades do JavaFX
    public int getId() { return id.get(); }
    public String getLivroTitulo() { return livroTitulo.get(); }
    public String getUsuarioNome() { return usuarioNome.get(); }
    public Date getDtEmprestimo() { return dtEmprestimo.get(); }
    public Date getDtPrevistaDevolucao() { return dtPrevistaDevolucao.get(); }
    public String getStatus() { return status.get(); }
}