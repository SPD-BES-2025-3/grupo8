package model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de teste para o repositório de Resenhas.
 * Testa o CRUD e as associações (Livro e Usuário).
 *
 * @version 1.0
 */
public class ResenhaRepositorioTest {
    
    private Livro livroTeste;
    private Usuario usuarioTeste;
    private Cargos cargoTeste;
    private Categoria categoriaTeste;

    /**
     * Configura o ambiente de teste criando as dependências necessárias.
     */
    @BeforeEach
    void setUp() {
        // Cria todas as dependências necessárias para uma Resenha
        cargoTeste = new Cargos(); 
        cargoTeste.setName("Cargo para Resenha"); 
        Repositorios.CARGO.create(cargoTeste);

        usuarioTeste = new Usuario(); 
        usuarioTeste.setNome("Usuário de Resenha"); 
        usuarioTeste.setEmail("resenha.teste@email.com"); 
        usuarioTeste.setCargo(cargoTeste); 
        Repositorios.USUARIO.create(usuarioTeste);

        categoriaTeste = new Categoria();
        categoriaTeste.setNome("Categoria para Resenha");
        Repositorios.CATEGORIA.create(categoriaTeste);

        livroTeste = new Livro(); 
        livroTeste.setTitulo("Livro para Resenha"); 
        livroTeste.setCategoria(categoriaTeste);
        Repositorios.LIVRO.create(livroTeste);
    }

    /**
     * Limpa o ambiente de teste deletando as dependências criadas.
     */
    @AfterEach
    void tearDown() {
        // Limpa o banco de dados na ordem inversa para evitar erros de chave estrangeira
        Repositorios.LIVRO.delete(livroTeste);
        Repositorios.USUARIO.delete(usuarioTeste);
        Repositorios.CARGO.delete(cargoTeste);
        Repositorios.CATEGORIA.delete(categoriaTeste);
    }

    /**
     * Testa o ciclo completo de CRUD para a entidade Resenha.
     */
    @Test
    void testResenhaCrudCompleto() {
        // 1. CREATE
        Resenha novaResenha = new Resenha();
        novaResenha.setLivro(livroTeste);
        novaResenha.setUsuario(usuarioTeste);
        novaResenha.setNota(5);
        novaResenha.setTexto("Uma obra-prima da ficção, recomendo a todos!");
        novaResenha.setDtAvaliacao(new Date());
        
        Repositorios.RESENHA.create(novaResenha);

        assertTrue(novaResenha.getId() > 0, "O ID da resenha não foi gerado.");

        // 2. READ - Verifica todos os atributos
        Resenha resenhaSalva = Repositorios.RESENHA.loadFromId(novaResenha.getId());
        assertNotNull(resenhaSalva, "Falha ao carregar a resenha do banco.");
        assertEquals(5, resenhaSalva.getNota(), "A 'nota' não foi salva corretamente.");
        assertEquals("Uma obra-prima da ficção, recomendo a todos!", resenhaSalva.getTexto(), "O 'texto' da resenha não foi salvo corretamente.");
        assertNotNull(resenhaSalva.getDtAvaliacao(), "A 'data de avaliação' não foi salva.");
        assertEquals(livroTeste.getId(), resenhaSalva.getLivro().getId(), "O 'livro' associado à resenha está incorreto.");
        assertEquals(usuarioTeste.getId(), resenhaSalva.getUsuario().getId(), "O 'usuário' associado à resenha está incorreto.");

        // 3. UPDATE - Altera a nota e o texto da resenha
        resenhaSalva.setNota(4);
        resenhaSalva.setTexto("Após reler, achei o ritmo um pouco lento no meio.");
        Repositorios.RESENHA.update(resenhaSalva);

        Resenha resenhaAtualizada = Repositorios.RESENHA.loadFromId(resenhaSalva.getId());
        assertNotNull(resenhaAtualizada, "Falha ao recarregar a resenha após a atualização.");
        assertEquals(4, resenhaAtualizada.getNota(), "A 'nota' não foi atualizada corretamente.");
        assertEquals("Após reler, achei o ritmo um pouco lento no meio.", resenhaAtualizada.getTexto(), "O 'texto' da resenha não foi atualizado corretamente.");

        // 4. DELETE
        Repositorios.RESENHA.delete(resenhaAtualizada);
        Resenha resenhaDeletada = Repositorios.RESENHA.loadFromId(resenhaAtualizada.getId());
        assertNull(resenhaDeletada, "A resenha não foi deletada corretamente.");
    }
}