# uol-customer-api

**TODO: Introdução**

## Funcionalidades

- Criar um Cliente
    - Consulta à API aberta de geolocalização por IP https://www.ipvigilante.com/
    - Consulta à API de clima por geolocalização https://www.metaweather.com/api/
        - Quando executar a busca de clima por geolocalização, caso não exista a cidade especifica de origem, utilize o resultado mais próximo.
        - Ao criar um cliente, apenas para fins estatísticos e históricos, busque qual a localização geográfica de quem executou a requisição, usando o IP de origem. Com a localização geográfica, consulte qual é a temperatura máxima e mínima do dia da requisição de criação no local do IP de origem. Salve essa informação e a associe ao cliente resultado da requisição de origem.
- Alterar um Cliente
- Consultar um Cliente por id
- Listar todos os Clientes salvos
- Remover Cliente por id

## Como Usar

**TODO: Swagger descrevendo a API**

Também podem ser encontrados exemplos de uso da API
de acordo com a coleção do [Postman](https://www.getpostman.com) localizada na raíz
deste repositório: `postman_collection.json`

## Ferramentas Utilizadas

- Java 8
- Spring Boot
- PostgreSQL
- Docker
- Docker Compose

## Requisitos de Infraestrutura

- Sistema operacional Windows ou baseado em Linux
- Docker e Docker Compose instalados
    - As versões utilizadas durante o desenvolvimento foram as seguintes:
        - Docker Desktop (apenas Windows): v2.1.0.0 (36874)
        - Docker Engine: v19.03.1
        - Docker Compose: 1.24.1

### Apenas para Desenvolvimento

Para compilar o programa é necessário possuir, no mínimo, o Maven e uma JDK 8.

## Instruções de Deploy

Para compilar o programa:
```bash
mvn clean install
```

Para rodar o programa, após compilação:
```
docker-compose up --build
```

### Deploy em Produção

**TODO: Gerar ZIP utilizando plugin do Maven**
