package view;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * ViewModel para a entidade Categoria, usado para exibição na TableView do JavaFX.
 *
 * @version 1.0
 */
public class Categoria {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty nome;
    private final SimpleStringProperty descricao;

    /**
     * Construtor da classe Categoria.
     * @param id O ID da categoria.
     * @param nome O nome da categoria.
     * @param descricao A descrição da categoria.
     */
    public Categoria(int id, String nome, String descricao) {
        this.id = new SimpleIntegerProperty(id);
        this.nome = new SimpleStringProperty(nome);
        this.descricao = new SimpleStringProperty(descricao);
    }

    // Getters para as propriedades do JavaFX
    /**
     * Retorna o ID da categoria.
     * @return O ID.
     */
    public int getId() { return id.get(); }
    /**
     * Retorna o nome da categoria.
     * @return O nome.
     */
    public String getNome() { return nome.get(); }
    /**
     * Retorna a descrição da categoria.
     * @return A descrição.
     */
    public String getDescricao() { return descricao.get(); }
}