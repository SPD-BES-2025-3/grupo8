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
    /**
     * Retorna o ID do livro.
     * @return O ID.
     */
    public int getId() { return id; }
    /**
     * Define o ID do livro.
     * @param id O ID.
     */
    public void setId(int id) { this.id = id; }
    /**
     * Retorna o ISBN do livro.
     * @return O ISBN.
     */
    public String getIsbn() { return isbn; }
    /**
     * Define o ISBN do livro.
     * @param isbn O ISBN.
     */
    public void setIsbn(String isbn) { this.isbn = isbn; }
    /**
     * Retorna o título do livro.
     * @return O título.
     */
    public String getTitulo() { return titulo; }
    /**
     * Define o título do livro.
     * @param titulo O título.
     */
    public void setTitulo(String titulo) { this.titulo = titulo; }
    /**
     * Retorna o ano de publicação do livro.
     * @return O ano de publicação.
     */
    public int getAnoPublicacao() { return anoPublicacao; }
    /**
     * Define o ano de publicação do livro.
     * @param anoPublicacao O ano de publicação.
     */
    public void setAnoPublicacao(int anoPublicacao) { this.anoPublicacao = anoPublicacao; }
    /**
     * Retorna a edição do livro.
     * @return A edição.
     */
    public String getEdicao() { return edicao; }
    /**
     * Define a edição do livro.
     * @param edicao A edição.
     */
    public void setEdicao(String edicao) { this.edicao = edicao; }
    /**
     * Retorna o número de páginas do livro.
     * @return O número de páginas.
     */
    public int getNumPaginas() { return numPaginas; }
    /**
     * Define o número de páginas do livro.
     * @param numPaginas O número de páginas.
     */
    public void setNumPaginas(int numPaginas) { this.numPaginas = numPaginas; }
    /**
     * Retorna a sinopse do livro.
     * @return A sinopse.
     */
    public String getSinopse() { return sinopse; }
    /**
     * Define a sinopse do livro.
     * @param sinopse A sinopse.
     */
    public void setSinopse(String sinopse) { this.sinopse = sinopse; }
    /**
     * Retorna a categoria do livro.
     * @return A categoria.
     */
    public Categoria getCategoria() { return categoria; }
    /**
     * Define a categoria do livro.
     * @param categoria A categoria.
     */
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    /**
     * Retorna a coleção de autores do livro.
     * @return A coleção de autores.
     */
    public ForeignCollection<LivroAutor> getAutores() { return autores; }
    /**
     * Define a coleção de autores do livro.
     * @param autores A coleção de autores.
     */
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