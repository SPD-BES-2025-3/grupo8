package model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de teste para o repositório de Usuários.
 * Testa o CRUD e a associação com a entidade Cargos.
 *
 * @version 1.0
 */
public class UsuarioRepositorioTest {
    
    private Cargos cargoTeste;

    /**
     * Configura o ambiente de teste criando um cargo de teste.
     */
    @BeforeEach
    void setUp() {
        // Antes de cada teste, cria um cargo para ser associado ao usuário.
        cargoTeste = new Cargos();
        cargoTeste.setName("Cliente de Teste");
        Repositorios.CARGO.create(cargoTeste);
    }

    /**
     * Limpa o ambiente de teste deletando o cargo de teste.
     */
    @AfterEach
    void tearDown() {
        // Após cada teste, remove o cargo criado para manter o banco limpo.
        Repositorios.CARGO.delete(cargoTeste);
    }

    /**
     * Testa o ciclo completo de CRUD para a entidade Usuario.
     */
    @Test
    void testUsuarioCrudCompleto() {
        // 1. CREATE
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome("Ana Carolina");
        novoUsuario.setEmail("ana.carolina@teste.com");
        novoUsuario.setSenha("senhaForte123");
        novoUsuario.setTelefone("62999998888");
        novoUsuario.setDtNascimento(new Date()); // Data de exemplo
        novoUsuario.setAtivo(true);
        novoUsuario.setCargo(cargoTeste); // Associa o cargo de teste
        
        Repositorios.USUARIO.create(novoUsuario);
        
        assertTrue(novoUsuario.getId() > 0, "O ID do usuário não foi gerado corretamente.");

        // 2. READ - Verifica todos os atributos
        Usuario usuarioSalvo = Repositorios.USUARIO.loadFromId(novoUsuario.getId());
        assertNotNull(usuarioSalvo, "Falha ao carregar o usuário do banco de dados.");
        assertEquals("Ana Carolina", usuarioSalvo.getNome(), "O atributo 'nome' não foi salvo corretamente.");
        assertEquals("ana.carolina@teste.com", usuarioSalvo.getEmail(), "O atributo 'email' não foi salvo corretamente.");
        assertEquals("senhaForte123", usuarioSalvo.getSenha(), "O atributo 'senha' não foi salvo corretamente.");
        assertEquals("62999998888", usuarioSalvo.getTelefone(), "O atributo 'telefone' não foi salvo corretamente.");
        assertTrue(usuarioSalvo.isAtivo(), "O atributo 'isAtivo' não foi salvo corretamente.");
        assertNotNull(usuarioSalvo.getDtNascimento(), "O atributo 'dtNascimento' não foi salvo.");
        assertNotNull(usuarioSalvo.getCargo(), "O 'cargo' não foi associado corretamente.");
        assertEquals(cargoTeste.getId(), usuarioSalvo.getCargo().getId(), "O ID do cargo associado está incorreto.");

        // 3. UPDATE - Altera o telefone e o status, e verifica se os outros dados permanecem
        usuarioSalvo.setTelefone("62911112222");
        usuarioSalvo.setAtivo(false);
        Repositorios.USUARIO.update(usuarioSalvo);
        
        Usuario usuarioAtualizado = Repositorios.USUARIO.loadFromId(usuarioSalvo.getId());
        assertNotNull(usuarioAtualizado, "Falha ao recarregar o usuário após a atualização.");
        assertEquals("62911112222", usuarioAtualizado.getTelefone(), "O 'telefone' não foi atualizado corretamente.");
        assertFalse(usuarioAtualizado.isAtivo(), "O status 'isAtivo' não foi atualizado corretamente.");
        assertEquals("Ana Carolina", usuarioAtualizado.getNome(), "O 'nome' foi alterado indevidamente durante a atualização.");

        // 4. DELETE
        Repositorios.USUARIO.delete(usuarioAtualizado);
        Usuario usuarioDeletado = Repositorios.USUARIO.loadFromId(usuarioAtualizado.getId());
        assertNull(usuarioDeletado, "O usuário não foi deletado corretamente.");
    }
}