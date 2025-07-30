package com.livraria.integrador.util;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
// Importe todos os modelos necessários do seu projeto desktop
import model.*; 

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import com.livraria.integrador.model.LivroApi;
import com.livraria.integrador.model.ResenhaApi;

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
            Dao<Resenha, Integer> resenhaDao = DaoManager.createDao(connectionSource, Resenha.class);
            Dao<Usuario, Integer> usuarioDao = DaoManager.createDao(connectionSource, Usuario.class);
            Dao<Cargos, Integer> cargoDao = DaoManager.createDao(connectionSource, Cargos.class);

            if ("DELETE".equals(operacao)) {
                List<LivroAutor> linksParaDeletar = livroAutorDao.queryBuilder().where().eq("livro_id", idDesktop).query();
                if (!linksParaDeletar.isEmpty()) {
                    livroAutorDao.delete(linksParaDeletar);
                }
                livroDao.deleteById(idDesktop);
                System.out.println("[DB Updater] Livro ID " + idDesktop + " e suas associações foram removidos do Desktop.");
                return null;
            }

            // --- Lógica de CREATE e UPDATE ---
            
            // 1. Sincroniza o Livro principal, Categoria e Autores (lógica existente)
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
                    livroAutorDao.create(new LivroAutor(livroDesktop, autor));
                }
            }
            
            // 2. *** LÓGICA DE SINCRONIZAÇÃO DE RESENHAS ADICIONADA AQUI ***
            // 2.1. Apaga as resenhas antigas deste livro para garantir consistência total
            List<Resenha> resenhasAntigas = resenhaDao.queryBuilder().where().eq("livro_id", livroDesktop.getId()).query();
            if (!resenhasAntigas.isEmpty()) {
                resenhaDao.delete(resenhasAntigas);
            }

            // 2.2. Itera e cria as novas resenhas vindas da API
            if (livroApi.getResenhas() != null) {
                for (ResenhaApi resenhaApi : livroApi.getResenhas()) {
                    // Procura o usuário da resenha pelo nome
                    Usuario usuario = usuarioDao.queryBuilder().where().eq("nome", resenhaApi.getNomeUsuario()).queryForFirst();
                    
                    // Se o usuário não existe, cria um novo com o cargo "Cliente"
                    if (usuario == null) {
                        Cargos cargoCliente = cargoDao.queryBuilder().where().eq("name", "Cliente").queryForFirst();
                        if (cargoCliente == null) { // Fallback caso o cargo "Cliente" não exista
                            cargoCliente = new Cargos();
                            cargoCliente.setName("Cliente");
                            cargoDao.create(cargoCliente);
                        }
                        usuario = new Usuario();
                        usuario.setNome(resenhaApi.getNomeUsuario());
                        usuario.setCargo(cargoCliente);
                        // Define um email/senha padrão para o novo usuário, pois são campos obrigatórios
                        usuario.setEmail(resenhaApi.getNomeUsuario().replaceAll("\\s+", "").toLowerCase() + "@email-sync.com");
                        usuario.setSenha("123456");
                        usuario.setAtivo(false);
                        usuarioDao.create(usuario);
                    }

                    // Cria a nova entidade Resenha para o desktop
                    Resenha resenhaDesktop = new Resenha();
                    resenhaDesktop.setLivro(livroDesktop);
                    resenhaDesktop.setUsuario(usuario);
                    resenhaDesktop.setNota(resenhaApi.getNota());
                    resenhaDesktop.setTexto(resenhaApi.getTexto());
                    if (resenhaApi.getDataAvaliacao() != null) {
                        // Converte LocalDateTime da API para java.util.Date do Desktop
                        resenhaDesktop.setDtAvaliacao(Date.from(resenhaApi.getDataAvaliacao().atZone(ZoneId.systemDefault()).toInstant()));
                    }
                    
                    resenhaDao.create(resenhaDesktop);
                }
            }
            
            System.out.println("[DB Updater] Livro ID " + livroDesktop.getId() + " e suas resenhas foram sincronizados (CRIADO/ATUALIZADO) no Desktop.");
            return livroDesktop;

        } catch (Exception e) {
            System.err.println("ERRO CRÍTICO ao sincronizar dados no banco do Desktop: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}