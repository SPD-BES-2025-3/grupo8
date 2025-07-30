package com.livraria.integrador.model;

import java.time.LocalDateTime;

// Esta é a classe DTO que representa a Resenha DENTRO do integrador.
public class ResenhaApi {

    private String nomeUsuario;
    private int nota;
    private String texto;
    private LocalDateTime dataAvaliacao;

    public ResenhaApi() {
        this.dataAvaliacao = LocalDateTime.now();
    }

    // Getters e Setters
    public String getNomeUsuario() { return nomeUsuario; }
    public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }
    public int getNota() { return nota; }
    public void setNota(int nota) { this.nota = nota; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public LocalDateTime getDataAvaliacao() { return dataAvaliacao; }
    public void setDataAvaliacao(LocalDateTime dataAvaliacao) { this.dataAvaliacao = dataAvaliacao; }
}