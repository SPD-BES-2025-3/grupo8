package com.livraria.integrador.processor;

import com.google.gson.Gson;
import model.Autor; // Importação necessária
import model.Livro;// Modelo do Desktop
import com.livraria.integrador.model.LivroApi;
import model.LivroAutor; // Importação necessária
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

public class DesktopToApiProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        String jsonDesktop = exchange.getIn().getBody(String.class);
        Gson gson = new Gson();

        // Desserializa para o objeto do modelo do Desktop
        Livro livroDesktop = gson.fromJson(jsonDesktop, Livro.class);

        // Guarda o objeto original e o ID para uso posterior
        exchange.getIn().setHeader("entidadeOriginal", livroDesktop);
        exchange.getIn().setHeader("idDesktop", livroDesktop.getId());

        // Cria o objeto do modelo da API (DTO) usando seu nome completo para evitar conflito
        LivroApi livroApi = new LivroApi();

        // Mapeamento dos campos
        livroApi.setTitulo(livroDesktop.getTitulo());
        livroApi.setIsbn(livroDesktop.getIsbn());
        livroApi.setAnoPublicacao(livroDesktop.getAnoPublicacao());
        livroApi.setEdicao(livroDesktop.getEdicao());
        livroApi.setNumPaginas(livroDesktop.getNumPaginas());
        livroApi.setSinopse(livroDesktop.getSinopse());

        if (livroDesktop.getCategoria() != null) {
            livroApi.setCategoria(livroDesktop.getCategoria().getNome());
        }

        // *** MELHORIA AQUI ***
        // Converte a ForeignCollection para uma lista padrão para evitar problemas de serialização
        if (livroDesktop.getAutores() != null && !livroDesktop.getAutores().isEmpty()) {
            List<String> nomesAutores = new ArrayList<>(livroDesktop.getAutores()).stream()
                    .map(LivroAutor::getAutor)
                    .map(Autor::getNome)
                    .collect(Collectors.toList());
            livroApi.setAutores(nomesAutores);
        } else {
            livroApi.setAutores(new ArrayList<>());
        }

        // O campo 'resenhas' da API não será preenchido aqui para simplificar a sincronização
        livroApi.setResenhas(new ArrayList<>());

        // Converte o DTO da API para JSON e o define como o novo corpo da mensagem
        exchange.getIn().setBody(gson.toJson(livroApi));
    }
}