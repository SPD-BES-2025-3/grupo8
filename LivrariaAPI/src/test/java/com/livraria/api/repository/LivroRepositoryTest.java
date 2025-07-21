package com.livraria.api.repository;

import com.livraria.api.model.Livro;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

/**
 * Testes de integração para a camada de repositório LivroRepository.
 * Utiliza um banco de dados MongoDB em memória.
 */
@DataMongoTest
class LivroRepositoryTest {

    @Autowired
    private LivroRepository livroRepository;

    @AfterEach
    void tearDown() {
        livroRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve salvar um livro com sucesso no banco de dados")
    void save_DeveSalvarLivro_QuandoDadosCompletos() {
        Livro novoLivro = new Livro();
        novoLivro.setTitulo("A Arte da Guerra");
        novoLivro.setAutores(java.util.Collections.singletonList("Sun Tzu"));

        Livro livroSalvo = livroRepository.save(novoLivro);

        assertThat(livroSalvo).isNotNull();
        assertThat(livroSalvo.getId()).isNotNull();
        assertThat(livroSalvo.getTitulo()).isEqualTo("A Arte da Guerra");
    }

    @Test
    @DisplayName("Deve encontrar um livro pelo ID se ele existir")
    void findById_DeveRetornarLivro_QuandoIdExiste() {
        Livro novoLivro = new Livro();
        novoLivro.setTitulo("O Príncipe");
        Livro livroSalvo = livroRepository.save(novoLivro);

        Optional<Livro> livroEncontrado = livroRepository.findById(livroSalvo.getId());

        assertThat(livroEncontrado).isPresent();
        assertThat(livroEncontrado.get().getId()).isEqualTo(livroSalvo.getId());
    }
}
