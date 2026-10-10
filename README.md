# Escola Projeto — somente Unidades

Pacote: `com.superdev.escolaprojeto`. Organização inspirada no Helpdesk, sem pacote common.

Java 25 e Spring Boot 4.1.1 preservados do projeto IntelliJ existente.

Inclui cadastro, edição de nome, consulta por ID, filtros, paginação, validação de sigla e nome únicos e tratamento de erros. Sigla imutável e persistida em maiúsculas. Não há rota DELETE, conforme a especificação.

## Executar no IntelliJ

Recarregue o Maven e execute `EscolaApplication`. O PostgreSQL local deve estar ativo. As credenciais locais ficam em `application-local.properties`, ignorado pelo Git. Também é possível configurar DB_URL, DB_USER e DB_PASSWORD.

O Flyway cria apenas a tabela unidades e seus índices no banco escola-projeto; sua tabela de histórico registra a migração. O JPA apenas valida o esquema.

Swagger: http://localhost:8080/swagger-ui/index.html

Requisições prontas: unidades.http.

## Testes

Execute `./mvnw test` com DB_URL, DB_USER e DB_PASSWORD apontando para um banco exclusivo de testes. Os cenários HTTP são transacionais e revertidos ao final.
