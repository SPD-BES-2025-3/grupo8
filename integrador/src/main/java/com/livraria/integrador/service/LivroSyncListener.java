package com.livraria.integrador.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.dao.Dao;
import com.livraria.integrador.dto.LivroApiDto;
import com.livraria.integrador.model.*; // Importa os modelos do projeto desktop
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LivroSyncListener {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final Dao<Livro, Integer> livroDao;
    private final Dao<Autor, Integer> autorDao;
    private final Dao<Categoria, Integer> categoriaDao;
    private final Dao<LivroAutor, Integer> livroAutorDao;

    @Autowired
    public LivroSyncListener(RestTemplateBuilder restTemplateBuilder,
                             Dao<Livro, Integer> livroDao,
                             Dao<Autor, Integer> autorDao,
                             Dao<Categoria, Integer> categoriaDao,
                             Dao<LivroAutor, Integer> livroAutorDao) {
        this.restTemplate = restTemplateBuilder.build();
        this.objectMapper = new ObjectMapper();
        this.livroDao = livroDao;
        this.autorDao = autorDao;
        this.categoriaDao = categoriaDao;
        this.livroAutorDao = livroAutorDao;
    }

    /**
     * Ouve a fila do Desktop (ORM) e sincroniza com a API (ODM).
     * Este fluxo é mais simples, pois apenas monta um DTO e chama a API.
     */
    @JmsListener(destination = "LIVRO_SYNC_ORM_TO_ODM")
    public void receiveMessageFromOrm(TextMessage message) throws Exception {
        String operation = message.getStringProperty("operation");
        // O ID no desktop é um Integer, mas a API usa String. Vamos converter.
        String livroOrmId = message.getStringProperty("livroOrmId");
        String jsonPayload = message.getText();

        System.out.println("[ORM->ODM] Recebido: " + operation + " para Livro ID " + livroOrmId);

        LivroApiDto dto = objectMapper.readValue(jsonPayload, LivroApiDto.class);
        String apiUrl = "http://localhost:8080/api/livros";

        switch (operation) {
            case "CREATE", "UPDATE":
                // O ID no DTO será o ID do MongoDB, que pode ser diferente do ID do ORM.
                // Usaremos um PUT para criar ou atualizar (UPSERT) na API.
                // A API precisa ter um endpoint PUT que use um ID externo, como o ISBN ou o próprio título.
                // Para simplificar, vamos assumir que a API pode receber o ID do ORM.
                restTemplate.put(apiUrl + "/" + livroOrmId, dto);
                System.out.println("[ORM->ODM] Livro '" + dto.getTitulo() + "' enviado para a API.");
                break;
            case "DELETE":
                restTemplate.delete(apiUrl + "/" + livroOrmId);
                System.out.println("[ORM->ODM] Solicitação de exclusão para o livro ID " + livroOrmId + " enviada para a API.");
                break;
        }
    }

    /**
     * Ouve a fila da API (ODM) e sincroniza com o banco de dados do Desktop (ORM).
     * Este é o fluxo mais complexo.
     */
    @JmsListener(destination = "LIVRO_SYNC_ODM_TO_ORM")
    public void receiveMessageFromOdm(TextMessage message) throws Exception {
        String operation = message.getStringProperty("operation");
        String livroApiId = message.getStringProperty("livroApiId");
        String jsonPayload = message.getText();

        System.out.println("[ODM->ORM] Recebido: " + operation + " para Livro ID " + livroApiId);

        LivroApiDto livroApi = objectMapper.readValue(jsonPayload, LivroApiDto.class);

        // Para correlacionar, precisamos de um campo comum. Vamos usar o ISBN.
        // O ideal seria ter um campo "mongoId" na tabela Livro do SQLite.
        Livro livroOrm = livroDao.queryBuilder().where().eq("isbn", livroApi.getIsbn()).queryForFirst();

        switch (operation) {
            case "CREATE", "UPDATE":
                boolean isNew = (livroOrm == null);
                if (isNew) {
                    livroOrm = new Livro();
                }

                // Preenche/Atualiza os dados simples
                livroOrm.setTitulo(livroApi.getTitulo());
                livroOrm.setIsbn(livroApi.getIsbn());
                livroOrm.setAnoPublicacao(livroApi.getAnoPublicacao());
                livroOrm.setEdicao(livroApi.getEdicao());
                livroOrm.setNumPaginas(livroApi.getNumPaginas());
                livroOrm.setSinopse(livroApi.getSinopse());

                // Trata a Categoria (busca ou cria)
                Categoria categoria = categoriaDao.queryBuilder().where().eq("nome", livroApi.getCategoria()).queryForFirst();
                if (categoria == null) {
                    categoria = new Categoria();
                    categoria.setNome(livroApi.getCategoria());
                    categoriaDao.create(categoria);
                }
                livroOrm.setCategoria(categoria);

                // Salva o livro (cria ou atualiza)
                if (isNew) {
                    livroDao.create(livroOrm);
                } else {
                    livroDao.update(livroOrm);
                }

                // Trata os Autores (busca ou cria) e a associação
                // Primeiro, remove associações antigas
                if (!isNew) {
                    List<LivroAutor> oldAssociations = livroAutorDao.queryBuilder().where().eq("livro_id", livroOrm.getId()).query();
                    if (!oldAssociations.isEmpty()) {
                        livroAutorDao.delete(oldAssociations);
                    }
                }

                // Cria novas associações
                for (String nomeAutor : livroApi.getAutores()) {
                    Autor autor = autorDao.queryBuilder().where().eq("nome", nomeAutor).queryForFirst();
                    if (autor == null) {
                        autor = new Autor();
                        autor.setNome(nomeAutor);
                        autorDao.create(autor);
                    }
                    LivroAutor novaAssociacao = new LivroAutor(livroOrm, autor);
                    livroAutorDao.create(novaAssociacao);
                }
                System.out.println("[ODM->ORM] Livro '" + livroOrm.getTitulo() + "' sincronizado no SQLite.");
                break;

            case "DELETE":
                if (livroOrm != null) {
                    // Remove associações antes de deletar o livro
                    List<LivroAutor> associations = livroAutorDao.queryBuilder().where().eq("livro_id", livroOrm.getId()).query();
                    if (!associations.isEmpty()) {
                        livroAutorDao.delete(associations);
                    }
                    livroDao.delete(livroOrm);
                    System.out.println("[ODM->ORM] Livro '" + livroOrm.getTitulo() + "' deletado do SQLite.");
                } else {
                    System.out.println("[ODM->ORM] Livro com ISBN " + livroApi.getIsbn() + " não encontrado no SQLite para deletar.");
                }
                break;
        }
    }
}