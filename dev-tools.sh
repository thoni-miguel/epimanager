#!/bin/bash

# EPI Manager - Script de Utilidades
# Facilita operações comuns durante desenvolvimento

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Banner
clear
echo -e "${CYAN}"
echo "╔══════════════════════════════════════════════╗"
echo "║         EPI Manager - Dev Tools              ║"
echo "║              Backend MVP v1.0                ║"
echo "╚══════════════════════════════════════════════╝"
echo -e "${NC}"

# Função para mostrar menu
show_menu() {
    echo ""
    echo -e "${BLUE}╔════════════════ MENU PRINCIPAL ════════════════╗${NC}"
    echo -e "${BLUE}║${NC}  1  ${GREEN}│${NC} Buildar e Rodar Backend                   ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}  2  ${GREEN}│${NC} Maven Clean                               ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}  3  ${GREEN}│${NC} Maven Clean + Compile                     ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}  4  ${GREEN}│${NC} Maven Clean + Install                     ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}  5  ${GREEN}│${NC} Rodar Testes                              ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}  6  ${GREEN}│${NC} Abrir Swagger UI no Navegador             ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}  7  ${GREEN}│${NC} Testar Endpoint de Login                  ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}  8  ${GREEN}│${NC} Listar Todos Endpoints                    ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}  9  ${GREEN}│${NC} Ver Logs em Tempo Real                    ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}  10 ${GREEN}│${NC} Parar Backend (se rodando)                ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}  11 ${GREEN}│${NC} Status do Projeto (Git)                   ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}  12 ${GREEN}│${NC} Criar Backup SHA256                       ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}  0  ${GREEN}│${NC} ${RED}Sair${NC}                                      ${BLUE}║${NC}"
    echo -e "${BLUE}╚════════════════════════════════════════════════╝${NC}"
    echo ""
}

# Função para pressionar qualquer tecla
press_any_key() {
    echo ""
    echo -e "${YELLOW}Pressione qualquer tecla para continuar...${NC}"
    read -n 1 -s
}

# Opção 1 - Buildar e Rodar Backend
run_backend() {
    echo -e "${CYAN}▶ Buildando e rodando backend...${NC}"
    echo ""
    ./mvnw spring-boot:run
}

# Opção 2 - Maven Clean
maven_clean() {
    echo -e "${CYAN}▶ Executando Maven Clean...${NC}"
    ./mvnw clean
    echo -e "${GREEN}✓ Clean concluído!${NC}"
    press_any_key
}

# Opção 3 - Maven Clean + Compile
maven_clean_compile() {
    echo -e "${CYAN}▶ Executando Maven Clean + Compile...${NC}"
    ./mvnw clean compile
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Build concluído com sucesso!${NC}"
    else
        echo -e "${RED}✗ Erro no build!${NC}"
    fi
    press_any_key
}

# Opção 4 - Maven Clean + Install
maven_clean_install() {
    echo -e "${CYAN}▶ Executando Maven Clean + Install...${NC}"
    ./mvnw clean install
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Install concluído com sucesso!${NC}"
    else
        echo -e "${RED}✗ Erro no install!${NC}"
    fi
    press_any_key
}

# Opção 5 - Rodar Testes
run_tests() {
    echo -e "${CYAN}▶ Executando testes...${NC}"
    ./mvnw test
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Todos os testes passaram!${NC}"
    else
        echo -e "${RED}✗ Alguns testes falharam!${NC}"
    fi
    press_any_key
}

# Opção 6 - Abrir Swagger UI
open_swagger() {
    echo -e "${CYAN}▶ Abrindo Swagger UI...${NC}"
    echo ""
    echo -e "${YELLOW}URL: http://localhost:8080/swagger-ui.html${NC}"
    echo ""
    
    # Detecta o sistema operacional e abre o navegador
    if [[ "$OSTYPE" == "darwin"* ]]; then
        # macOS
        open "http://localhost:8080/swagger-ui.html"
    elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
        # Linux
        xdg-open "http://localhost:8080/swagger-ui.html" 2>/dev/null || \
        sensible-browser "http://localhost:8080/swagger-ui.html" 2>/dev/null || \
        firefox "http://localhost:8080/swagger-ui.html" 2>/dev/null
    else
        echo -e "${YELLOW}Sistema operacional não detectado. Abra manualmente:${NC}"
        echo "http://localhost:8080/swagger-ui.html"
    fi
    
    press_any_key
}

# Opção 7 - Testar Login
test_login() {
    echo -e "${CYAN}▶ Testando endpoint de login...${NC}"
    echo ""
    echo -e "${YELLOW}Credenciais: admin / admin123${NC}"
    echo ""
    
    curl -X POST http://localhost:8080/auth/login \
      -H "Content-Type: application/json" \
      -d '{
        "username": "admin",
        "password": "admin123"
      }' \
      -w "\n\n${GREEN}Status Code: %{http_code}${NC}\n" 2>/dev/null
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Request enviado com sucesso!${NC}"
    else
        echo -e "${RED}✗ Backend não está rodando ou erro de conexão!${NC}"
    fi
    
    press_any_key
}

# Opção 8 - Listar Endpoints
list_endpoints() {
    echo -e "${CYAN}▶ Endpoints disponíveis:${NC}"
    echo ""
    echo -e "${GREEN}Autenticação (Público):${NC}"
    echo "  POST   /auth/register"
    echo "  POST   /auth/login"
    echo ""
    echo -e "${GREEN}Funcionários (Auth):${NC}"
    echo "  GET    /funcionarios"
    echo "  POST   /funcionarios"
    echo "  GET    /funcionarios/{id}"
    echo ""
    echo -e "${GREEN}EPIs (Auth):${NC}"
    echo "  GET    /epis"
    echo "  POST   /epis"
    echo "  GET    /epis/recomendados?cargoId={id}"
    echo ""
    echo -e "${GREEN}Atividades (Auth):${NC}"
    echo "  GET    /atividades"
    echo "  GET    /atividades/{id}"
    echo ""
    echo -e "${GREEN}Cargos (Auth):${NC}"
    echo "  GET    /cargos"
    echo "  GET    /cargos/{id}"
    echo "  GET    /cargos/atividade/{atividadeId}"
    echo ""
    echo -e "${GREEN}Entregas (Auth):${NC}"
    echo "  POST   /entregas"
    echo "  GET    /entregas/vencendo?dias={n}"
    echo "  PUT    /entregas/{id}/devolver"
    echo ""
    echo -e "${BLUE}Total: 16 endpoints${NC}"
    
    press_any_key
}

# Opção 9 - Ver Logs
view_logs() {
    echo -e "${CYAN}▶ Mostrando logs (Ctrl+C para sair)...${NC}"
    echo ""
    
    # Verifica se existe arquivo de log
    if [ -f "logs/application.log" ]; then
        tail -f logs/application.log
    else
        echo -e "${YELLOW}Nenhum arquivo de log encontrado.${NC}"
        echo "Rode o backend primeiro (opção 1)"
        press_any_key
    fi
}

# Opção 10 - Parar Backend
stop_backend() {
    echo -e "${CYAN}▶ Procurando processo do backend...${NC}"
    
    # Procura processo Java rodando Spring Boot
    PID=$(ps aux | grep 'spring-boot:run' | grep -v grep | awk '{print $2}')
    
    if [ -z "$PID" ]; then
        echo -e "${YELLOW}Nenhum backend rodando encontrado.${NC}"
    else
        echo -e "${YELLOW}Processo encontrado (PID: $PID)${NC}"
        echo -e "${RED}Encerrando...${NC}"
        kill $PID
        sleep 2
        echo -e "${GREEN}✓ Backend parado!${NC}"
    fi
    
    press_any_key
}

# Opção 11 - Git Status
git_status() {
    echo -e "${CYAN}▶ Status do Git:${NC}"
    echo ""
    git status
    echo ""
    echo -e "${CYAN}▶ Últimos 5 commits:${NC}"
    git log --oneline -5
    
    press_any_key
}

# Opção 12 - Criar Backup
create_backup() {
    echo -e "${CYAN}▶ Criando backup SHA256...${NC}"
    
    TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
    BACKUP_NAME="epimanager_backup_${TIMESTAMP}.tar.gz"
    
    echo -e "${YELLOW}Compactando projeto...${NC}"
    tar -czf "../${BACKUP_NAME}" \
        --exclude='target' \
        --exclude='uploads' \
        --exclude='node_modules' \
        --exclude='.git' \
        .
    
    if [ $? -eq 0 ]; then
        cd ..
        SHA256=$(shasum -a 256 "${BACKUP_NAME}" | awk '{print $1}')
        
        echo ""
        echo -e "${GREEN}✓ Backup criado com sucesso!${NC}"
        echo -e "${BLUE}Arquivo:${NC} ../${BACKUP_NAME}"
        echo -e "${BLUE}SHA256:${NC}  ${SHA256}"
        echo ""
        echo "${SHA256}" > "${BACKUP_NAME}.sha256"
        echo -e "${GREEN}✓ Checksum salvo em: ${BACKUP_NAME}.sha256${NC}"
        
        cd - > /dev/null
    else
        echo -e "${RED}✗ Erro ao criar backup!${NC}"
    fi
    
    press_any_key
}

# Loop principal
while true; do
    show_menu
    
    echo -ne "${CYAN}Escolha uma opção: ${NC}"
    read option
    
    case $option in
        1)
            run_backend
            ;;
        2)
            maven_clean
            ;;
        3)
            maven_clean_compile
            ;;
        4)
            maven_clean_install
            ;;
        5)
            run_tests
            ;;
        6)
            open_swagger
            ;;
        7)
            test_login
            ;;
        8)
            list_endpoints
            ;;
        9)
            view_logs
            ;;
        10)
            stop_backend
            ;;
        11)
            git_status
            ;;
        12)
            create_backup
            ;;
        0)
            echo ""
            echo -e "${GREEN}Até logo! 👋${NC}"
            echo ""
            exit 0
            ;;
        *)
            echo -e "${RED}Opção inválida! Tente novamente.${NC}"
            sleep 1
            ;;
    esac
    
    clear
done
