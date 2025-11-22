# Definição Técnica: Projeto Freelancing Ismael (MVP)

**Versão:** 1.1 (Atualizado: Lógica de Sugestão)
**Status:** Planejamento
**Foco:** MVP Simples e Funcional (Entrega Segura + Controle de Estoque)

---

## 1. Visão Geral
O objetivo é um sistema completo de **Gestão de EPIs e Segurança do Trabalho**, substituindo o controle em papel.
* **Infraestrutura:** WiFi de qualidade disponível (Cliente Online).
* **Arquitetura:** Modelo Cliente-Servidor. O Backend é a fonte da verdade; o App é um cliente leve.

---

## 2. Stack Tecnológica

### Backend (API & Regras de Negócio)
* **Linguagem:** Java 17/21.
* **Framework:** Spring Boot (Web, Data JPA, Validation).
* **Banco de Dados:** MySQL.
* **Armazenamento de Arquivos:** TBD (To Be Decided).
* **Hospedagem (Dev):** Docker local.

### Mobile (Front-end)
* **Linguagem:** Kotlin.
* **UI:** Jetpack Compose.
* **Comunicação:** Retrofit.
* **Funcionalidade:** Foco em usabilidade, câmera e coleta de assinatura.

---

## 3. Regras de Negócio

### 3.1. Orientação de Segurança (O "Guia")
* **Origem:** *EPIS E EXAMES POR ATIVIDADE E FUNÇÃO.txt*
* **Nova Regra:** O sistema deve **SUGERIR** os EPIs obrigatórios para a função, mas **PERMITIR** a entrega de outros itens se a atividade exigir.
* **Lógica:**
    1. O sistema verifica a função do colaborador.
    2. Exibe primeiro a lista de "EPIs Recomendados".
    3. Permite buscar na "Lista Geral" caso necessário.

### 3.2. Controle de Estoque (O "Cofre")
* **Origem:** *Planilha Integrada de Gestão de EPIs*
* **Regra:** Toda entrega deve abater do saldo de estoque.
* **Dados Críticos:** Monitoramento de Estoque Mínimo, Custo Unitário e Validade (CA).

### 3.3. Documentação Legal (A "Garantia")
* **Origem:** *FICHA DE EPI EM BRANCO - 2025*
* **Regra:** A entrega gera uma Ficha de EPI jurídica.
* **Requisitos:** Termo de responsabilidade, CA, Assinatura Digital e Foto do item.

---

## 4. Escopo do MVP (Fluxo Feliz)

### Backend (Java Spring Boot)
1.  **CRUD de Funcionários:** (Nome, CPF, Função).
2.  **CRUD de EPIs:** (Nome, CA, Estoque, Custo).
3.  **Tabela de Associação (Config):** Liga `Função -> EPIs Sugeridos`.
4.  **Endpoint de Entrega:**
    * Recebe: ID Funcionário, ID EPI, Evidências.
    * Processa: Decrementa Estoque → Salva registro (Audit) → Gera PDF.

### Mobile (Kotlin Compose)
1.  **Login Simples:** Acesso do Gerente.
2.  **Seleção de Funcionário:** Busca na API.
3.  **Seleção de EPI (Inteligente):**
    * *Aba 1:* Recomendados (Filtrados pela função).
    * *Aba 2:* Todos (Busca livre no estoque).
4.  **Coleta de Evidências:** Foto (CameraX) e Assinatura (Canvas).
5.  **Finalização:** Envio para API.

---

## 5. Próximos Passos (Imediatos)
1.  Modelagem das Entidades JPA (`Employee`, `Epi`, `FunctionConfig`).
2.  Criação dos Repositories e Services básicos.