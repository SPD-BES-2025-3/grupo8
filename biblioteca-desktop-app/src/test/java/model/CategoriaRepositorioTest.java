package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de teste para o repositório de Categorias.
 * Testa as operações CRUD completas para a entidade Categoria.
 *
 * @version 1.0
 */
public class CategoriaRepositorioTest {

    /**
     * Testa o ciclo completo de CRUD (Create, Read, Update, Delete) para a entidade Categoria.
     */
    @Test
    void testCategoriaCrudCompleto() {
        // 1. CREATE
        Categoria novaCategoria = new Categoria();
        novaCategoria.setNome("Fantasia Épica");
        novaCategoria.setDescricao("Gênero caracterizado por mundos mágicos e grandes jornadas.");
        
        Repositorios.CATEGORIA.create(novaCategoria);
        
        assertNotNull(novaCategoria.getId(), "Falha ao salvar a categoria, o ID não deveria ser nulo.");
        assertTrue(novaCategoria.getId() > 0, "O ID da categoria deve ser um número positivo.");

        // 2. READ - Verifica todos os atributos
        Categoria categoriaSalva = Repositorios.CATEGORIA.loadFromId(novaCategoria.getId());
        assertNotNull(categoriaSalva, "Não foi possível carregar a categoria do banco de dados.");
        assertEquals("Fantasia Épica", categoriaSalva.getNome(), "O atributo 'nome' não foi salvo corretamente.");
        assertEquals("Gênero caracterizado por mundos mágicos e grandes jornadas.", categoriaSalva.getDescricao(), "O atributo 'descricao' não foi salvo corretamente.");

        // 3. UPDATE - Altera a descrição e verifica se o nome permanece o mesmo
        categoriaSalva.setDescricao("Obras de alta fantasia com construção de mundo complexa.");
        Repositorios.CATEGORIA.update(categoriaSalva);
        
        Categoria categoriaAtualizada = Repositorios.CATEGORIA.loadFromId(categoriaSalva.getId());
        assertNotNull(categoriaAtualizada, "Falha ao recarregar a categoria após a atualização.");
        assertEquals("Obras de alta fantasia com construção de mundo complexa.", categoriaAtualizada.getDescricao(), "O atributo 'descricao' não foi atualizado corretamente.");
        assertEquals("Fantasia Épica", categoriaAtualizada.getNome(), "O atributo 'nome' foi alterado indevidamente durante a atualização.");

        // 4. DELETE
        Repositorios.CATEGORIA.delete(categoriaAtualizada);
        Categoria categoriaDeletada = Repositorios.CATEGORIA.loadFromId(categoriaAtualizada.getId());
        assertNull(categoriaDeletada, "A categoria não foi deletada corretamente do banco de dados.");
    }
}