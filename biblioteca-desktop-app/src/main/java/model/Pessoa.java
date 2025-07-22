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
    /**
     * Retorna o ID da pessoa.
     * @return O ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Define o ID da pessoa.
     * @param id O ID.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Retorna o nome da pessoa.
     * @return O nome.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Define o nome da pessoa.
     * @param nome O nome.
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Retorna a data de nascimento da pessoa.
     * @return A data de nascimento.
     */
    public Date getDtNascimento() {
        return dtNascimento;
    }

    /**
     * Define a data de nascimento da pessoa.
     * @param dtNascimento A data de nascimento.
     */
    public void setDtNascimento(Date dtNascimento) {
        this.dtNascimento = dtNascimento;
    }
}