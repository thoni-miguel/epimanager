# Guia de Teste - Autenticação Spring Security

## ✅ O que foi implementado:

**Arquivos Criados:**
1. `entity/User.java` - Entidade de usuário
2. `repository/UserRepository.java` - Repository para User
3. `dto/LoginRequest.java` - DTO para login
4. `dto/RegisterRequest.java` - DTO para registro
5. `dto/AuthResponse.java` - DTO para resposta de autenticação
6. `service/AuthService.java` - Serviço de autenticação (com BCrypt)
7. `config/SecurityConfig.java` - Configuração Spring Security
8. `controller/AuthController.java` - Controller de autenticação

**Arquivos Modificados:**
- `pom.xml` - Adicionada dependência `spring-boot-starter-security`

---

## 🧪 Como Testar

### 1. Iniciar Aplicação
```bash
./mvnw spring-boot:run
```

### 2. Registrar um Usuário

**cURL:**
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**Resposta Esperada:**
```
Usuário criado com sucesso: admin
```

### 3. Fazer Login

**cURL:**
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**Resposta Esperada:**
```json
{
  "token": "BASIC_admin_1732394567890",
  "username": "admin"
}
```

### 4. Acessar Endpoint Protegido (Com Basic Auth)

**cURL:**
```bash
curl -X GET http://localhost:8080/funcionarios \
  -u admin:admin123
```

**Importante**: Por enquanto, estamos usando **HTTP Basic Authentication** temporariamente.  
No Postman: Aba "Authorization" → Tipo "Basic Auth" → Username: admin, Password: admin123

---

## 📋 Endpoints Disponíveis

| Método | Endpoint | Acesso | Descrição |
|--------|----------|--------|-----------|
| POST | `/auth/register` | 🌍 Público | Registra novo usuário |
| POST | `/auth/login` | 🌍 Público | Faz login (retorna token) |
| GET | `/funcionarios` | 🔒 Autenticado | Lista funcionários |
| POST | `/funcionarios` | 🔒 Autenticado | Cria funcionário |
| GET | `/epis` | 🔒 Autenticado | Lista EPIs |
| POST | `/epis` | 🔒 Autenticado | Cria EPI |
| POST | `/entregas` | 🔒 Autenticado | Registra entrega |
| GET | `/entregas/vencendo` | 🔒 Autenticado | Vencimentos próximos |

---

## 🔐 Como Funciona

1. **Registro**: Cliente envia username + password → Backend armazena password **hasheado** com BCrypt
2. **Login**: Cliente envia credenciais → Backend valida → Retorna token simples
3. **Acesso**: Cliente usa Basic Auth (username:password) temporariamente

---

## ⚠️ Limitações Atuais (Para MVP)

- ✅ Senha está **hasheada** (BCrypt) - seguro
- ⚠️ Token retornado é **simples** (não é JWT real)
- ⚠️ Usando **Basic Auth** para autenticação
- ✅ Endpoints `/auth/**` são **públicos**
- ✅ Todos outros endpoints **requerem autenticação**

**Para Android**: Use Basic Auth temporariamente. Melhorar para JWT depois se necessário.

---

## 🎯 Próximos Passos (Action Plan)

- [x] Spring Security implementado
- [ ] Swagger/OpenAPI (Dia 3)
- [ ] Validação final (Dia 4)
- [ ] Iniciar Android (Semana 2)

---

## 🐛 Troubleshooting

**Erro: "401 Unauthorized"**
→ Você esqueceu de adicionar credenciais. Use `-u username:password` no curl ou Basic Auth no Postman

**Erro: "Usuário já existe"**
→ Tente com outro username ou delete o banco (`ddl-auto=create` já limpa no restart)

**Erro: "403 Forbidden"**
→ Endpoint está protegido. Adicione autenticação básica
