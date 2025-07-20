# API de Livraria - Backend

API RESTful desenvolvida em Java com Spring Boot e MongoDB. Este projeto serve como o backend para um sistema de gerenciamento de livraria, oferecendo operações de CRUD (Criar, Ler, Atualizar, Deletar) para livros e a possibilidade de adicionar resenhas.

Esta API faz parte de uma arquitetura maior que inclui uma aplicação desktop (JavaFX/SQLite) e um middleware de sincronização (ActiveMQ/JMS).

## Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.x**
- **Spring Web**: Para a construção de endpoints REST.
- **Spring Data MongoDB**: Para a integração com o banco de dados NoSQL.
- **Maven**: Gerenciador de dependências e build.
- **MongoDB**: Banco de dados orientado a documentos.
- **Lombok**: Para reduzir código boilerplate (getters, setters, etc).

## Pré-requisitos

Antes de iniciar, você precisará ter os seguintes softwares instalados em sua máquina:

- JDK 17 ou superior
- Apache Maven
- Uma instância do MongoDB (rodando localmente ou em um serviço de nuvem como o MongoDB Atlas).

## Instalação e Execução

1.  **Clone o repositório:**
    ```bash
    git clone 
    cd seu-repositorio
    ```

2.  **Configure a Conexão com o Banco de Dados:**
    Abra o arquivo `src/main/resources/application.properties` e configure a URI de conexão do seu MongoDB.

    *Para uma instância local padrão:*
    ```properties
    spring.data.mongodb.uri=mongodb://localhost:27017/livrariaDB
    ```

    *Para uma instância no MongoDB Atlas, use a string de conexão fornecida por eles.*

3.  **Execute a Aplicação:**
    Use o Maven Wrapper para iniciar o servidor.

    ```bash
    ./mvnw spring-boot:run
    ```
    A API estará disponível em `http://localhost:8080`.

## Testando a API

Você pode usar ferramentas como o [Postman](https://www.postman.com/), [Insomnia](https://insomnia.rest/) ou `curl` para testar os endpoints.

---

### Endpoints de Livros (`/api/livros`)

**1. Criar um novo livro**
- **Método:** `POST`
- **URL:** `/api/livros`
- **Corpo (JSON):**
  ```json
  {
      "titulo": "O Hobbit",
      "autores": ["J. R. R. Tolkien"],
      "anoPublicacao": 1937,
      "categoria": "Fantasia"
  }
  ```
- **Exemplo com `curl`:**
  ```bash
  curl -X POST http://localhost:8080/api/livros \
  -H "Content-Type: application/json" \
  -d '{"titulo": "O Hobbit", "autores": ["J. R. R. Tolkien"], "anoPublicacao": 1937, "categoria": "Fantasia"}'
  ```

**2. Listar todos os livros**
- **Método:** `GET`
- **URL:** `/api/livros`
- **Exemplo com `curl`:**
  ```bash
  curl http://localhost:8080/api/livros
  ```

**3. Buscar um livro por ID**
- **Método:** `GET`
- **URL:** `/api/livros/{id}`
- **Exemplo com `curl`:**
  ```bash
  curl http://localhost:8080/api/livros/669c5e21c8b3e84a2a11b8f1
  ```

**4. Atualizar um livro**
- **Método:** `PUT`
- **URL:** `/api/livros/{id}`
- **Exemplo com `curl`:**
  ```bash
  curl -X PUT http://localhost:8080/api/livros/669c5e21c8b3e84a2a11b8f1 \
  -H "Content-Type: application/json" \
  -d '{"titulo": "O Hobbit (Edição de Colecionador)", "autores": ["J. R. R. Tolkien"], "anoPublicacao": 1937, "categoria": "Fantasia"}'
  ```

**5. Deletar um livro**
- **Método:** `DELETE`
- **URL:** `/api/livros/{id}`
- **Exemplo com `curl`:**
  ```bash
  curl -X DELETE http://localhost:8080/api/livros/669c5e21c8b3e84a2a11b8f1
  ```

---

### Endpoints de Resenhas

**1. Adicionar uma resenha a um livro**
- **Método:** `POST`
- **URL:** `/api/livros/{livroId}/resenhas`
- **Corpo (JSON):**
  ```json
  {
      "nomeUsuario": "Ana",
      "nota": 5,
      "texto": "Uma jornada fantástica e inesquecível!"
  }
  ```
- **Exemplo com `curl`:**
  ```bash
  curl -X POST http://localhost:8080/api/livros/669c5e21c8b3e84a2a11b8f1/resenhas \
  -H "Content-Type: application/json" \
  -d '{"nomeUsuario": "Ana", "nota": 5, "texto": "Uma jornada fantástica e inesquecível!"}'
  ```