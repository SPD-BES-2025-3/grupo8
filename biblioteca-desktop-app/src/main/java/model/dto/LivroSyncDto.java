package model.dto;

import model.Autor;
import model.Livro;
import model.LivroAutor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO (Data Transfer Object) para serializar os dados de um Livro para o JMS.
 * Isso evita a serialização de objetos ORMLite complexos e resolve referências circulares.
 */
public class LivroSyncDto {

    // Campos simples
    private int id;
    private String isbn;
    private String titulo;
    private int anoPublicacao;
    private String edicao;
    private int numPaginas;
    private String sinopse;
    
    // Campos de relacionamento (simplificados)
    private String categoria;
    private List<String> autores;

    // Construtor privado para ser usado pelo método estático `fromLivro`
    private LivroSyncDto() {}

    /**
     * Constrói um DTO a partir de uma entidade Livro do ORMLite.
     */
    public static LivroSyncDto fromLivro(Livro livro) {
        LivroSyncDto dto = new LivroSyncDto();
        
        // Copia os campos simples
        dto.id = livro.getId();
        dto.isbn = livro.getIsbn();
        dto.titulo = livro.getTitulo();
        dto.anoPublicacao = livro.getAnoPublicacao();
        dto.edicao = livro.getEdicao();
        dto.numPaginas = livro.getNumPaginas();
        dto.sinopse = livro.getSinopse();

        // Extrai o nome da categoria
        if (livro.getCategoria() != null) {
            dto.categoria = livro.getCategoria().getNome();
        }

        // Extrai os nomes dos autores
        dto.autores = new ArrayList<>();
        if (livro.getAutores() != null) {
            dto.autores = new ArrayList<>(livro.getAutores()).stream()
                    .map(LivroAutor::getAutor)
                    .map(Autor::getNome)
                    .collect(Collectors.toList());
        }
        
        return dto;
    }
    
    // Getters são necessários para a serialização do Gson
    public int getId() { return id; }
    public String getTitulo() { return titulo; }

    public String getIsbn() {
        return isbn;
    }

    public int getAnoPublicacao() {
        return anoPublicacao;
    }

    public String getEdicao() {
        return edicao;
    }

    public int getNumPaginas() {
        return numPaginas;
    }

    public String getSinopse() {
        return sinopse;
    }

    public String getCategoria() {
        return categoria;
    }

    public List<String> getAutores() {
        return autores;
    }
}