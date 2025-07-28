package com.livraria.integrador.model;

import java.time.LocalDateTime;

/**
 * Representa uma resenha de um livro. Esta classe é projetada para ser um
 * documento embutido dentro da entidade {@link Livro}.
 *
 * @author Hugo
 * @since 2025-07-20
 */

public class Resenha {

    /**
     * O nome do usuário que escreveu a resenha.
     */
    private String nomeUsuario;

    /**
     * A nota atribuída ao livro (ex: 1 a 5).
     */
    private int nota;

    /**
     * O texto descritivo da resenha.
     */
    private String texto;

    /**
     * A data e hora em que a avaliação foi criada.
     * É preenchida automaticamente no momento da criação.
     */
    private LocalDateTime dataAvaliacao;

    /**
     * Construtor padrão que inicializa a data de avaliação com o momento atual.
     */
    public Resenha() {
        this.dataAvaliacao = LocalDateTime.now();
    }
}