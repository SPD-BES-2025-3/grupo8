package com.livraria.integrador.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * Data Transfer Object (DTO) que representa a estrutura de um Livro
 * conforme esperado pela API RESTful (ODM).
 * <p>
 * Esta classe é usada pelo Integrador para serializar e desserializar
 * os dados do livro ao se comunicar com a API. A anotação
 * {@code @JsonIgnoreProperties(ignoreUnknown = true)} garante que, se a API
 * enviar campos extras, eles serão ignorados sem causar erros.
 */
// Adiciona flexibilidade para ignorar campos desconhecidos durante a desserialização
@JsonIgnoreProperties(ignoreUnknown = true)
public class LivroApiDto {

    private String id;
    private String isbn;
    private String titulo;
    private int anoPublicacao;
    private String edicao;
    private int numPaginas;
    private String sinopse;
    private List<String> autores;
    private String categoria;

    // As resenhas são gerenciadas por um endpoint separado na API,
    // então geralmente não são incluídas no DTO principal de sincronização de livros.
    // private List<ResenhaApiDto> resenhas; // Opcional, se necessário

    // Construtor padrão (necessário para Jackson/JSON)
    public LivroApiDto() {
    }

    // Getters e Setters para todos os campos

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getAnoPublicacao() {
        return anoPublicacao;
    }

    public void setAnoPublicacao(int anoPublicacao) {
        this.anoPublicacao = anoPublicacao;
    }

    public String getEdicao() {
        return edicao;
    }

    public void setEdicao(String edicao) {
        this.edicao = edicao;
    }

    public int getNumPaginas() {
        return numPaginas;
    }

    public void setNumPaginas(int numPaginas) {
        this.numPaginas = numPaginas;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    public List<String> getAutores() {
        return autores;
    }

    public void setAutores(List<String> autores) {
        this.autores = autores;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    @Override
    public String toString() {
        return "LivroApiDto{" +
                "id='" + id + '\'' +
                ", titulo='" + titulo + '\'' +
                ", autores=" + autores +
                '}';
    }
}