package model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.Date;

/**
 * Classe de modelo para a entidade Emprestimo.
 *
 * @version 1.0
 */
@DatabaseTable(tableName = "emprestimo")
public class Emprestimo {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private Date dtEmprestimo;
    
    @DatabaseField
    private Date dtPrevistaDevolucao;
    
    @DatabaseField
    private Date dtDevolucaoReal;
    
    @DatabaseField
    private String status;

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Usuario usuario;

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Livro livro;

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Date getDtEmprestimo() { return dtEmprestimo; }
    public void setDtEmprestimo(Date dtEmprestimo) { this.dtEmprestimo = dtEmprestimo; }
    public Date getDtPrevistaDevolucao() { return dtPrevistaDevolucao; }
    public void setDtPrevistaDevolucao(Date dtPrevistaDevolucao) { this.dtPrevistaDevolucao = dtPrevistaDevolucao; }
    public Date getDtDevolucaoReal() { return dtDevolucaoReal; }
    public void setDtDevolucaoReal(Date dtDevolucaoReal) { this.dtDevolucaoReal = dtDevolucaoReal; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Livro getLivro() { return livro; }
    public void setLivro(Livro livro) { this.livro = livro; }
}