package model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Classe de modelo para a entidade Autor.
 * Estende a classe Pessoa e adiciona campos específicos de Autor.
 *
 * @version 1.0
 */
@DatabaseTable(tableName = "autor")
public class Autor extends Pessoa {
    
    @DatabaseField
    private String biografia;
    
    @DatabaseField
    private String nacionalidade;

    // Relação Muitos-para-Muitos com Livro
    @ForeignCollectionField(eager = true)
    private ForeignCollection<LivroAutor> livros;
    
    // Getters e Setters
    public String getBiografia() { return biografia; }
    public void setBiografia(String biografia) { this.biografia = biografia; }
    public String getNacionalidade() { return nacionalidade; }
    public void setNacionalidade(String nacionalidade) { this.nacionalidade = nacionalidade; }
    public ForeignCollection<LivroAutor> getLivros() { return livros; }
    public void setLivros(ForeignCollection<LivroAutor> livros) { this.livros = livros; }
    
    @Override
    public String toString() {
        return nome; // Sobrescreve para mostrar o nome no ComboBox
    }
}