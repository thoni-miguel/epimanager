# Action Plan - EPI Manager MVP

**Objetivo**: Finalizar backend e partir para o Android  
**Deadline Backend**: 30 de Novembro de 2025 (7 dias)  
**Status**: 🟢 Em andamento

---

## ⏰ Esta Semana (25-30 Nov) - BACKEND FINAL

### 🔐 Dia 1-2: Spring Security Básico (2 dias)
**Objetivo**: Login funcional, nada complexo

- [ ] Adicionar dependência `spring-boot-starter-security`
- [ ] Criar entidade `User` (id, username, password, role)
- [ ] Implementar `UserDetailsService`
- [ ] Configurar `SecurityFilterChain`:
  - POST `/auth/login` → público
  - POST `/auth/register` → público
  - Todos outros endpoints → autenticado
- [ ] Endpoint básico de login (retorna token ou sessão)
- [ ] Testar no Postman

**Critério de "Pronto"**: Conseguir fazer login e acessar `/funcionarios` autenticado

**⚠️ NÃO FAZER**:
- ❌ JWT customizado complexo
- ❌ OAuth2/SSO
- ❌ Sistema de permissões granulares
- ❌ Refresh tokens
→ Use o mais simples que funciona (Basic Auth ou in-memory JWT)

---

### 📚 Dia 3: OpenAPI/Swagger (1 dia)
**Objetivo**: Documentar endpoints para consumir no Android

- [ ] Adicionar dependência `springdoc-openapi-starter-webmvc-ui`
- [ ] Acessar `http://localhost:8080/swagger-ui.html`
- [ ] Adicionar anotações básicas nos controllers:
  - `@Tag(name = "Funcionários")`
  - `@Operation(summary = "Lista todos funcionários")`
- [ ] Testar todos endpoints pela UI do Swagger

**Critério de "Pronto"**: Swagger UI mostra todos endpoints com descrições

**⚠️ NÃO FAZER**:
- ❌ Documentar TUDO com exemplos detalhados
- ❌ Customizar UI do Swagger
- ❌ Schemas complexos
→ Descrições básicas são suficientes

---

### 🧪 Dia 4: Validação e Ajustes Finais (1 dia)

- [ ] Rodar todos os testes (`./mvnw test`)
- [ ] Atualizar Postman Collection com endpoints de autenticação
- [ ] Testar fluxo completo:
  1. Login
  2. Listar EPIs
  3. Registrar entrega
  4. Listar vencimentos
- [ ] Commit final: `feat: adicionar autenticação e documentação OpenAPI`

**Critério de "Pronto"**: API funciona end-to-end com autenticação

---

### 🚫 Dia 5-7: BUFFER / BUGFIXES ONLY

**Regra**: Apenas corrigir bugs descobertos nos dias 1-4  
**Proibido**: Adicionar features novas, refatorar, otimizar

---

## 📱 Semana Seguinte (1-7 Dez) - ANDROID START

### 📋 Setup Android (Dia 1)
- [ ] Criar projeto Kotlin no Android Studio
- [ ] Adicionar dependências (Retrofit, Coil, etc.)
- [ ] Configurar `build.gradle`

### 🔑 Tela de Login (Dia 2-3)
- [ ] Layout XML/Compose
- [ ] Chamar endpoint `/auth/login`
- [ ] Salvar token (SharedPreferences)

### 📊 Lista de EPIs (Dia 4-5)
- [ ] RecyclerView/LazyColumn
- [ ] GET `/epis/recomendados?cargoId=X`
- [ ] Exibir nome, CA, estoque

### 📸 Registro de Entrega (Dia 6-7)
- [ ] Captura de foto (Camera API)
- [ ] Assinatura (Canvas)
- [ ] POST `/entregas`

**Meta**: Protótipo funcional em 7 dias

---

## 🎯 Critérios de Sucesso

### Backend (30 Nov)
✅ Login funciona  
✅ Swagger documenta todos endpoints  
✅ Testes passam  
✅ Postman atualizado

### Android Protótipo (7 Dez)
✅ Login → Lista EPIs → Registra Entrega funciona  
✅ Comunicação com backend sem erros  
✅ UI mínima mas funcional

---

## 🚨 Regras Anti-TDAH

### ✅ PERMITIDO durante desenvolvimento Android:
- Corrigir bugs no backend descobertos pelo app
- Adicionar endpoint esquecido (se realmente necessário)
- Ajustar response do backend (ex: adicionar campo faltante)

### ❌ PROIBIDO durante desenvolvimento Android:
- "Melhorar" arquitetura do backend
- Adicionar cache "já que vou mexer nisso"
- Refatorar services "para ficar mais limpo"
- Implementar features "que podem ser úteis depois"
- Ler artigos sobre "Spring Boot best practices"

**Mantra**: *"Backend está bom o suficiente. Foco no Android."*

---

## 📊 Tracking Progress

| Tarefa | Status | Data Conclusão |
|--------|--------|----------------|
| Spring Security | ⬜ | ___ |
| OpenAPI/Swagger | ⬜ | ___ |
| Validação Final | ⬜ | ___ |
| Setup Android | ⬜ | ___ |
| Tela Login | ⬜ | ___ |
| Lista EPIs | ⬜ | ___ |
| Registro Entrega | ⬜ | ___ |

**Atualizar diariamente**: Marcar ✅ quando concluído

---

## 🎬 Próxima Ação IMEDIATA

**AGORA**: 
```bash
# 1. Adicionar Spring Security ao pom.xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

# 2. Rodar aplicação e ver erro de login
./mvnw spring-boot:run

# 3. Começar a configurar SecurityFilterChain
```

**Não planeje mais. Comece. ⚡**

---

## 📝 Notas

- Backend está em **excelente estado** para MVP
- Tudo além de Security + Docs = YAGNI (You Ain't Gonna Need It)
- Android vai revelar o que realmente falta no backend
- Iterar é melhor que planejar perfeitamente

**Lembre-se**: Um protótipo funcionando em 2 semanas > Backend perfeito que nunca vira app. 🚀
