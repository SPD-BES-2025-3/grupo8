package com.livraria.integrador.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class LivroApi {

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