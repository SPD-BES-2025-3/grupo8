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

    public Autor(int id, String nome, String nacionalidade) {
        this.id = new SimpleIntegerProperty(id);
        this.nome = new SimpleStringProperty(nome);
        this.nacionalidade = new SimpleStringProperty(nacionalidade);
    }

    // Getters para as propriedades do JavaFX
    public int getId() { return id.get(); }
    public String getNome() { return nome.get(); }
    public String getNacionalidade() { return nacionalidade.get(); }
}