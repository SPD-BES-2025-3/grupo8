package com.livraria.integrador.processor;

import com.google.gson.Gson;
import com.livraria.integrador.model.canonico.MapeamentoID;
import com.livraria.integrador.repository.RepositorioCanonico;
import model.dto.LivroSyncDto;
import com.livraria.integrador.model.LivroApi;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.ArrayList;

public class DesktopToApiProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        String jsonDesktopDto = exchange.getIn().getBody(String.class);
        Gson gson = new Gson();

        LivroSyncDto livroDesktopDto = gson.fromJson(jsonDesktopDto, LivroSyncDto.class);
        
        // Guarda o DTO e o ID do desktop para uso posterior
        exchange.getIn().setHeader("entidadeOriginal", livroDesktopDto); // Usar 'entidadeOriginal'
        exchange.getIn().setHeader("idDesktop", livroDesktopDto.getId());
        
        // *** LÓGICA CRÍTICA ADICIONADA AQUI ***
        String operacao = exchange.getIn().getHeader("operacao", String.class);
        if ("UPDATE".equals(operacao) || "DELETE".equals(operacao)) {
            // Para updates ou deletes, precisamos encontrar o ID da API
            MapeamentoID mapeamento = RepositorioCanonico.mapeamentoIdDao.queryBuilder()
                .where().eq("idDesktop", livroDesktopDto.getId()).queryForFirst();
            
            if (mapeamento == null) {
                System.err.println("ERRO: Mapeamento não encontrado para o livro do desktop ID: " + livroDesktopDto.getId() + ". A sincronização não pode continuar.");
                exchange.setRouteStop(true); // Para a rota para evitar o erro 404
                return;
            }
            
            // Coloca o ID correto da API no header para a rota usar
            exchange.getIn().setHeader("idApi", mapeamento.getIdApi());
        }

        // Cria o objeto do modelo da API
        LivroApi livroApi = new LivroApi();
        
        // Mapeamento dos campos
        livroApi.setTitulo(livroDesktopDto.getTitulo());
        livroApi.setIsbn(livroDesktopDto.getIsbn());
        livroApi.setAnoPublicacao(livroDesktopDto.getAnoPublicacao());
        livroApi.setEdicao(livroDesktopDto.getEdicao());
        livroApi.setNumPaginas(livroDesktopDto.getNumPaginas());
        livroApi.setSinopse(livroDesktopDto.getSinopse());
        livroApi.setCategoria(livroDesktopDto.getCategoria());
        livroApi.setAutores(livroDesktopDto.getAutores() != null ? livroDesktopDto.getAutores() : new ArrayList<>());
        livroApi.setResenhas(new ArrayList<>()); // Resenhas são gerenciadas por outro endpoint

        exchange.getIn().setBody(gson.toJson(livroApi));
    }
}