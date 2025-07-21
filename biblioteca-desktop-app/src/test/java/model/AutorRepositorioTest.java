package model;

import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de teste para o repositório de Autores.
 * Testa as operações CRUD completas para a entidade Autor.
 *
 * @version 1.0
 */
public class AutorRepositorioTest {

    /**
     * Testa o ciclo completo de CRUD (Create, Read, Update, Delete) para a entidade Autor.
     */
    @Test
    void testAutorCrudCompleto() {
        // 1. CREATE
        Autor novoAutor = new Autor();
        novoAutor.setNome("George Orwell");
        novoAutor.setNacionalidade("Britânico");
        novoAutor.setDtNascimento(new Date()); // Usando a data atual como exemplo
        novoAutor.setBiografia("Autor de 1984 e A Revolução dos Bichos.");
        Repositorios.AUTOR.create(novoAutor);      
        assertNotNull(novoAutor.getId(), "Falha ao salvar o autor, ID é nulo.");
        assertTrue(novoAutor.getId() > 0, "ID do autor não foi gerado corretamente.");

        // 2. READ - Verifica todos os atributos
        Autor autorSalvo = Repositorios.AUTOR.loadFromId(novoAutor.getId());
        assertNotNull(autorSalvo, "Falha ao carregar o autor do banco.");
        assertEquals("George Orwell", autorSalvo.getNome(), "O nome não foi salvo corretamente.");
        assertEquals("Britânico", autorSalvo.getNacionalidade(), "A nacionalidade não foi salva corretamente.");
        assertEquals("Autor de 1984 e A Revolução dos Bichos.", autorSalvo.getBiografia(), "A biografia não foi salva corretamente.");
        assertNotNull(autorSalvo.getDtNascimento(), "A data de nascimento não foi salva.");

        // 3. UPDATE - Altera a biografia e verifica
        autorSalvo.setBiografia("Eric Arthur Blair, mais conhecido como George Orwell...");
        Repositorios.AUTOR.update(autorSalvo);  
        Autor autorAtualizado = Repositorios.AUTOR.loadFromId(autorSalvo.getId());
        assertNotNull(autorAtualizado, "Falha ao recarregar o autor após a atualização.");
        assertEquals("Eric Arthur Blair, mais conhecido como George Orwell...", autorAtualizado.getBiografia(), "A biografia não foi atualizada corretamente.");
        assertEquals("George Orwell", autorAtualizado.getNome(), "O nome foi alterado incorretamente durante a atualização.");

        // 4. DELETE
        Repositorios.AUTOR.delete(autorAtualizado);
        Autor autorDeletado = Repositorios.AUTOR.loadFromId(autorAtualizado.getId());
        assertNull(autorDeletado, "Falha ao deletar o autor.");
    }
}