# API de Tarefas (Task Management API) 📝

Esta é uma API RESTful robusta desenvolvida para o gerenciamento de tarefas, com foco em segurança, persistência de dados e documentação. O projeto foi concebido como uma forma de **aprendizado aprofundado** sobre o ecossistema Spring e boas práticas de desenvolvimento backend.

> **Nota de Aprendizado:** Este projeto serviu para consolidar conhecimentos em **Spring Security**, autenticação, autorização e a integração de bancos de dados relacionais com **Docker**. Foi um laboratório essencial para entender o ciclo de vida de uma aplicação Java moderna, desde o desenvolvimento até a conteinerização.

---

## 🛠️ Tecnologias e Ferramentas

- **Java 25**: Utilizando as versões mais recentes da linguagem para explorar novas funcionalidades.
- **Spring Boot 3.5.6**: Base para a construção da API.
- **Spring Security**: Implementação de camadas de segurança para proteção dos endpoints.
- **Spring Data JPA**: Abstração para persistência de dados.
- **MySQL**: Banco de dados relacional utilizado em produção/desenvolvimento.
- **H2 Database**: Banco de dados em memória utilizado para agilizar os testes unitários e de integração.
- **Docker & Docker Compose**: Para orquestração do ambiente e do banco de dados.
- **Lombok**: Produtividade no desenvolvimento com redução de código repetitivo.
- **SpringDoc OpenAPI (Swagger)**: Documentação interativa da API.

---

## 🚀 Funcionalidades Principais

- **CRUD de Tarefas**: Criação, leitura, atualização e exclusão de tarefas.
- **Segurança**: Endpoints protegidos que exigem autenticação.
- **Documentação Automática**: Interface Swagger para testar os endpoints diretamente pelo navegador.
- **Ambiente Isolado**: Configuração pronta para rodar via Docker, garantindo que a aplicação funcione em qualquer ambiente.

---

## 📦 Como Executar

### Via Docker (Recomendado)
Certifique-se de ter o Docker e o Docker Compose instalados:
```bash
docker-compose up -d
```

### Via Maven
Se preferir rodar localmente (necessário ter o MySQL configurado conforme o `application.properties`):
```bash
./mvnw spring-boot:run
```

---

## 📖 Documentação da API
Após iniciar a aplicação, você pode acessar a documentação interativa (Swagger) em:
`http://localhost:8080/swagger-ui.html` (ou na porta configurada).

---

## 🧠 Evolução Técnica
Este projeto permitiu o domínio de:
- Estruturação de projetos seguindo o padrão MVC.
- Configuração de segurança customizada com Spring Security.
- Uso de **Docker** para gerenciar dependências de infraestrutura (Banco de Dados).
- Escrita de testes utilizando **JUnit** e **Mockito**, buscando sempre a excelência na cobertura de código.

---
*Desenvolvido por [Diego Prado](https://www.linkedin.com/in/diego-prado-dev/) como parte de sua jornada de evolução técnica.*
