# Sistema de Gestão de Biblioteca (Desktop App)

Este é um projeto de um sistema de gestão de biblioteca desenvolvido em Java, utilizando JavaFX para a interface gráfica e ORMLite para a persistência de dados em um banco de dados SQLite. A aplicação permite o gerenciamento de livros, autores, categorias, usuários, empréstimos e resenhas.

## Funcionalidades

O sistema oferece funcionalidades de **CRUD (Create, Read, Update, Delete)** para as seguintes entidades:

* **Livros**: Cadastro de livros com título, ISBN, ano de publicação, edição, número de páginas, sinopse, categoria e autores.
* **Autores**: Gerenciamento de autores com nome, nacionalidade e biografia.
* **Categorias**: Criação e edição de categorias para os livros.
* **Usuários**: Gestão de usuários do sistema com nome, email, senha, telefone, cargo e status (ativo/inativo).
* **Cargos**: Definição de cargos para os usuários (ex: Administrador, Bibliotecário, Cliente).
* **Empréstimos**: Registro de empréstimos de livros para usuários, com datas de empréstimo, devolução prevista e devolução real.
* **Resenhas**: Permite que usuários façam resenhas dos livros, com nota e texto.

## Tecnologias Utilizadas

* **Java 17**: Linguagem de programação principal.
* **JavaFX**: Framework para a construção da interface gráfica do usuário (GUI).
* **Maven**: Ferramenta de automação de compilação e gerenciamento de dependências.
* **ORMLite**: Framework de Mapeamento Objeto-Relacional (ORM) para simplificar o acesso ao banco de dados.
* **SQLite**: Sistema de gerenciamento de banco de dados relacional embarcado.
* **JUnit 5**: Framework para a escrita e execução de testes unitários.

## Como Executar

1.  **Pré-requisitos**:
    * JDK 17 ou superior instalado.
    * Maven instalado.

2.  **Clone o repositório**:
    ```bash
    git clone <url-do-seu-repositorio>
    cd biblioteca-desktop-app
    ```

3.  **Compile e execute com o Maven**:
    O plugin do JavaFX configurado no `pom.xml` facilita a execução.
    ```bash
    mvn clean install javafx:run
    ```

4.  **Uso Inicial**:
    * Na primeira execução, a aplicação criará automaticamente um arquivo de banco de dados chamado `biblioteca_desktop.sqlite` na raiz do projeto.
    * Serão criados os cargos padrão ("Administrador", "Bibliotecário", "Cliente").
    * Um usuário **administrador** padrão será criado com as seguintes credenciais:
        * **Email**: `admin@admin`
        * **Senha**: `admin`

## Arquitetura do Projeto

O projeto segue uma arquitetura que separa as responsabilidades em diferentes pacotes:

* `client`: Contém a classe `MainApp`, ponto de entrada da aplicação JavaFX.
* `controller`: Contém as classes controladoras que gerenciam a lógica da interface do usuário para cada tela (aba) da aplicação.
    * `AbstractCrudController` e `AbstractPessoaController` fornecem uma base reutilizável para as operações de CRUD.
* `model`: Contém as classes de entidade (POJOs) que são mapeadas para as tabelas do banco de dados, além das classes de acesso a dados (Repositório, Database).
* `view`: Contém classes "ViewModel" que utilizam propriedades do JavaFX (`SimpleStringProperty`, etc.) para facilitar a vinculação de dados (data binding) com os componentes da interface, como as `TableView`.
* `resources/view`: Contém os arquivos FXML que definem a estrutura da interface gráfica.

---