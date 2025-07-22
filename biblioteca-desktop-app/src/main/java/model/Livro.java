package model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Classe de modelo para a entidade Livro.
 * Contém todos os atributos de um livro e suas relações com Categoria e Autor.
 *
 * @version 1.1
 */
@DatabaseTable(tableName = "livro")
public class Livro {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String isbn;

    @DatabaseField(canBeNull = false)
    private String titulo;

    @DatabaseField
    private int anoPublicacao;
    
    @DatabaseField
    private String edicao;

    @DatabaseField
    private int numPaginas;

    @DatabaseField
    private String sinopse;

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Categoria categoria;
    
    @ForeignCollectionField(eager = true)
    private ForeignCollection<LivroAutor> autores;

    // Getters e Setters...
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public int getAnoPublicacao() { return anoPublicacao; }
    public void setAnoPublicacao(int anoPublicacao) { this.anoPublicacao = anoPublicacao; }
    public String getEdicao() { return edicao; }
    public void setEdicao(String edicao) { this.edicao = edicao; }
    public int getNumPaginas() { return numPaginas; }
    public void setNumPaginas(int numPaginas) { this.numPaginas = numPaginas; }
    public String getSinopse() { return sinopse; }
    public void setSinopse(String sinopse) { this.sinopse = sinopse; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    public ForeignCollection<LivroAutor> getAutores() { return autores; }
    public void setAutores(ForeignCollection<LivroAutor> autores) { this.autores = autores; }

    /**
     * Retorna a representação em String do objeto, que é o seu título.
     * Essencial para a exibição correta em componentes como ComboBox.
     * @return O título do livro.
     */
    @Override
    public String toString() {
        return this.titulo;
    }
}