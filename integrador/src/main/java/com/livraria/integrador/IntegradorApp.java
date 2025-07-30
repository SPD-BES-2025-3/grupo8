package com.livraria.integrador;

import com.livraria.integrador.routes.LivrariaRouteBuilder;
import org.apache.camel.main.Main;

/**
 * Ponto de entrada da aplicação de integração.
 * Inicializa o Apache Camel e carrega as rotas de sincronização.
 */
public class IntegradorApp {
	public static void main(String[] args) throws Exception {
		System.out.println("==================================================");
		System.out.println("INICIANDO INTEGRADOR DE LIVRARIA COM CAMEL E JMS");
		System.out.println("==================================================");

		Main main = new Main();
		// Adiciona as rotas de integração ao contexto do Camel
		main.configure().addRoutesBuilder(new LivrariaRouteBuilder());

		// Mantém a aplicação rodando para escutar as filas JMS
		main.run(args);
	}
}