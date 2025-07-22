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

    /**
     * Construtor da classe Resenha.
     * @param id O ID da resenha.
     * @param livroTitulo O título do livro.
     * @param usuarioNome O nome do usuário.
     * @param nota A nota da resenha.
     * @param texto O texto da resenha.
     */
    public Resenha(int id, String livroTitulo, String usuarioNome, int nota, String texto) {
        this.id = new SimpleIntegerProperty(id);
        this.livroTitulo = new SimpleStringProperty(livroTitulo);
        this.usuarioNome = new SimpleStringProperty(usuarioNome);
        this.nota = new SimpleIntegerProperty(nota);
        this.texto = new SimpleStringProperty(texto);
    }

    // Getters para as propriedades do JavaFX
    /**
     * Retorna o ID da resenha.
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
     * Retorna a nota da resenha.
     * @return A nota.
     */
    public int getNota() { return nota.get(); }
    /**
     * Retorna o texto da resenha.
     * @return O texto.
     */
    public String getTexto() { return texto.get(); }
}