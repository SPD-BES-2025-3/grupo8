package view;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * ViewModel para a entidade Resenha, usado para exibição na TableView do JavaFX.
 *
 * @version 1.0
 */
public class Resenha {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty livroTitulo;
    private final SimpleStringProperty usuarioNome;
    private final SimpleIntegerProperty nota;
    private final SimpleStringProperty texto;

    public Resenha(int id, String livroTitulo, String usuarioNome, int nota, String texto) {
        this.id = new SimpleIntegerProperty(id);
        this.livroTitulo = new SimpleStringProperty(livroTitulo);
        this.usuarioNome = new SimpleStringProperty(usuarioNome);
        this.nota = new SimpleIntegerProperty(nota);
        this.texto = new SimpleStringProperty(texto);
    }

    // Getters para as propriedades do JavaFX
    public int getId() { return id.get(); }
    public String getLivroTitulo() { return livroTitulo.get(); }
    public String getUsuarioNome() { return usuarioNome.get(); }
    public int getNota() { return nota.get(); }
    public String getTexto() { return texto.get(); }
}