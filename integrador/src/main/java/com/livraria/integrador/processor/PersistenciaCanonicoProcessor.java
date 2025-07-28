package com.livraria.integrador.processor;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.livraria.integrador.model.canonico.MapeamentoID;
import com.livraria.integrador.repository.RepositorioCanonico;
import model.Livro;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import java.util.Date;
import java.util.UUID;

public class PersistenciaCanonicoProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        // Recupera o objeto original do Desktop que foi guardado no header
        Livro livroDesktop = exchange.getIn().getHeader("entidadeOriginal", Livro.class);
        if (livroDesktop == null) {
            System.err.println("ERRO: Objeto 'livroDesktop' não encontrado no header.");
            return;
        }

        // Pega a resposta da API (que está no corpo da mensagem)
        String respostaApiJson = exchange.getIn().getBody(String.class);
        JsonObject respostaJsonObj = new Gson().fromJson(respostaApiJson, JsonObject.class);
        String idApi = respostaJsonObj.get("id").getAsString();

        // Busca por um mapeamento existente usando o ID do Desktop
        MapeamentoID mapeamento = RepositorioCanonico.mapeamentoIdDao.queryBuilder()
                .where().eq("idDesktop", livroDesktop.getId()).queryForFirst();

        if (mapeamento != null) {
            // Se já existe, apenas atualiza o ID da API (se mudou) e a data
            mapeamento.setIdApi(idApi);
            mapeamento.setUltimaAtualizacao(new Date());
            RepositorioCanonico.mapeamentoIdDao.update(mapeamento);
            System.out.println("Mapeamento atualizado. ID Canônico: " + mapeamento.getIdCanonico());
        } else {
            // Se não existe, cria um novo mapeamento
            String idCanonico = UUID.randomUUID().toString();

            MapeamentoID novoMapeamento = new MapeamentoID();
            novoMapeamento.setIdCanonico(idCanonico);
            novoMapeamento.setIdDesktop(livroDesktop.getId());
            novoMapeamento.setIdApi(idApi);
            novoMapeamento.setUltimaAtualizacao(new Date());

            RepositorioCanonico.mapeamentoIdDao.create(novoMapeamento);
            System.out.println("Novo mapeamento criado. ID Canônico: " + idCanonico);
        }
    }
}