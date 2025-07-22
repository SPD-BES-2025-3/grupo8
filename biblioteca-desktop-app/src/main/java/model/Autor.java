package model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.Objects;

/**
 * Classe de modelo para a entidade Autor.
 * Estende a classe Pessoa e adiciona campos específicos de Autor.
 *
 * @version 1.1
 */
@DatabaseTable(tableName = "autor")
public class Autor extends Pessoa {
    
    @DatabaseField
    private String biografia;
    
    @DatabaseField
    private String nacionalidade;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<LivroAutor> livros;
    
    // Getters e Setters...
    /**
     * Retorna a biografia do autor.
     * @return A biografia.
     */
    public String getBiografia() { return biografia; }
    /**
     * Define a biografia do autor.
     * @param biografia A biografia.
     */
    public void setBiografia(String biografia) { this.biografia = biografia; }
    /**
     * Retorna a nacionalidade do autor.
     * @return A nacionalidade.
     */
    public String getNacionalidade() { return nacionalidade; }
    /**
     * Define a nacionalidade do autor.
     * @param nacionalidade A nacionalidade.
     */
    public void setNacionalidade(String nacionalidade) { this.nacionalidade = nacionalidade; }
    /**
     * Retorna a coleção de livros do autor.
     * @return A coleção de livros.
     */
    public ForeignCollection<LivroAutor> getLivros() { return livros; }
    /**
     * Define a coleção de livros do autor.
     * @param livros A coleção de livros.
     */
    public void setLivros(ForeignCollection<LivroAutor> livros) { this.livros = livros; }
    
    @Override
    public String toString() {
        return nome;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Autor autor = (Autor) o;
        return id == autor.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}