package com.livraria.api.controller;

import com.livraria.api.model.Livro;
import com.livraria.api.model.Resenha;
import com.livraria.api.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Controlador REST responsável por gerenciar os endpoints da API para a entidade {@link Livro}.
 *
 * @author Hugo
 * @since 2025-07-20
 */
@RestController
@RequestMapping("/api/livros")
public class LivroController {

    @Autowired
    private LivroRepository livroRepository;

    /**
     * Cria um novo livro no banco de dados.
     * @param livro O objeto Livro a ser criado, vindo do corpo da requisição.
     * @return ResponseEntity com o livro criado e status 201 CREATED.
     */
    @PostMapping
    public ResponseEntity<Livro> criarLivro(@RequestBody Livro livro) {
        Livro novoLivro = livroRepository.save(livro);
        return new ResponseEntity<>(novoLivro, HttpStatus.CREATED);
    }

    /**
     * Retorna uma lista com todos os livros cadastrados.
     * @return Uma lista de objetos Livro.
     */
    @GetMapping
    public List<Livro> listarTodosLivros() {
        return livroRepository.findAll();
    }

    /**
     * Busca um livro específico pelo seu ID.
     * @param id O ID do livro a ser buscado.
     * @return ResponseEntity com o livro encontrado e status 200 OK, ou status 404 NOT FOUND se o livro não existir.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Livro> buscarLivroPorId(@PathVariable String id) {
        return livroRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Atualiza um livro existente com base em seu ID.
     * @param id O ID do livro a ser atualizado.
     * @param livroAtualizado O objeto Livro com os novos dados.
     * @return ResponseEntity com o livro atualizado e status 200 OK, ou status 404 NOT FOUND se o livro não existir.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Livro> atualizarLivro(@PathVariable String id, @RequestBody Livro livroAtualizado) {
        return livroRepository.findById(id)
                .map(livroExistente -> {
                    livroExistente.setIsbn(livroAtualizado.getIsbn());
                    livroExistente.setTitulo(livroAtualizado.getTitulo());
                    livroExistente.setAnoPublicacao(livroAtualizado.getAnoPublicacao());
                    livroExistente.setEdicao(livroAtualizado.getEdicao());
                    livroExistente.setNumPaginas(livroAtualizado.getNumPaginas());
                    livroExistente.setSinopse(livroAtualizado.getSinopse());
                    livroExistente.setAutores(livroAtualizado.getAutores());
                    livroExistente.setCategoria(livroAtualizado.getCategoria());
                    Livro salvo = livroRepository.save(livroExistente);
                    return ResponseEntity.ok(salvo);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Deleta um livro do banco de dados com base em seu ID.
     * @param id O ID do livro a ser deletado.
     * @return ResponseEntity com status 204 NO CONTENT em caso de sucesso, ou 404 NOT FOUND se o livro não existir.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarLivro(@PathVariable String id) {
        if (!livroRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        livroRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Adiciona uma nova resenha a um livro existente.
     * @param livroId O ID do livro que receberá a resenha.
     * @param resenha O objeto Resenha a ser adicionado.
     * @return ResponseEntity com o objeto Livro atualizado (incluindo a nova resenha) e status 200 OK,
     * ou status 404 NOT FOUND se o livro não existir.
     */
    @PostMapping("/{livroId}/resenhas")
    public ResponseEntity<Livro> adicionarResenha(@PathVariable String livroId, @RequestBody Resenha resenha) {
        return livroRepository.findById(livroId)
                .map(livro -> {
                    resenha.setDataAvaliacao(LocalDateTime.now());
                    livro.getResenhas().add(resenha);
                    Livro livroAtualizado = livroRepository.save(livro);
                    return ResponseEntity.ok(livroAtualizado);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}