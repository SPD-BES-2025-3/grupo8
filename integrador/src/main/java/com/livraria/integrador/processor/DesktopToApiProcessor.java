package com.livraria.integrador.processor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder; // Importe o GsonBuilder
import com.livraria.integrador.model.canonico.MapeamentoID;
import com.livraria.integrador.repository.RepositorioCanonico;
import com.livraria.integrador.util.LocalDateTimeAdapter;

import model.dto.LivroSyncDto;
import com.livraria.integrador.model.LivroApi;
import com.livraria.integrador.model.ResenhaApi;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class DesktopToApiProcessor implements Processor {
    
    private final Gson gson;

    public DesktopToApiProcessor() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .disableHtmlEscaping()
                .create();
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String jsonDesktopDto = exchange.getIn().getBody(String.class);
        LivroSyncDto livroDesktopDto = new Gson().fromJson(jsonDesktopDto, LivroSyncDto.class);
        
        exchange.getIn().setHeader("entidadeOriginal", livroDesktopDto);
        exchange.getIn().setHeader("idDesktop", livroDesktopDto.getId());
        
        String operacao = exchange.getIn().getHeader("operacao", String.class);
        if ("UPDATE".equals(operacao) || "DELETE".equals(operacao)) {
            MapeamentoID mapeamento = RepositorioCanonico.mapeamentoIdDao.queryBuilder()
                .where().eq("idDesktop", livroDesktopDto.getId()).queryForFirst();
            
            if (mapeamento == null) {
                System.err.println("ERRO: Mapeamento não encontrado para o livro do desktop ID: " + livroDesktopDto.getId() + ".");
                exchange.setRouteStop(true); 
                return;
            }
            exchange.getIn().setHeader("idApi", mapeamento.getIdApi());
        }

        LivroApi livroApi = new LivroApi();
        
        livroApi.setTitulo(livroDesktopDto.getTitulo());
        livroApi.setIsbn(livroDesktopDto.getIsbn());
        livroApi.setAnoPublicacao(livroDesktopDto.getAnoPublicacao());
        livroApi.setEdicao(livroDesktopDto.getEdicao());
        livroApi.setNumPaginas(livroDesktopDto.getNumPaginas());
        livroApi.setSinopse(livroDesktopDto.getSinopse());
        livroApi.setCategoria(livroDesktopDto.getCategoria());
        livroApi.setAutores(livroDesktopDto.getAutores() != null ? livroDesktopDto.getAutores() : new ArrayList<>());

        if (livroDesktopDto.getResenhas() != null) {
            livroApi.setResenhas(
                livroDesktopDto.getResenhas().stream().map(resenhaDto -> {
                    ResenhaApi resenhaApi = new ResenhaApi();
                    resenhaApi.setTexto(resenhaDto.getTexto());
                    resenhaApi.setNomeUsuario(resenhaDto.getNomeUsuario());
                    resenhaApi.setNota(resenhaDto.getNota());
                    if (resenhaDto.getDataAvaliacao() != null) {
                        ZonedDateTime zdt = ZonedDateTime.parse(resenhaDto.getDataAvaliacao(), DateTimeFormatter.ISO_DATE_TIME);
                        resenhaApi.setDataAvaliacao(zdt.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime());
                    }
                    return resenhaApi;
                }).collect(Collectors.toList())
            );
            Gson gson = new Gson();
            System.out.println(gson.toJson(livroApi));
        } else {
            livroApi.setResenhas(new ArrayList<>());
        }

        // Usa a instância configurada para criar o JSON final
        exchange.getIn().setBody(gson.toJson(livroApi));
    }
}