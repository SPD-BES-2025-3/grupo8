package com.livraria.integrador.processor;

import com.google.gson.Gson;
import com.livraria.integrador.model.LivroApi; // Modelo da API
import com.livraria.integrador.model.canonico.MapeamentoID;
import com.livraria.integrador.repository.RepositorioCanonico;
import com.livraria.integrador.util.DatabaseUpdater;
import model.Livro; // Modelo do Desktop
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class ApiToDesktopProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        String jsonApi = exchange.getIn().getBody(String.class);
        LivroApi livroApi = new Gson().fromJson(jsonApi, LivroApi.class);

        // Usa o ID da API para encontrar o mapeamento
        MapeamentoID mapeamento = RepositorioCanonico.mapeamentoIdDao.queryBuilder()
                .where().eq("idApi", livroApi.getId()).queryForFirst();

        if (mapeamento == null) {
            System.out.println("AVISO: Mapeamento não encontrado para o livro da API ID: " + livroApi.getId() + ". Ignorando sincronização para o desktop.");
            exchange.setRouteStop(true); // Para a execução desta rota
            return;
        }

        // Transforma para o modelo do Desktop
        Livro livroDesktop = new Livro();
        livroDesktop.setId(mapeamento.getIdDesktop()); // Usa o ID mapeado!
        livroDesktop.setTitulo(livroApi.getTitulo());
        livroDesktop.setIsbn(livroApi.getIsbn());
        // ... Mapeie todos os outros campos

        // Ação: Chamar o DatabaseUpdater para persistir a mudança no SQLite
        String operacao = exchange.getIn().getHeader("operacao", String.class);

        DatabaseUpdater.sincronizarLivro(livroDesktop, operacao);
    }
}