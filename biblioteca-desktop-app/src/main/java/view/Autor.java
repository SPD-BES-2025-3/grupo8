package view;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * ViewModel para a entidade Autor, usado para exibição na TableView do JavaFX.
 *
 * @version 1.0
 */
public class Autor {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty nome;
    private final SimpleStringProperty nacionalidade;

    /**
     * Construtor da classe Autor.
     * @param id O ID do autor.
     * @param nome O nome do autor.
     * @param nacionalidade A nacionalidade do autor.
     */
    public Autor(int id, String nome, String nacionalidade) {
        this.id = new SimpleIntegerProperty(id);
        this.nome = new SimpleStringProperty(nome);
        this.nacionalidade = new SimpleStringProperty(nacionalidade);
    }

    // Getters para as propriedades do JavaFX
    /**
     * Retorna o ID do autor.
     * @return O ID.
     */
    public int getId() { return id.get(); }
    /**
     * Retorna o nome do autor.
     * @return O nome.
     */
    public String getNome() { return nome.get(); }
    /**
     * Retorna a nacionalidade do autor.
     * @return A nacionalidade.
     */
    public String getNacionalidade() { return nacionalidade.get(); }
}