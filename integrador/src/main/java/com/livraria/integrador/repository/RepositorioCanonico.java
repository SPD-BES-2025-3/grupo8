package com.livraria.integrador.repository;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.livraria.integrador.model.canonico.MapeamentoID;

import java.sql.SQLException;

public class RepositorioCanonico {

    private static ConnectionSource connectionSource;
    public static Dao<MapeamentoID, String> mapeamentoIdDao;

    // Bloco estático para inicializar a conexão e os DAOs uma única vez.
    static {
        try {
            // Este banco de dados pertence SOMENTE ao integrador
            String databaseUrl = "jdbc:sqlite:integrador_dados.sqlite";
            connectionSource = new JdbcConnectionSource(databaseUrl);

            // Cria a tabela de mapeamento se ela não existir
            TableUtils.createTableIfNotExists(connectionSource, MapeamentoID.class);

            // Inicializa o DAO
            mapeamentoIdDao = DaoManager.createDao(connectionSource, MapeamentoID.class);

            System.out.println("Repositório Canônico inicializado com sucesso.");

        } catch (SQLException e) {
            System.err.println("Falha fatal ao inicializar o Repositório Canônico.");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}