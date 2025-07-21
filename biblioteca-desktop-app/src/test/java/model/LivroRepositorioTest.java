package model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de teste para o repositório de Livros.
 * Testa o CRUD e as associações (Categoria e Autores).
 *
 * @version 1.0
 */
public class LivroRepositorioTest {

    private Autor autorTeste;
    private Categoria categoriaTeste;

    /**
     * Configura o ambiente de teste criando as dependências (Autor e Categoria).
     */
    @BeforeEach
    void setUp() {
        // Cria as dependências que serão associadas ao livro
        autorTeste = new Autor();
        autorTeste.setNome("Frank Herbert");
        Repositorios.AUTOR.create(autorTeste);

        categoriaTeste = new Categoria();
        categoriaTeste.setNome("Ficção Científica");
        Repositorios.CATEGORIA.create(categoriaTeste);
    }

    /**
     * Limpa o ambiente de teste deletando as dependências criadas.
     */
    @AfterEach
    void tearDown() {
        // Limpa as dependências criadas
        Repositorios.AUTOR.delete(autorTeste);
        Repositorios.CATEGORIA.delete(categoriaTeste);
    }

    /**
     * Testa o ciclo completo de CRUD para a entidade Livro e suas associações.
     */
    @Test
    void testLivroCrudCompletoEAssociacoes() {
        // 1. CREATE
        Livro novoLivro = new Livro();
        novoLivro.setTitulo("Duna");
        novoLivro.setIsbn("978-8576570533");
        novoLivro.setAnoPublicacao(1965);
        novoLivro.setEdicao("1ª");
        novoLivro.setNumPaginas(680);
        novoLivro.setSinopse("Uma história de aventura e misticismo em um futuro distante.");
        novoLivro.setCategoria(categoriaTeste); // Associa a categoria
        
        Repositorios.LIVRO.create(novoLivro);
        assertTrue(novoLivro.getId() > 0, "O ID do livro não foi gerado.");

        // Adiciona a relação Muitos-para-Muitos com o Autor
        LivroAutor livroAutor = new LivroAutor(novoLivro, autorTeste);
        Repositorios.LIVRO_AUTOR.create(livroAutor);
        assertTrue(livroAutor.getId() > 0, "A associação Livro-Autor falhou.");

        // 2. READ - Verifica todos os atributos e relacionamentos
        Livro livroSalvo = Repositorios.LIVRO.loadFromId(novoLivro.getId());
        assertNotNull(livroSalvo, "Falha ao carregar o livro do banco.");
        assertEquals("Duna", livroSalvo.getTitulo(), "O 'título' não foi salvo corretamente.");
        assertEquals("978-8576570533", livroSalvo.getIsbn(), "O 'isbn' não foi salvo corretamente.");
        assertEquals(1965, livroSalvo.getAnoPublicacao(), "O 'anoPublicacao' não foi salvo corretamente.");
        assertEquals("1ª", livroSalvo.getEdicao(), "A 'edicao' não foi salva corretamente.");
        assertEquals(680, livroSalvo.getNumPaginas(), "O 'numPaginas' não foi salvo corretamente.");
        assertEquals("Uma história de aventura e misticismo em um futuro distante.", livroSalvo.getSinopse(), "A 'sinopse' não foi salva corretamente.");
        
        // Verifica relacionamentos
        assertNotNull(livroSalvo.getCategoria(), "A categoria não foi associada.");
        assertEquals(categoriaTeste.getId(), livroSalvo.getCategoria().getId());
        assertEquals(1, livroSalvo.getAutores().size(), "A associação com o autor falhou.");
        assertEquals(autorTeste.getId(), livroSalvo.getAutores().iterator().next().getAutor().getId());
        
        // 3. UPDATE
        livroSalvo.setEdicao("Edição de Colecionador");
        livroSalvo.setNumPaginas(712);
        Repositorios.LIVRO.update(livroSalvo);
        
        Livro livroAtualizado = Repositorios.LIVRO.loadFromId(livroSalvo.getId());
        assertNotNull(livroAtualizado, "Falha ao recarregar o livro após a atualização.");
        assertEquals("Edição de Colecionador", livroAtualizado.getEdicao(), "O campo 'edicao' não foi atualizado.");
        assertEquals(712, livroAtualizado.getNumPaginas(), "O campo 'numPaginas' não foi atualizado.");
        assertEquals("Duna", livroAtualizado.getTitulo(), "O campo 'titulo' foi alterado indevidamente.");
        
        // 4. DELETE
        // Primeiro, é necessário remover as associações da tabela de junção
        for (LivroAutor la : livroAtualizado.getAutores()) {
            Repositorios.LIVRO_AUTOR.delete(la);
        }
        // Agora, o livro pode ser deletado
        Repositorios.LIVRO.delete(livroAtualizado);
        
        assertNull(Repositorios.LIVRO.loadFromId(livroAtualizado.getId()), "Falha ao deletar o livro.");
    }
}