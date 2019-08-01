# uol-customer-api

**TODO: Introdução**

## Funcionalidades

- **OK** Criar Cliente
    - **TODO** Consulta à [API aberta de geolocalização por IP](https://www.ipvigilante.com/) 
    - **TODO** Consulta à [API de clima por geolocalização](https://www.metaweather.com/api/)
        - **TODO** Ao executar a busca de clima por geolocalização, caso não exista a cidade especifica de origem, é utilizado o resultado mais próximo.
        - **TODO** Ao criar um cliente, apenas para fins estatísticos e históricos, é buscada a localização geográfica de quem executou a requisição, usando o IP de origem.
        Com a localização geográfica, é consultada a temperatura máxima e mínima do dia da requisição de criação no local do IP de origem.
        A informação é salva e a associada ao cliente resultado da requisição de origem.
- **TODO** Alterar um Cliente
- **OK** Consultar um Cliente por id
- **OK** Listar todos os Clientes salvos
- **OK** Remover Cliente por id

## Como Usar

**TODO: Swagger descrevendo a API**

Também podem ser encontrados exemplos de uso da API
de acordo com a coleção do [Postman](https://www.getpostman.com) localizada na raíz
deste repositório: `postman_collection.json`

## Ferramentas Utilizadas

- Java 8
    - Versão comercial mais estável do Java,
    escolhi por segurança visto que muitos frameworks ainda
    não se adaptaram a modularidade exigida pelo Java 11.
- Spring Boot
    - Utilizado pois é a tecnologia usada na UOL. Me surpreendeu positivamente
    com a quantidade de coisas que traz prontas :)
- PostgreSQL
    - Foi utilizado apenas para ter experiência com algo diferente de MySQL e Oracle,
    apesar de não fazer muita diferença pois o Spring Boot
    abstrai a comunicação toda por meio do Hybernate
- Docker
    - Utilizado para proporcionar mais facilidade ao montar o ambiente,
    principalmente o de desenvolvimento
- Docker Compose
    - Utilizado pois como a aplicação envolve banco de dados e também cache
    da requisições de consulta, mais de um container é necessário

## Requisitos de Infraestrutura

- Sistema operacional Windows ou baseado em Linux
- Docker e Docker Compose instalados
    - As versões utilizadas durante o desenvolvimento foram as seguintes:
        - Docker Desktop (apenas Windows): v2.1.0.0 (36874)
        - Docker Engine: v19.03.1
        - Docker Compose: 1.24.1

### Apenas para Desenvolvimento

Para compilar o programa é necessário possuir, no mínimo:
- Maven 3.0
- JDK 8.0

## Instruções de Deploy

Para compilar o programa:
```bash
mvn clean install
```

Para rodar o programa, após a compilação:
```bash
docker-compose up --build
```

### Deploy em Produção

**TODO: Gerar ZIP utilizando plugin do Maven**
