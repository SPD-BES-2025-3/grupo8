package com.livraria.api.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

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
    private List<Resenha> resenhas = new ArrayList<>();
}