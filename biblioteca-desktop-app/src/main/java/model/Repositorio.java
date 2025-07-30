package model;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Classe genérica de repositório que encapsula as operações CRUD
 * para uma entidade usando ORMLite.
 *
 * @param <T>  O tipo da entidade.
 * @param <ID> O tipo do ID da entidade.
 * @version 1.0
 */
public class Repositorio<T, ID> {
    private final Dao<T, ID> dao;

    /**
     * Construtor do repositório. Inicializa o DAO e cria a tabela se ela não existir.
     *
     * @param database      A instância do banco de dados.
     * @param entityClass   A classe da entidade.
     */
    public Repositorio(Database database, Class<T> entityClass) {
        try {
            dao = DaoManager.createDao(database.getConnection(), entityClass);
            TableUtils.createTableIfNotExists(database.getConnection(), entityClass);
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao inicializar repositório: " + e.getMessage(), e);
        }
    }

    /**
     * Retorna o objeto DAO para operações mais complexas.
     * @return O DAO da entidade.
     */
    public Dao<T, ID> getDao() {
        return dao;
    }

    /**
     * Cria uma nova entidade no banco de dados.
     *
     * @param entity A entidade a ser criada.
     * @return A entidade criada, ou null se ocorrer um erro.
     */
    public T create(T entity) {
        try {
            dao.create(entity);
            return entity;
        } catch (SQLException e) {
            System.err.println("Erro ao criar: " + e.getMessage());
            return null;
        }
    }

    /**
     * Atualiza uma entidade existente no banco de dados.
     *
     * @param entity A entidade a ser atualizada.
     */
    public void update(T entity) {
        try {
            dao.update(entity);
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar: " + e.getMessage());
        }
    }

    /**
     * Deleta uma entidade do banco de dados.
     *
     * @param entity A entidade a ser deletada.
     */
    public void delete(T entity) {
        try {
            dao.delete(entity);
        } catch (SQLException e) {
            System.err.println("Erro ao deletar: " + e.getMessage());
        }
    }

    /**
     * Carrega uma entidade do banco de dados pelo seu ID.
     *
     * @param id O ID da entidade a ser carregada.
     * @return A entidade encontrada, ou null se não for encontrada ou se ocorrer um erro.
     */
    public T loadFromId(ID id) {
        try {
            return dao.queryForId(id);
        } catch (SQLException e) {
            System.err.println("Erro ao carregar por ID: " + e.getMessage());
            return null;
        }
    }

    /**
     * Carrega todas as entidades de um tipo do banco de dados.
     *
     * @return Uma lista de todas as entidades, ou uma lista vazia se ocorrer um erro.
     */
    public List<T> loadAll() {
        try {
            return dao.queryForAll();
        } catch (SQLException e) {
            System.err.println("Erro ao carregar todos: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}