package model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Classe de modelo para a entidade Usuario.
 * Estende a classe Pessoa e adiciona campos específicos de Usuário.
 *
 * @version 1.1
 */
@DatabaseTable(tableName = "usuario")
public class Usuario extends Pessoa {

    @DatabaseField(unique = true)
    private String email;

    @DatabaseField
    private String senha;
    
    @DatabaseField
    private String telefone;
    
    @DatabaseField
    private boolean isAtivo;

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Cargos cargo;
    
    // Getters e Setters
    /**
     * Retorna o email do usuário.
     * @return O email.
     */
    public String getEmail() { return email; }
    /**
     * Define o email do usuário.
     * @param email O email.
     */
    public void setEmail(String email) { this.email = email; }
    /**
     * Retorna a senha do usuário.
     * @return A senha.
     */
    public String getSenha() { return senha; }
    /**
     * Define a senha do usuário.
     * @param senha A senha.
     */
    public void setSenha(String senha) { this.senha = senha; }
    /**
     * Retorna o telefone do usuário.
     * @return O telefone.
     */
    public String getTelefone() { return telefone; }
    /**
     * Define o telefone do usuário.
     * @param telefone O telefone.
     */
    public void setTelefone(String telefone) { this.telefone = telefone; }
    /**
     * Retorna se o usuário está ativo.
     * @return `true` se o usuário está ativo, `false` caso contrário.
     */
    public boolean isAtivo() { return isAtivo; }
    /**
     * Define se o usuário está ativo.
     * @param isAtivo `true` para ativo, `false` para inativo.
     */
    public void setAtivo(boolean isAtivo) { this.isAtivo = isAtivo; }
    /**
     * Retorna o cargo do usuário.
     * @return O cargo.
     */
    public Cargos getCargo() { return cargo; }
    /**
     * Define o cargo do usuário.
     * @param cargo O cargo.
     */
    public void setCargo(Cargos cargo) { this.cargo = cargo; }

    /**
     * Retorna a representação em String do objeto, que é o seu nome.
     * Essencial para a exibição correta em componentes como ComboBox.
     * @return O nome do usuário.
     */
    @Override
    public String toString() {
        return this.nome;
    }
}