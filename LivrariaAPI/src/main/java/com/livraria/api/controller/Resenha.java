package com.livraria.api.controller;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Resenha {

    private String nomeUsuario;
    private int nota;
    private String texto;
    private LocalDateTime dataAvaliacao;

    public Resenha() {
        this.dataAvaliacao = LocalDateTime.now();
    }
}