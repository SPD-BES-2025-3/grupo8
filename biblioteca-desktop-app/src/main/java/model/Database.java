package model;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import java.sql.SQLException;

/**
 * Gerencia a conexão com o banco de dados SQLite.
 *
 * @version 1.0
 */
public class Database {
    private final String databaseName;
    private JdbcConnectionSource connection;

    /**
     * Construtor da classe Database.
     *
     * @param databaseName O nome do arquivo do banco de dados SQLite.
     */
    public Database(String databaseName) {
        this.databaseName = databaseName;
    }

    /**
     * Obtém a conexão com o banco de dados, criando-a se necessário (Singleton).
     *
     * @return A fonte de conexão JDBC.
     * @throws SQLException Se o nome do banco de dados for nulo ou se ocorrer um erro de conexão.
     */
    public JdbcConnectionSource getConnection() throws SQLException {
        if (databaseName == null) {
            throw new SQLException("O nome do banco de dados é nulo.");
        }
        if (connection == null) {
            try {
                connection = new JdbcConnectionSource("jdbc:sqlite:" + databaseName);
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }
        }
        return connection;
    }

    /**
     * Fecha a conexão com o banco de dados se estiver aberta.
     */
    public void close() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }
}