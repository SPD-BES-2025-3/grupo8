package model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.Date;

/**
 * Classe de modelo para a entidade Resenha.
 *
 * @version 1.0
 */
@DatabaseTable(tableName = "resenha")
public class Resenha {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String texto;

    @DatabaseField
    private int nota; // Ex: de 1 a 5

    @DatabaseField
    private Date dtAvaliacao;

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Livro livro;

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Usuario usuario;

    // Getters e Setters
    /**
     * Retorna o ID da resenha.
     * @return O ID.
     */
    public int getId() { return id; }
    /**
     * Define o ID da resenha.
     * @param id O ID.
     */
    public void setId(int id) { this.id = id; }
    /**
     * Retorna o texto da resenha.
     * @return O texto.
     */
    public String getTexto() { return texto; }
    /**
     * Define o texto da resenha.
     * @param texto O texto.
     */
    public void setTexto(String texto) { this.texto = texto; }
    /**
     * Retorna a nota da resenha.
     * @return A nota.
     */
    public int getNota() { return nota; }
    /**
     * Define a nota da resenha.
     * @param nota A nota.
     */
    public void setNota(int nota) { this.nota = nota; }
    /**
     * Retorna a data de avaliação da resenha.
     * @return A data de avaliação.
     */
    public Date getDtAvaliacao() { return dtAvaliacao; }
    /**
     * Define a data de avaliação da resenha.
     * @param dtAvaliacao A data de avaliação.
     */
    public void setDtAvaliacao(Date dtAvaliacao) { this.dtAvaliacao = dtAvaliacao; }
    /**
     * Retorna o livro da resenha.
     * @return O livro.
     */
    public Livro getLivro() { return livro; }
    /**
     * Define o livro da resenha.
     * @param livro O livro.
     */
    public void setLivro(Livro livro) { this.livro = livro; }
    /**
     * Retorna o usuário da resenha.
     * @return O usuário.
     */
    public Usuario getUsuario() { return usuario; }
    /**
     * Define o usuário da resenha.
     * @param usuario O usuário.
     */
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}