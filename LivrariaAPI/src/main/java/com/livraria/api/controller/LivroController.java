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

@RestController
@RequestMapping("/api/livros")
public class LivroController {

    @Autowired
    private LivroRepository livroRepository;


    @PostMapping
    public ResponseEntity<Livro> criarLivro(@RequestBody Livro livro) {
        Livro novoLivro = livroRepository.save(livro);
        return new ResponseEntity<>(novoLivro, HttpStatus.CREATED);
    }

    @GetMapping
    public List<Livro> listarTodosLivros() {
        return livroRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Livro> buscarLivroPorId(@PathVariable String id) {
        Optional<Livro> livro = livroRepository.findById(id);
        return livro.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarLivro(@PathVariable String id) {
        if (!livroRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        livroRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{livroId}/resenhas")
    public ResponseEntity<Livro> adicionarResenha(@PathVariable String livroId, @RequestBody Resenha resenha) {

        Optional<Livro> livroOptional = livroRepository.findById(livroId);

        if (livroOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Livro livro = livroOptional.get();
        resenha.setDataAvaliacao(LocalDateTime.now());

        livro.getResenhas().add(resenha);

        Livro livroAtualizado = livroRepository.save(livro);

        return ResponseEntity.ok(livroAtualizado);
    }
}