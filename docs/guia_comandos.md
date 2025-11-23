# Guia de Comandos do Projeto

Este guia contém os principais comandos para desenvolver, testar e rodar a aplicação **Epimanager**.

## 🚀 Executar a Aplicação

Para rodar a aplicação localmente (certifique-se de que o Docker está rodando para o banco de dados):

```bash
./mvnw spring-boot:run
```

## 🏗️ Build e Instalação

Para limpar a pasta `target` e baixar todas as dependências (Clean Install):

```bash
./mvnw clean install
```

Para apenas compilar e empacotar o projeto (gera o `.jar` em `target/`):

```bash
./mvnw clean package
```

## 🧪 Testes

Para rodar todos os testes unitários e de integração:

```bash
./mvnw test
```

## 🐳 Docker (Banco de Dados)

Para subir o banco de dados MySQL via Docker Compose:

```bash
docker-compose up -d
```

Para parar e remover os containers:

```bash
docker-compose down
```

## 🧹 Limpeza

Para limpar arquivos compilados antigos:

```bash
./mvnw clean
```
