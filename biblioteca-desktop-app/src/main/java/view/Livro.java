package view;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * ViewModel para a entidade Livro, usado para exibição na TableView do JavaFX.
 *
 * @version 1.0
 */
public class Livro {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty titulo;
    private final SimpleStringProperty categoriaNome;
    private final SimpleStringProperty autoresNomes;
    private final SimpleIntegerProperty anoPublicacao;

    /**
     * Construtor da classe Livro.
     * @param id O ID do livro.
     * @param titulo O título do livro.
     * @param categoriaNome O nome da categoria do livro.
     * @param autoresNomes Os nomes dos autores do livro.
     * @param anoPublicacao O ano de publicação do livro.
     */
    public Livro(int id, String titulo, String categoriaNome, String autoresNomes, int anoPublicacao) {
        this.id = new SimpleIntegerProperty(id);
        this.titulo = new SimpleStringProperty(titulo);
        this.categoriaNome = new SimpleStringProperty(categoriaNome);
        this.autoresNomes = new SimpleStringProperty(autoresNomes);
        this.anoPublicacao = new SimpleIntegerProperty(anoPublicacao);
    }

    // Getters para as propriedades do JavaFX
    /**
     * Retorna o ID do livro.
     * @return O ID.
     */
    public int getId() { return id.get(); }
    /**
     * Retorna o título do livro.
     * @return O título.
     */
    public String getTitulo() { return titulo.get(); }
    /**
     * Retorna o nome da categoria do livro.
     * @return O nome da categoria.
     */
    public String getCategoriaNome() { return categoriaNome.get(); }
    /**
     * Retorna os nomes dos autores do livro.
     * @return Os nomes dos autores.
     */
    public String getAutoresNomes() { return autoresNomes.get(); }
    /**
     * Retorna o ano de publicação do livro.
     * @return O ano de publicação.
     */
    public int getAnoPublicacao() { return anoPublicacao.get(); }
}