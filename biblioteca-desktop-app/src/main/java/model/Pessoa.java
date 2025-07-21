package model;

import com.j256.ormlite.field.DatabaseField;
import java.util.Date;

/**
 * Classe abstrata base para entidades que representam uma pessoa,
 * contendo campos comuns como id, nome e data de nascimento.
 *
 * @version 1.0
 */
public abstract class Pessoa {

    @DatabaseField(generatedId = true)
    protected int id;

    @DatabaseField(canBeNull = false)
    protected String nome;

    @DatabaseField
    protected Date dtNascimento;

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Date getDtNascimento() {
        return dtNascimento;
    }

    public void setDtNascimento(Date dtNascimento) {
        this.dtNascimento = dtNascimento;
    }
}