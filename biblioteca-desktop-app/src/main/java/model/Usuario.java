package model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Classe de modelo para a entidade Usuario.
 * Estende a classe Pessoa e adiciona campos específicos de Usuário.
 *
 * @version 1.0
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
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public boolean isAtivo() { return isAtivo; }
    public void setAtivo(boolean isAtivo) { this.isAtivo = isAtivo; }
    public Cargos getCargo() { return cargo; }
    public void setCargo(Cargos cargo) { this.cargo = cargo; }
}