package model;

/**
 * Classe central que inicializa e fornece acesso estático a todos os repositórios da aplicação.
 *
 * @version 1.0
 */
public class Repositorios {
    public static final Database database = new Database("biblioteca_desktop.sqlite");

    public static final Repositorio<Cargos, Integer> CARGO = new Repositorio<>(database, Cargos.class);
    public static final Repositorio<Usuario, Integer> USUARIO = new Repositorio<>(database, Usuario.class);
    public static final Repositorio<Categoria, Integer> CATEGORIA = new Repositorio<>(database, Categoria.class);
    public static final Repositorio<Autor, Integer> AUTOR = new Repositorio<>(database, Autor.class);
    public static final Repositorio<Livro, Integer> LIVRO = new Repositorio<>(database, Livro.class);
    public static final Repositorio<LivroAutor, Integer> LIVRO_AUTOR = new Repositorio<>(database, LivroAutor.class);
    public static final Repositorio<Emprestimo, Integer> EMPRESTIMO = new Repositorio<>(database, Emprestimo.class);
    public static final Repositorio<Resenha, Integer> RESENHA = new Repositorio<>(database, Resenha.class);
}