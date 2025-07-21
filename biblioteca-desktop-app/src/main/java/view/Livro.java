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

    public Livro(int id, String titulo, String categoriaNome, String autoresNomes, int anoPublicacao) {
        this.id = new SimpleIntegerProperty(id);
        this.titulo = new SimpleStringProperty(titulo);
        this.categoriaNome = new SimpleStringProperty(categoriaNome);
        this.autoresNomes = new SimpleStringProperty(autoresNomes);
        this.anoPublicacao = new SimpleIntegerProperty(anoPublicacao);
    }

    // Getters para as propriedades do JavaFX
    public int getId() { return id.get(); }
    public String getTitulo() { return titulo.get(); }
    public String getCategoriaNome() { return categoriaNome.get(); }
    public String getAutoresNomes() { return autoresNomes.get(); }
    public int getAnoPublicacao() { return anoPublicacao.get(); }
}