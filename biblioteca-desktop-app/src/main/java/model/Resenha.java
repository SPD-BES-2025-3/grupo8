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
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public int getNota() { return nota; }
    public void setNota(int nota) { this.nota = nota; }
    public Date getDtAvaliacao() { return dtAvaliacao; }
    public void setDtAvaliacao(Date dtAvaliacao) { this.dtAvaliacao = dtAvaliacao; }
    public Livro getLivro() { return livro; }
    public void setLivro(Livro livro) { this.livro = livro; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}