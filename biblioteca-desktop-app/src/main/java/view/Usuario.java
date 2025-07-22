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

    /**
     * Construtor da classe Usuario.
     * @param id O ID do usuário.
     * @param nome O nome do usuário.
     * @param email O email do usuário.
     * @param telefone O telefone do usuário.
     * @param isAtivo Se o usuário está ativo.
     * @param cargoNome O nome do cargo do usuário.
     */
    public Usuario(int id, String nome, String email, String telefone, boolean isAtivo, String cargoNome) {
        this.id = new SimpleIntegerProperty(id);
        this.nome = new SimpleStringProperty(nome);
        this.email = new SimpleStringProperty(email);
        this.telefone = new SimpleStringProperty(telefone);
        this.isAtivo = new SimpleBooleanProperty(isAtivo);
        this.cargoNome = new SimpleStringProperty(cargoNome);
    }

    // Getters para as propriedades do JavaFX
    /**
     * Retorna o ID do usuário.
     * @return O ID.
     */
    public int getId() { return id.get(); }
    /**
     * Retorna o nome do usuário.
     * @return O nome.
     */
    public String getNome() { return nome.get(); }
    /**
     * Retorna o email do usuário.
     * @return O email.
     */
    public String getEmail() { return email.get(); }
    /**
     * Retorna o telefone do usuário.
     * @return O telefone.
     */
    public String getTelefone() { return telefone.get(); }
    /**
     * Retorna se o usuário está ativo.
     * @return `true` se o usuário está ativo, `false` caso contrário.
     */
    public boolean isIsAtivo() { return isAtivo.get(); }
    /**
     * Retorna o nome do cargo do usuário.
     * @return O nome do cargo.
     */
    public String getCargoNome() { return cargoNome.get(); }
}