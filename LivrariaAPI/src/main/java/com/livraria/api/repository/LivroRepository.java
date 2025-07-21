package com.livraria.api.repository;

import com.livraria.api.model.Livro;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface de repositório para operações CRUD na entidade {@link Livro}.
 * Utiliza o Spring Data MongoDB para gerar automaticamente as implementações
 * dos métodos de acesso ao banco de dados.
 *
 * @author Hugo
 * @since 2025-07-20
 */
@Repository
public interface LivroRepository extends MongoRepository<Livro, String> {

}