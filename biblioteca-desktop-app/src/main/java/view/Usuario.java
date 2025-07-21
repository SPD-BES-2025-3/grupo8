package view;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * ViewModel para a entidade Usuario, usado para exibição na TableView do JavaFX.
 *
 * @version 1.0
 */
public class Usuario {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty nome;
    private final SimpleStringProperty email;
    private final SimpleStringProperty telefone;
    private final SimpleBooleanProperty isAtivo;
    private final SimpleStringProperty cargoNome;

    public Usuario(int id, String nome, String email, String telefone, boolean isAtivo, String cargoNome) {
        this.id = new SimpleIntegerProperty(id);
        this.nome = new SimpleStringProperty(nome);
        this.email = new SimpleStringProperty(email);
        this.telefone = new SimpleStringProperty(telefone);
        this.isAtivo = new SimpleBooleanProperty(isAtivo);
        this.cargoNome = new SimpleStringProperty(cargoNome);
    }

    // Getters para as propriedades do JavaFX
    public int getId() { return id.get(); }
    public String getNome() { return nome.get(); }
    public String getEmail() { return email.get(); }
    public String getTelefone() { return telefone.get(); }
    public boolean isIsAtivo() { return isAtivo.get(); }
    public String getCargoNome() { return cargoNome.get(); }
}