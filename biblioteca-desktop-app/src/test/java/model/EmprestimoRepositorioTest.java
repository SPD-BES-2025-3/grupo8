package model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de teste para o repositório de Empréstimos.
 * Testa as operações CRUD completas para a entidade Empréstimo,
 * incluindo a configuração de suas dependências.
 *
 * @version 1.0
 */
public class EmprestimoRepositorioTest {

    private Livro livroTeste;
    private Usuario usuarioTeste;
    private Cargos cargoTeste;
    private Categoria categoriaTeste; // Adicionado para o livro

    /**
     * Configura o ambiente de teste antes de cada método de teste,
     * criando as entidades dependentes (Cargo, Usuário, Categoria, Livro).
     */
    @BeforeEach
    void setUp() {
        // Cria todas as dependências necessárias para um empréstimo
        cargoTeste = new Cargos();
        cargoTeste.setName("Cargo Empréstimo Teste");
        Repositorios.CARGO.create(cargoTeste);

        usuarioTeste = new Usuario();
        usuarioTeste.setNome("Usuario de Empréstimo");
        usuarioTeste.setEmail("emprestimo.teste@email.com");
        usuarioTeste.setCargo(cargoTeste);
        Repositorios.USUARIO.create(usuarioTeste);

        categoriaTeste = new Categoria();
        categoriaTeste.setNome("Categoria Empréstimo");
        Repositorios.CATEGORIA.create(categoriaTeste);

        livroTeste = new Livro();
        livroTeste.setTitulo("Livro para Empréstimo");
        livroTeste.setCategoria(categoriaTeste);
        Repositorios.LIVRO.create(livroTeste);
    }

    /**
     * Limpa o ambiente de teste após cada método de teste,
     * deletando as entidades criadas na ordem inversa de dependência.
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
     * Testa o ciclo completo de CRUD (Create, Read, Update, Delete) para a entidade Empréstimo.
     */
    @Test
    void testEmprestimoCrudCompleto() {
        // 1. CREATE
        Emprestimo novoEmprestimo = new Emprestimo();
        novoEmprestimo.setLivro(livroTeste);
        novoEmprestimo.setUsuario(usuarioTeste);
        novoEmprestimo.setStatus("ATIVO");
        novoEmprestimo.setDtEmprestimo(new Date()); // Data de hoje
        
        // Data de devolução prevista para 14 dias a partir de hoje
        LocalDate hoje = LocalDate.now();
        LocalDate devolucaoPrevista = hoje.plusDays(14);
        novoEmprestimo.setDtPrevistaDevolucao(Date.from(devolucaoPrevista.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        
        Repositorios.EMPRESTIMO.create(novoEmprestimo);

        assertTrue(novoEmprestimo.getId() > 0, "O ID do empréstimo não foi gerado.");

        // 2. READ - Verifica todos os atributos
        Emprestimo emprestimoSalvo = Repositorios.EMPRESTIMO.loadFromId(novoEmprestimo.getId());
        assertNotNull(emprestimoSalvo, "Falha ao carregar o empréstimo do banco.");
        assertEquals("ATIVO", emprestimoSalvo.getStatus(), "O status não foi salvo corretamente.");
        assertNotNull(emprestimoSalvo.getDtEmprestimo(), "A data de empréstimo não foi salva.");
        assertNotNull(emprestimoSalvo.getDtPrevistaDevolucao(), "A data de devolução prevista não foi salva.");
        assertNull(emprestimoSalvo.getDtDevolucaoReal(), "A data de devolução real deveria ser nula na criação.");
        assertEquals(livroTeste.getId(), emprestimoSalvo.getLivro().getId(), "O livro associado está incorreto.");
        assertEquals(usuarioTeste.getId(), emprestimoSalvo.getUsuario().getId(), "O usuário associado está incorreto.");

        // 3. UPDATE - Simula a devolução do livro
        emprestimoSalvo.setStatus("DEVOLVIDO");
        emprestimoSalvo.setDtDevolucaoReal(new Date());
        Repositorios.EMPRESTIMO.update(emprestimoSalvo);

        Emprestimo emprestimoAtualizado = Repositorios.EMPRESTIMO.loadFromId(emprestimoSalvo.getId());
        assertNotNull(emprestimoAtualizado, "Falha ao recarregar o empréstimo após a atualização.");
        assertEquals("DEVOLVIDO", emprestimoAtualizado.getStatus(), "O status não foi atualizado corretamente.");
        assertNotNull(emprestimoAtualizado.getDtDevolucaoReal(), "A data de devolução real não foi atualizada.");

        // 4. DELETE
        Repositorios.EMPRESTIMO.delete(emprestimoAtualizado);
        Emprestimo emprestimoDeletado = Repositorios.EMPRESTIMO.loadFromId(emprestimoAtualizado.getId());
        assertNull(emprestimoDeletado, "O empréstimo não foi deletado corretamente.");
    }
}