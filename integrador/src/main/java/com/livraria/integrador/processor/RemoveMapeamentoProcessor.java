package com.livraria.integrador.processor;

import com.livraria.integrador.model.canonico.MapeamentoID;
import com.livraria.integrador.repository.RepositorioCanonico;
import model.dto.LivroSyncDto;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class RemoveMapeamentoProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        // Recupera o DTO original do header para obter o ID do desktop
        LivroSyncDto livroDesktopDto = exchange.getIn().getHeader("entidadeOriginal", LivroSyncDto.class);
        if (livroDesktopDto == null) {
            System.err.println("ERRO (DELETE): DTO 'entidadeOriginal' não encontrado no header.");
            return;
        }

        // Busca o mapeamento pelo ID do desktop
        MapeamentoID mapeamento = RepositorioCanonico.mapeamentoIdDao.queryBuilder()
                .where().eq("idDesktop", livroDesktopDto.getId()).queryForFirst();

        if (mapeamento != null) {
            // Se encontrar, remove o mapeamento do banco de dados do integrador
            RepositorioCanonico.mapeamentoIdDao.delete(mapeamento);
            System.out.println("Mapeamento removido com sucesso para o livro do desktop ID: " + livroDesktopDto.getId());
        } else {
            System.out.println("AVISO (DELETE): Mapeamento para o livro do desktop ID: " + livroDesktopDto.getId() + " não foi encontrado. Nenhuma ação necessária.");
        }
    }
}