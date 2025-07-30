package com.livraria.integrador.processor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.livraria.integrador.model.canonico.MapeamentoID;
import com.livraria.integrador.repository.RepositorioCanonico;
import com.livraria.integrador.util.DatabaseUpdater;
import com.livraria.integrador.util.LocalDateTimeAdapter; // Verifique se o import está presente
import model.Livro;
import com.livraria.integrador.model.LivroApi;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.time.LocalDateTime; // Verifique se o import está presente
import java.util.Date;
import java.util.UUID;

public class ApiToDesktopProcessor implements Processor {

    // Cria uma instância do Gson com o adaptador de data
    private final Gson gson;

    public ApiToDesktopProcessor() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String jsonApi = exchange.getIn().getBody(String.class);

        // Usa a instância do Gson configurada para desserializar
        LivroApi livroApi = this.gson.fromJson(jsonApi, LivroApi.class);

        String operacao = exchange.getIn().getHeader("operacao", String.class);
        String idApi = exchange.getIn().getHeader("idApi", String.class);

        MapeamentoID mapeamento = RepositorioCanonico.mapeamentoIdDao.queryBuilder()
                .where().eq("idApi", idApi).queryForFirst();

        Integer idDesktop = 0;

        if (mapeamento != null) {
            idDesktop = mapeamento.getIdDesktop();
        } else if (!"CREATE".equals(operacao)) {
            System.err.println("ERRO: Mapeamento não encontrado para o livro da API ID: " + idApi + ".");
            exchange.setRouteStop(true);
            return;
        }
        if ("DELETE".equals(operacao) && mapeamento != null) {
            RepositorioCanonico.mapeamentoIdDao.delete(mapeamento);
            System.out.println("Mapeamento removido após DELETE vindo da API.");
            return; // Adicionado para parar o processamento
        }

        Livro livroSalvoNoDesktop = DatabaseUpdater.sincronizarLivro(livroApi, idDesktop, operacao);

        // Lógica de criação de mapeamento para eventos originados na API
        if (mapeamento == null && "CREATE".equals(operacao) && livroSalvoNoDesktop != null) {

            // VERIFICAÇÃO DEFENSIVA: Antes de criar, verifica se o outro processo já não o fez.
            MapeamentoID finalCheck = RepositorioCanonico.mapeamentoIdDao.queryBuilder()
                    .where().eq("idDesktop", livroSalvoNoDesktop.getId())
                    .queryForFirst();

            if (finalCheck == null) {
                // Só cria se o mapeamento realmente não existir
                String idCanonico = UUID.randomUUID().toString();
                MapeamentoID novoMapeamento = new MapeamentoID();
                novoMapeamento.setIdCanonico(idCanonico);
                novoMapeamento.setIdApi(livroApi.getId());
                novoMapeamento.setIdDesktop(livroSalvoNoDesktop.getId());
                novoMapeamento.setUltimaAtualizacao(new Date());

                RepositorioCanonico.mapeamentoIdDao.create(novoMapeamento);
                System.out.println("Novo mapeamento criado a partir da API. ID Canônico: " + idCanonico);
            } else {
                // Log para indicar que a criação foi pulada para evitar o erro
                System.out.println("AVISO (API->Desktop): Mapeamento para Desktop ID " + livroSalvoNoDesktop.getId() + " já existe. Não será criado novamente.");
            }
        }
    }
}