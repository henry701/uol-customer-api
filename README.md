# uol-customer-api

API REST desenvolvida como teste técnico para processo seletivo na UOL.

## Funcionalidades

- **OK** Criar Cliente
    - **OK** Consulta à [API aberta de geolocalização por IP](https://www.ipvigilante.com/) 
    - **OK** Consulta à [API de clima por geolocalização](https://www.metaweather.com/api/)
        - **OK** Ao executar a busca de clima por geolocalização, caso não exista a cidade especifica de origem, é utilizado o resultado mais próximo.
        - **OK** Ao criar um cliente, apenas para fins estatísticos e históricos, é buscada a localização geográfica de quem executou a requisição, usando o IP de origem.
        Com a localização geográfica, é consultada a temperatura máxima e mínima do dia da requisição de criação no local do IP de origem.
        A informação é salva e a associada ao cliente resultado da requisição de origem.
- **OK** Alterar um Cliente
- **OK** Consultar um Cliente por id
    - **TODO** Caching dos clientes consultados, por ID, com invalidação ao update/delete, utilizando Redis
- **OK** Listar todos os Clientes salvos
- **OK** Remover Cliente por id

## Como Usar

**TODO: Swagger descrevendo a API**

Para descrever a API foi criado um arquivo no formato OpenAPI 3.0 que pode
ser lido por diversas ferramentas que geram documentação, como [essas aqui](https://openapi.tools/#documentation).
Ele pode ser encontrado no arquivo `openapi-uol-customer-api.yaml` na raiz desse repositório.

Também podem ser encontrados exemplos de uso da API
de acordo com a coleção do [Postman](https://www.getpostman.com) localizada na raíz
deste repositório: `postman_collection.json`.

## Ferramentas Utilizadas

- Java 8
    - Versão comercial mais estável do Java,
    escolhi por segurança visto que muitos frameworks ainda
    não se adaptaram a modularidade exigida pelo Java 11
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
    da requisições de consulta, mais de um container é necessário para o funcionamento do sistema
- JUnit
    - Padrão de fato para testes unitários no Java, e possui boa integração
    com os frameworks existentes (como o Spring Boot, por exemplo, que tem seu próprio Runner de testes)
- Logback
    - Framework de logging que vem integrado por padrão no Spring,
    com poucas diferenças do Log4j2 que estou acostumado
- Redis
    - Para realizar caching de entidades sem colocar pressão no Garbage Collector,
    e porque é um programa de caching robusto dedicado

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

## Configuração

No arquivo `application.properties` localizado na raíz do ZIP e na pasta `src/main/resources` deste projeto,
é possível alterar o endereço e as credenciais do banco de dados, 
além da porta de exposição da API, entre outras configurações da aplicação.

## Instruções de Deploy

### Deploy para Desenvolvimento utilizando Docker

Para compilar o programa:
```bash
mvn clean install
```

Para rodar o programa:
```bash
mvn exec exec:docker-up
```

Para parar o programa:
```bash
mvn exec exec:docker-stop
```

#### Troubleshooting

Caso haja problemas com as imagens após alterar a estrutura do banco de dados ou alguma
outra alteração de alto impacto, execute:
```bash
mvn exec exec:docker-down
```
***Aviso: Isso deleta todos os containers do sistema, apenas executar em DEV
pois acarreta em limpeza completa do banco de dados!***

### Deploy em Produção

#### Deploy utilizando Docker

Após a compilação, é gerado um ZIP com os artefatos e dependências
do programa na pasta target, cujo nome segue o formato `uol-customer-api-`**`VERSÃO`**`.zip`.

Para realizar o deploy em produção, basta extrair o ZIP na máquina alvo
e executar:
```bash
docker-compose up --build
```
Na pasta onde foi extraído o ZIP.

#### Deploy sem Docker

Caso a máquina de produção não possua o Docker instalado, também é possível
rodar a aplicação em modo standalone, contanto que ela possua uma instância do
BD Postgres rodando e, opcionalmente, uma do Redis para realizar o cache, rodando o comando:
```bash
java -Dlogback.configurationFile=app/logback-spring.xml -cp "app:app/lib/*" "br.com.henry.selective.uol.customer.Application"
```
Na raíz da estrutura do arquivo ZIP, após sua extração.
