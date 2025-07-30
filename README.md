# Sistema de Gerenciamento de Biblioteca (Projeto Integrado)

Bem-vindo ao projeto do Grupo 8! Este repositório contém um ecossistema de software completo para o gerenciamento de uma biblioteca, demonstrando uma arquitetura moderna e desacoplada.

O sistema é composto por três componentes principais:
1.  **Aplicação Desktop (JavaFX):** Uma interface de administração para gerenciar o acervo, usuários e empréstimos.
2.  **API RESTful (Spring Boot):** Um serviço de backend para consultas públicas do acervo e resenhas.
3.  **Módulo Integrador (Apache Camel):** Um middleware que garante a sincronização de dados em tempo real entre o Desktop e a API.

---

## 🚀 Documentação Técnica Completa

Para uma análise aprofundada da arquitetura, detalhes de implementação de cada módulo, diagramas UML, instruções de configuração e guia de endpoints da API, por favor, consulte nossa documentação técnica completa.

➡️ **Consulte o arquivo: [livrariaDoc.pdf](./livrariaDoc.pdf)**

---

## Principais Serviços e Como Acessá-los:

API RESTful: http://localhost:8080

Console do ActiveMQ: http://localhost:8162

MongoDB: mongodb://localhost:27018

## 🛠️ Tecnologias Principais
Backend: Java 17, Spring Boot

Frontend: JavaFX

Bancos de Dados: MongoDB (API), SQLite (Desktop)

Integração: Apache Camel, JMS, Apache ActiveMQ

Orquestração: Docker, Docker Compose

Build: Maven

## Autores
Daired Almeida Cruz

Hugo Alves dos Santos

## ⚡ Guia de Início Rápido (Quick Start)

Com o uso de Docker Compose, a execução de toda a infraestrutura de backend foi simplificada para um único comando.

### Pré-requisitos
* Java 17 (ou superior)
* Apache Maven
* Docker e Docker Compose

### Para Iniciar
Execute o script na raiz do projeto. Ele cuidará de compilar todos os módulos, iniciar os serviços de backend (MongoDB, ActiveMQ, API, Integrador) e executar a aplicação desktop.

```bash
# No Linux ou macOS (torne-o executável primeiro: chmod +x start.sh)
./start.sh

