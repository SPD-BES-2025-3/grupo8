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
    /**
     * Retorna o ID do empréstimo.
     * @return O ID.
     */
    public int getId() { return id; }
    /**
     * Define o ID do empréstimo.
     * @param id O ID.
     */
    public void setId(int id) { this.id = id; }
    /**
     * Retorna a data de empréstimo.
     * @return A data de empréstimo.
     */
    public Date getDtEmprestimo() { return dtEmprestimo; }
    /**
     * Define a data de empréstimo.
     * @param dtEmprestimo A data de empréstimo.
     */
    public void setDtEmprestimo(Date dtEmprestimo) { this.dtEmprestimo = dtEmprestimo; }
    /**
     * Retorna a data prevista de devolução.
     * @return A data prevista de devolução.
     */
    public Date getDtPrevistaDevolucao() { return dtPrevistaDevolucao; }
    /**
     * Define a data prevista de devolução.
     * @param dtPrevistaDevolucao A data prevista de devolução.
     */
    public void setDtPrevistaDevolucao(Date dtPrevistaDevolucao) { this.dtPrevistaDevolucao = dtPrevistaDevolucao; }
    /**
     * Retorna a data real de devolução.
     * @return A data real de devolução.
     */
    public Date getDtDevolucaoReal() { return dtDevolucaoReal; }
    /**
     * Define a data real de devolução.
     * @param dtDevolucaoReal A data real de devolução.
     */
    public void setDtDevolucaoReal(Date dtDevolucaoReal) { this.dtDevolucaoReal = dtDevolucaoReal; }
    /**
     * Retorna o status do empréstimo.
     * @return O status.
     */
    public String getStatus() { return status; }
    /**
     * Define o status do empréstimo.
     * @param status O status.
     */
    public void setStatus(String status) { this.status = status; }
    /**
     * Retorna o usuário que realizou o empréstimo.
     * @return O usuário.
     */
    public Usuario getUsuario() { return usuario; }
    /**
     * Define o usuário que realizou o empréstimo.
     * @param usuario O usuário.
     */
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    /**
     * Retorna o livro emprestado.
     * @return O livro.
     */
    public Livro getLivro() { return livro; }
    /**
     * Define o livro emprestado.
     * @param livro O livro.
     */
    public void setLivro(Livro livro) { this.livro = livro; }
}