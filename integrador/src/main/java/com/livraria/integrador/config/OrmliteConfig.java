package com.livraria.integrador.config;


import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.livraria.integrador.model.*; // Importe todos os modelos do projeto desktop
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.SQLException;

@Configuration
public class OrmliteConfig {

    // ATENÇÃO: Substitua pelo caminho absoluto para o seu arquivo .sqlite
    private static final String DATABASE_URL = "jdbc:sqlite:/caminho/absoluto/para/grupo8/biblioteca-desktop-app/biblioteca_desktop.sqlite";

    @Bean
    public ConnectionSource connectionSource() throws SQLException {
        return new JdbcConnectionSource(DATABASE_URL);
    }

    // Disponibiliza os DAOs para serem injetados em outras classes
    @Bean
    public Dao<Livro, Integer> livroDao(ConnectionSource connectionSource) throws SQLException {
        return DaoManager.createDao(connectionSource, Livro.class);
    }

    @Bean
    public Dao<Autor, Integer> autorDao(ConnectionSource connectionSource) throws SQLException {
        return DaoManager.createDao(connectionSource, Autor.class);
    }

    @Bean
    public Dao<Categoria, Integer> categoriaDao(ConnectionSource connectionSource) throws SQLException {
        return DaoManager.createDao(connectionSource, Categoria.class);
    }

    @Bean
    public Dao<LivroAutor, Integer> livroAutorDao(ConnectionSource connectionSource) throws SQLException {
        return DaoManager.createDao(connectionSource, LivroAutor.class);
    }
}