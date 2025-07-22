package model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.Objects;

/**
 * Classe de modelo para a entidade Categoria.
 *
 * @version 1.1
 */
@DatabaseTable(tableName = "categoria")
public class Categoria {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, unique = true)
    private String nome;

    @DatabaseField
    private String descricao;
    
    // Getters e Setters...
    /**
     * Retorna o ID da categoria.
     * @return O ID.
     */
    public int getId() { return id; }
    /**
     * Define o ID da categoria.
     * @param id O ID.
     */
    public void setId(int id) { this.id = id; }
    /**
     * Retorna o nome da categoria.
     * @return O nome.
     */
    public String getNome() { return nome; }
    /**
     * Define o nome da categoria.
     * @param nome O nome.
     */
    public void setNome(String nome) { this.nome = nome; }
    /**
     * Retorna a descrição da categoria.
     * @return A descrição.
     */
    public String getDescricao() { return descricao; }
    /**
     * Define a descrição da categoria.
     * @param descricao A descrição.
     */
    public void setDescricao(String descricao) { this.descricao = descricao; }

    @Override
    public String toString() {
        return nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Categoria categoria = (Categoria) o;
        return id == categoria.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}