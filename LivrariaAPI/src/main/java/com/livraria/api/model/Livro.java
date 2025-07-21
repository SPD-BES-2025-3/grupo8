package com.livraria.api.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa a entidade Livro, mapeada para a coleção "livros" no MongoDB.
 * Contém todas as informações de um livro, bem como uma lista embutida de resenhas.
 *
 * @author Hugo
 * @since 2025-07-20
 */
@Document(collection = "livros")
@Data
public class Livro {

    @Id
    private String id;
    private String isbn;
    private String titulo;
    private int anoPublicacao;
    private String edicao;
    private int numPaginas;
    private String sinopse;
    private List<String> autores;
    private String categoria;

    /**
     * Lista de resenhas embutidas associadas a este livro.
     */
    private List<Resenha> resenhas = new ArrayList<>();
}