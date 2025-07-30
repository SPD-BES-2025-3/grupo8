package model.dto;

import com.j256.ormlite.dao.Dao;
import model.*; // Importa as classes de modelo
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO (Data Transfer Object) para serializar os dados de um Livro para o JMS.
 * Esta classe busca ativamente os dados relacionados (autores e resenhas)
 * para construir um objeto completo para sincronização.
 */
public class LivroSyncDto {

    private int id;
    private String isbn;
    private String titulo;
    private int anoPublicacao;
    private String edicao;
    private int numPaginas;
    private String sinopse;
    
    private String categoria;
    private List<String> autores;
    private List<ResenhaSyncDto> resenhas;

    // Construtor privado para forçar o uso do método de fábrica estático
    private LivroSyncDto() {}

    /**
     * Constrói um DTO completo a partir de uma entidade Livro do ORMLite.
     * Este método executa consultas adicionais para agregar dados de autores e resenhas.
     * @param livro A entidade Livro do banco de dados.
     * @return Um LivroSyncDto preenchido e pronto para ser serializado.
     */
    public static LivroSyncDto fromLivro(Livro livro) {
        LivroSyncDto dto = new LivroSyncDto();
        
        // 1. Mapeia os campos diretos do objeto Livro
        dto.id = livro.getId();
        dto.isbn = livro.getIsbn();
        dto.titulo = livro.getTitulo();
        dto.anoPublicacao = livro.getAnoPublicacao();
        dto.edicao = livro.getEdicao();
        dto.numPaginas = livro.getNumPaginas();
        dto.sinopse = livro.getSinopse();

        if (livro.getCategoria() != null) {
            dto.categoria = livro.getCategoria().getNome();
        }

        // 2. Mapeia a coleção de Autores
        dto.autores = new ArrayList<>();
        if (livro.getAutores() != null) {
            dto.autores = new ArrayList<>(livro.getAutores()).stream()
                    .map(LivroAutor::getAutor)
                    .map(Autor::getNome)
                    .collect(Collectors.toList());
        }
        
        // 3. *** LÓGICA DE BUSCA DE RESENHAS AQUI ***
        // Busca ativamente no banco de dados todas as resenhas associadas a este livro.
        dto.resenhas = new ArrayList<>();
        try {
            // Acessa o repositório estático de Resenhas para obter o DAO
            Dao<Resenha, Integer> resenhaDao = Repositorios.RESENHA.getDao();
            
            // Cria e executa a query: "SELECT * FROM resenha WHERE livro_id = ?"
            List<Resenha> resenhasDoLivro = resenhaDao.queryBuilder()
                    .where().eq("livro_id", livro.getId()).query();

            if (resenhasDoLivro != null) {
                // Mapeia a lista de entidades Resenha para uma lista de ResenhaSyncDto
                dto.resenhas = resenhasDoLivro.stream()
                        .map(ResenhaSyncDto::fromResenha)
                        .collect(Collectors.toList());
            }
        } catch (SQLException e) {
            System.err.println("ERRO: Falha ao buscar resenhas para o livro ID " + livro.getId());
            e.printStackTrace();
        }
        
        return dto;
    }
    
    // Getters para todos os campos
    public int getId() { return id; }
    public String getIsbn() { return isbn; }
    public String getTitulo() { return titulo; }
    public int getAnoPublicacao() { return anoPublicacao; }
    public String getEdicao() { return edicao; }
    public int getNumPaginas() { return numPaginas; }
    public String getSinopse() { return sinopse; }
    public String getCategoria() { return categoria; }
    public List<String> getAutores() { return autores; }
    public List<ResenhaSyncDto> getResenhas() { return resenhas; }
}