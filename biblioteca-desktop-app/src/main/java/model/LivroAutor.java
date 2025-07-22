package model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Classe de modelo para a tabela de junção entre Livro e Autor,
 * representando a relação Muitos-para-Muitos.
 *
 * @version 1.0
 */
@DatabaseTable(tableName = "livro_autor")
public class LivroAutor {
    @DatabaseField(generatedId = true)
    private int id;
    
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Livro livro;
    
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Autor autor;

    public LivroAutor() {}

    /**
     * Construtor da classe LivroAutor.
     * @param livro O livro a ser associado.
     * @param autor O autor a ser associado.
     */
    public LivroAutor(Livro livro, Autor autor) {
        this.livro = livro;
        this.autor = autor;
    }
    
    // Getters e Setters
    /**
     * Retorna o ID da associação.
     * @return O ID.
     */
    public int getId() { return id; }
    /**
     * Define o ID da associação.
     * @param id O ID.
     */
    public void setId(int id) { this.id = id; }
    /**
     * Retorna o livro associado.
     * @return O livro.
     */
    public Livro getLivro() { return livro; }
    /**
     * Define o livro associado.
     * @param livro O livro.
     */
    public void setLivro(Livro livro) { this.livro = livro; }
    /**
     * Retorna o autor associado.
     * @return O autor.
     */
    public Autor getAutor() { return autor; }
    /**
     * Define o autor associado.
     * @param autor O autor.
     */
    public void setAutor(Autor autor) { this.autor = autor; }
}