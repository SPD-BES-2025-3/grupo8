package com.livraria.integrador.util;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import model.Livro; // Modelo do Desktop
// Importe outros modelos do desktop que precisar

import java.sql.SQLException;

public class DatabaseUpdater {

    // IMPORTANTE: Este caminho deve ser o mesmo usado na sua aplicação desktop
    private static final String DESKTOP_DB_URL = "jdbc:sqlite:/home/hugo-santos/Desktop/grupo8/biblioteca-desktop-app/biblioteca_desktop.sqlite";

    public static void sincronizarLivro(Livro livro, String operacao) {
        try (ConnectionSource connectionSource = new JdbcConnectionSource(DESKTOP_DB_URL)) {
            Dao<Livro, Integer> livroDao = DaoManager.createDao(connectionSource, Livro.class);

            switch (operacao.toUpperCase()) {
                case "CREATE":
                case "UPDATE":
                    // O método createOrUpdate faz um "UPSERT": cria se não existe, atualiza se existe.
                    livroDao.createOrUpdate(livro);
                    System.out.println("Livro ID " + livro.getId() + " sincronizado (criado/atualizado) no banco de dados do Desktop.");
                    break;
                case "DELETE":
                    // Antes de deletar o livro, seria preciso deletar as associações (LivroAutor, etc)
                    // Para simplificar, vamos apenas deletar o livro por enquanto.
                    livroDao.deleteById(livro.getId());
                    System.out.println("Livro ID " + livro.getId() + " deletado do banco de dados do Desktop.");
                    break;
            }

        } catch (Exception e) {
            System.err.println("ERRO ao sincronizar dados no banco do Desktop: " + e.getMessage());
            e.printStackTrace();
        }
    }
}