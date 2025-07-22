package model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Classe de modelo para a entidade Cargos.
 *
 * @version 1.0
 */
@DatabaseTable(tableName = "cargos")
public class Cargos {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, unique = true)
    private String name;

    // Getters e Setters
    /**
     * Retorna o ID do cargo.
     * @return O ID.
     */
    public int getId() { return id; }
    /**
     * Define o ID do cargo.
     * @param id O ID.
     */
    public void setId(int id) { this.id = id; }
    /**
     * Retorna o nome do cargo.
     * @return O nome.
     */
    public String getName() { return name; }
    /**
     * Define o nome do cargo.
     * @param name O nome.
     */
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return name;
    }
}