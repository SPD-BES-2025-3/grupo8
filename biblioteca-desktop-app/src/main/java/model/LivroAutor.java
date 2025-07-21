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

    public LivroAutor(Livro livro, Autor autor) {
        this.livro = livro;
        this.autor = autor;
    }
    
    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Livro getLivro() { return livro; }
    public void setLivro(Livro livro) { this.livro = livro; }
    public Autor getAutor() { return autor; }
    public void setAutor(Autor autor) { this.autor = autor; }
}