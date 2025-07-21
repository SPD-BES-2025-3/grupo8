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

    public Categoria(int id, String nome, String descricao) {
        this.id = new SimpleIntegerProperty(id);
        this.nome = new SimpleStringProperty(nome);
        this.descricao = new SimpleStringProperty(descricao);
    }

    // Getters para as propriedades do JavaFX
    public int getId() { return id.get(); }
    public String getNome() { return nome.get(); }
    public String getDescricao() { return descricao.get(); }
}