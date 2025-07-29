package com.livraria.integrador.processor;

import com.google.gson.Gson;
import com.livraria.integrador.model.canonico.MapeamentoID;
import com.livraria.integrador.repository.RepositorioCanonico;
import com.livraria.integrador.util.DatabaseUpdater;
import model.Livro;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import com.livraria.integrador.model.LivroApi;

import java.util.Date;
import java.util.UUID;

public class ApiToDesktopProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        String jsonApi = exchange.getIn().getBody(String.class);
        LivroApi livroApi = new Gson().fromJson(jsonApi, LivroApi.class);

        String operacao = exchange.getIn().getHeader("operacao", String.class);
        String idApi = exchange.getIn().getHeader("idApi", String.class);

        MapeamentoID mapeamento = RepositorioCanonico.mapeamentoIdDao.queryBuilder()
            .where().eq("idApi", idApi).queryForFirst();
        
        Integer idDesktop = 0; // Padrão para CREATE

        if (mapeamento != null) {
            // Se o mapeamento existe, usamos o ID do desktop correspondente
            idDesktop = mapeamento.getIdDesktop();
        } else if (!"CREATE".equals(operacao)) {
            // Se é um UPDATE ou DELETE e o mapeamento não existe, é um erro.
            System.err.println("ERRO: Mapeamento não encontrado para o livro da API ID: " + idApi + ". Sincronização de UPDATE/DELETE falhou.");
            exchange.setRouteStop(true);
            return;
        }

        // Chama o utilitário para realizar a operação no banco de dados do Desktop
        Livro livroSalvoNoDesktop = DatabaseUpdater.sincronizarLivro(livroApi, idDesktop, operacao);

        // Se a operação foi um DELETE, removemos o mapeamento
        if ("DELETE".equals(operacao) && mapeamento != null) {
            RepositorioCanonico.mapeamentoIdDao.delete(mapeamento);
            System.out.println("Mapeamento removido após DELETE vindo da API.");
        }

        // *** LÓGICA CRÍTICA ADICIONADA AQUI ***
        // Se foi uma criação a partir da API, precisamos criar o mapeamento agora.
        if (mapeamento == null && "CREATE".equals(operacao) && livroSalvoNoDesktop != null) {
            String idCanonico = UUID.randomUUID().toString();
            MapeamentoID novoMapeamento = new MapeamentoID();
            novoMapeamento.setIdCanonico(idCanonico);
            novoMapeamento.setIdApi(livroApi.getId());
            novoMapeamento.setIdDesktop(livroSalvoNoDesktop.getId()); // Usa o novo ID retornado pelo updater!
            novoMapeamento.setUltimaAtualizacao(new Date());
            
            RepositorioCanonico.mapeamentoIdDao.create(novoMapeamento);
            System.out.println("Novo mapeamento criado a partir da API. ID Canônico: " + idCanonico);
        }
    }
}