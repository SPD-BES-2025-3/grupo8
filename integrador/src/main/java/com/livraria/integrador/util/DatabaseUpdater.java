package com.livraria.integrador.util;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import model.Autor;
import model.Categoria;
import model.Livro;
import model.LivroAutor;
import com.livraria.integrador.model.LivroApi;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class DatabaseUpdater {

    private static final String DESKTOP_DB_URL;

    // Bloco estático para calcular o caminho dinamicamente uma única vez
    static {
        // Pega o diretório de onde o JAR do integrador está sendo executado.
        Path currentRelativePath = Paths.get("");
        String currentAbsolutePath = currentRelativePath.toAbsolutePath().toString();
        
        System.out.println("[DB Updater] Diretório de execução do Integrador: " + currentAbsolutePath);

        // Constrói o caminho relativo para o banco de dados do desktop,
        // assumindo que 'integrador' e 'biblioteca-desktop-app' são pastas irmãs.
        Path dbPath = Paths.get(currentAbsolutePath, "..", "biblioteca-desktop-app", "biblioteca_desktop.sqlite");
        
        // Normaliza o caminho para resolver ".." e criar um caminho limpo e absoluto.
        String normalizedDbPath = dbPath.normalize().toString();
        
        DESKTOP_DB_URL = "jdbc:sqlite:" + normalizedDbPath;
        System.out.println("[DB Updater] Conectando ao banco de dados do Desktop em: " + DESKTOP_DB_URL);
    }

    public static Livro sincronizarLivro(LivroApi livroApi, Integer idDesktop, String operacao) {
        try (ConnectionSource connectionSource = new JdbcConnectionSource(DESKTOP_DB_URL)) {
            Dao<Livro, Integer> livroDao = DaoManager.createDao(connectionSource, Livro.class);
            Dao<Autor, Integer> autorDao = DaoManager.createDao(connectionSource, Autor.class);
            Dao<Categoria, Integer> categoriaDao = DaoManager.createDao(connectionSource, Categoria.class);
            Dao<LivroAutor, Integer> livroAutorDao = DaoManager.createDao(connectionSource, LivroAutor.class);

            if ("DELETE".equals(operacao)) {
                List<LivroAutor> linksParaDeletar = livroAutorDao.queryBuilder().where().eq("livro_id", idDesktop).query();
                if (!linksParaDeletar.isEmpty()) {
                    livroAutorDao.delete(linksParaDeletar);
                }
                livroDao.deleteById(idDesktop);
                System.out.println("[DB Updater] Livro ID " + idDesktop + " e suas associações foram removidos do Desktop.");
                return null;
            }

            Livro livroDesktop = new Livro();
            livroDesktop.setId(idDesktop);
            livroDesktop.setTitulo(livroApi.getTitulo());
            livroDesktop.setIsbn(livroApi.getIsbn());
            livroDesktop.setAnoPublicacao(livroApi.getAnoPublicacao());
            livroDesktop.setEdicao(livroApi.getEdicao());
            livroDesktop.setNumPaginas(livroApi.getNumPaginas());
            livroDesktop.setSinopse(livroApi.getSinopse());

            Categoria categoria = categoriaDao.queryBuilder().where().eq("nome", livroApi.getCategoria()).queryForFirst();
            if (categoria == null) {
                categoria = new Categoria();
                categoria.setNome(livroApi.getCategoria());
                categoriaDao.create(categoria);
            }
            livroDesktop.setCategoria(categoria);

            livroDao.createOrUpdate(livroDesktop);

            List<LivroAutor> linksAntigos = livroAutorDao.queryBuilder().where().eq("livro_id", livroDesktop.getId()).query();
            if (!linksAntigos.isEmpty()) {
                livroAutorDao.delete(linksAntigos);
            }
            
            if (livroApi.getAutores() != null) {
                for (String nomeAutor : livroApi.getAutores()) {
                    Autor autor = autorDao.queryBuilder().where().eq("nome", nomeAutor).queryForFirst();
                    if (autor == null) {
                        autor = new Autor();
                        autor.setNome(nomeAutor);
                        autor.setBiografia("");
                        autorDao.create(autor);
                    }
                    LivroAutor livroAutor = new LivroAutor(livroDesktop, autor);
                    livroAutorDao.create(livroAutor);
                }
            }
            
            System.out.println("[DB Updater] Livro ID " + livroDesktop.getId() + " foi sincronizado (CRIADO/ATUALIZADO) no Desktop.");
            return livroDesktop;

        } catch (Exception e) {
            System.err.println("ERRO CRÍTICO ao sincronizar dados no banco do Desktop: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}