# Dev Tools - Script de Utilidades

**Localização**: `dev-tools.sh`  
**Como usar**: `./dev-tools.sh`

---

## 🚀 Menu Interativo

Script bash com menu colorido para facilitar operações comuns durante desenvolvimento.

### Opções Disponíveis

| Opção | Comando | Descrição |
|-------|---------|-----------|
| **1** | Build + Run | Buildar e rodar backend (`./mvnw spring-boot:run`) |
| **2** | Clean | Maven clean |
| **3** | Clean + Compile | Maven clean + compile |
| **4** | Clean + Install | Maven clean + install |
| **5** | Testes | Rodar todos os testes |
| **6** | Swagger UI | Abrir Swagger UI no navegador |
| **7** | Testar Login | Fazer request de teste para `/auth/login` |
| **8** | Listar Endpoints | Mostrar todos os 16 endpoints |
| **9** | Ver Logs | Tail -f dos logs em tempo real |
| **10** | Parar Backend | Matar processo do backend |
| **11** | Git Status | Ver status e últimos commits |
| **12** | Backup | Criar backup .tar.gz com SHA256 |
| **0** | Sair | Fechar menu |

---

## 📋 Uso

### Primeira execução

```bash
# Dar permissão de execução
chmod +x dev-tools.sh

# Rodar
./dev-tools.sh
```

### Uso normal

```bash
./dev-tools.sh

# Escolher opção digitando o número
# Exemplo: 1 para buildar e rodar
```

---

## 🎨 Features

### Visual
- ✅ Menu colorido e organizado
- ✅ Banner ASCII art
- ✅ Emojis e cores para feedback
- ✅ Interface limpa com separadores

### Funcionalidades
- ✅ Testa endpoint de login automaticamente
- ✅ Abre Swagger UI no navegador (macOS/Linux)
- ✅ Lista todos os endpoints com cores
- ✅ Para backend gracefully
- ✅ Cria backup com checksum SHA256
- ✅ Mostra logs em tempo real

---

## 🔧 Exemplos de Uso

### Workflow Típico

```bash
# 1. Abrir menu
./dev-tools.sh

# 2. Escolher "3" para clean + compile
# Verifica se compila

# 3. Escolher "1" para rodar backend
# Backend inicia

# 4. Em outro terminal, rodar novamente
./dev-tools.sh

# 5. Escolher "6" para abrir Swagger
# Navegador abre automaticamente

# 6. Escolher "7" para testar login
# Faz request e mostra resposta

# 7. Escolher "10" para parar backend
# Mata o processo
```

---

## 💡 Dicas

### Atalhos úteis

**Após rodar backend (opção 1):**
- Ctrl+C para parar
- Ou usar opção 10 em outro terminal

**Ver logs:**
- Opção 9 mostra logs em tempo real
- Ctrl+C para sair

**Backup antes de mudanças:**
- Opção 12 cria backup com SHA256
- Útil antes de refatorações grandes

---

## 🛠️ Customização

Você pode adicionar mais opções editando o script:

```bash
# Adicionar nova função
my_custom_task() {
    echo -e "${CYAN}▶ Fazendo algo...${NC}"
    # seu código aqui
    press_any_key
}

# Adicionar no menu (na função show_menu)
echo -e "${BLUE}║${NC}  13 ${GREEN}│${NC} Minha Tarefa Customizada  ${BLUE}║${NC}"

# Adicionar no case do loop
13)
    my_custom_task
    ;;
```

---

## 🎯 Casos de Uso

### Desenvolvimento
1. Opção 3 (clean + compile) para verificar erros
2. Opção 1 (run) para testar
3. Opção 6 (Swagger) para testar endpoints

### Testes
1. Opção 5 (testes) para rodar suite completa
2. Opção 7 (login) para verificar autenticação
3. Opção 8 (endpoints) para ver lista completa

### Deploy/Backup
1. Opção 12 (backup) antes de mudanças grandes
2. Opção 11 (git status) para verificar o que mudou
3. Opção 4 (install) para gerar JAR final

---

**Status**: ✅ Pronto para uso  
**Compatibilidade**: macOS, Linux  
**Dependências**: bash, curl (para teste de login)
