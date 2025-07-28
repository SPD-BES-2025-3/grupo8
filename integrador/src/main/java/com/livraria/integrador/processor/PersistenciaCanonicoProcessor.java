package com.livraria.integrador.processor;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.livraria.integrador.model.canonico.MapeamentoID;
import com.livraria.integrador.repository.RepositorioCanonico;
import model.dto.LivroSyncDto;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.UUID;

public class PersistenciaCanonicoProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        // 1. Verifica se a chamada HTTP foi bem-sucedida (código 2xx)
        Integer responseCode = exchange.getMessage().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);
        if (responseCode == null || responseCode >= 300) {
            String responseBody = exchange.getMessage().getBody(String.class); // Tenta ler como texto para o log
            System.err.println("ERRO: A API retornou um código de falha: " + responseCode + ". Corpo: " + responseBody);
            return; // Interrompe o processamento se a chamada falhou
        }

        System.out.println("API respondeu com sucesso: " + responseCode);
        
        // 2. Recupera os dados necessários dos headers (necessário para todas as operações)
        LivroSyncDto livroDesktopDto = exchange.getIn().getHeader("entidadeOriginal", LivroSyncDto.class);
        if (livroDesktopDto == null) {
            System.err.println("ERRO CRÍTICO: DTO 'entidadeOriginal' não encontrado no header.");
            return;
        }

        String operacao = exchange.getIn().getHeader("operacao", String.class);

        // 3. Lógica para DELETE: remove o mapeamento
        if ("DELETE".equals(operacao)) {
            MapeamentoID mapeamento = RepositorioCanonico.mapeamentoIdDao.queryBuilder()
                    .where().eq("idDesktop", livroDesktopDto.getId()).queryForFirst();

            if (mapeamento != null) {
                RepositorioCanonico.mapeamentoIdDao.delete(mapeamento);
                System.out.println("Mapeamento removido com sucesso para o livro do desktop ID: " + livroDesktopDto.getId());
            } else {
                System.out.println("AVISO (DELETE): Mapeamento para o livro do desktop ID: " + livroDesktopDto.getId() + " não foi encontrado. Nenhuma ação de remoção necessária.");
            }
            return; // Finaliza o processamento para DELETE
        }

        // 4. Lógica para CREATE e UPDATE: lê o corpo da resposta e salva/atualiza o mapeamento
        InputStream responseStream = exchange.getMessage().getBody(InputStream.class);
        if (responseStream == null) {
            System.err.println("ERRO: O corpo da resposta da API está vazio para uma operação CREATE/UPDATE. O mapeamento não pode ser salvo.");
            return;
        }

        try (InputStreamReader reader = new InputStreamReader(responseStream)) {
            JsonObject respostaJsonObj = new Gson().fromJson(reader, JsonObject.class);
            if (respostaJsonObj == null || !respostaJsonObj.has("id")) {
                 System.err.println("ERRO: JSON de resposta da API inválido ou sem o campo 'id'.");
                 return;
            }
            String idApi = respostaJsonObj.get("id").getAsString();

            MapeamentoID mapeamento = RepositorioCanonico.mapeamentoIdDao.queryBuilder()
                    .where().eq("idDesktop", livroDesktopDto.getId()).queryForFirst();

            if (mapeamento != null) {
                mapeamento.setIdApi(idApi);
                mapeamento.setUltimaAtualizacao(new Date());
                RepositorioCanonico.mapeamentoIdDao.update(mapeamento);
                System.out.println("Mapeamento atualizado com sucesso. ID Canônico: " + mapeamento.getIdCanonico());
            } else {
                String idCanonico = UUID.randomUUID().toString();
                
                MapeamentoID novoMapeamento = new MapeamentoID();
                novoMapeamento.setIdCanonico(idCanonico);
                novoMapeamento.setIdDesktop(livroDesktopDto.getId());
                novoMapeamento.setIdApi(idApi);
                novoMapeamento.setUltimaAtualizacao(new Date());
                
                RepositorioCanonico.mapeamentoIdDao.create(novoMapeamento);
                System.out.println("Novo mapeamento criado com sucesso. ID Canônico: " + idCanonico);
            }
        } catch (JsonSyntaxException e) {
             System.err.println("ERRO de sintaxe JSON ao processar a resposta da API: " + e.getMessage());
        }
    }
}