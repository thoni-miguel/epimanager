# Resumo - Exception Handler Global

**Data**: 06 de Dezembro de 2025  
**Status**: ✅ Implementado e Compilado

---

## ✅ O que foi criado

### 1. **Custom Exceptions** (3 novas)

#### `ResourceNotFoundException` (404 Not Found)
Lançada quando um recurso não é encontrado.

**Uso:**
```java
throw new ResourceNotFoundException("Funcionário não encontrado com ID: " + id);
throw new ResourceNotFoundException("EPI não encontrado com ID: " + id);
throw new ResourceNotFoundException("Entrega não encontrada com ID: " + id);
```

---

#### `EstoqueInsuficienteException` (409 Conflict)
Lançada quando não há estoque disponível.

**Uso:**
```java
throw new EstoqueInsuficienteException("EPI 'Botina de segurança' sem estoque disponível");
```

---

#### `BusinessException` (409 Conflict)
Lançada para conflitos de regras de negócio.

**Uso:**
```java
throw new BusinessException("EPI já foi devolvido anteriormente em 2025-12-01");
throw new BusinessException("Usuário já existe");
```

---

### 2. **ErrorResponse DTO**

Resposta padronizada para erros.

**Estrutura:**
```java
public record ErrorResponse(
    String code,           // "ESTOQUE_INSUFICIENTE"
    String message,        // "EPI 'Botina' sem estoque disponível"
    LocalDateTime timestamp // "2025-12-06T14:05:30"
)
```

**Exemplo JSON:**
```json
{
  "code": "ESTOQUE_INSUFICIENTE",
  "message": "EPI 'Botina de segurança' sem estoque disponível",
  "timestamp": "2025-12-06T14:05:30.123"
}
```

---

### 3. **GlobalExceptionHandler** (@ControllerAdvice)

Intercepta exceções e retorna respostas HTTP adequadas.

**Handlers implementados:**

| Exception | HTTP Status | Código |
|-----------|-------------|--------|
| `ResourceNotFoundException` | 404 Not Found | `RESOURCE_NOT_FOUND` |
| `EstoqueInsuficienteException` | 409 Conflict | `ESTOQUE_INSUFICIENTE` |
| `BusinessException` | 409 Conflict | `BUSINESS_CONFLICT` |
| `BadCredentialsException` | 401 Unauthorized | `INVALID_CREDENTIALS` |
| `Exception` (qualquer outra) | 500 Internal Server Error | `INTERNAL_ERROR` |

---

## 🔍 Antes vs Depois

### **ANTES** - Sem Exception Handler

```java
// Service
if (epi.getEstoqueAtual() <= 0) {
    throw new RuntimeException("EPI sem estoque");
}

// Response HTTP: 500 Internal Server Error
{
  "timestamp": "2025-12-06T14:05:30",
  "status": 500,
  "error": "Internal Server Error",
  "message": "EPI sem estoque",
  "path": "/entregas"
}
```

**Problema no Android:**
- ❌ Status 500 = erro genérico
- ❌ Formato de erro não padronizado
- ❌ Difícil identificar o tipo de erro

---

### **DEPOIS** - Com Exception Handler

```java
// Service
if (epi.getEstoqueAtual() <= 0) {
    throw new EstoqueInsuficienteException("EPI 'Botina de segurança' sem estoque disponível");
}

// Response HTTP: 409 Conflict
{
  "code": "ESTOQUE_INSUFICIENTE",
  "message": "EPI 'Botina de segurança' sem estoque disponível",
  "timestamp": "2025-12-06T14:05:30.123"
}
```

**Vantagens no Android:**
- ✅ Status 409 = conflito específico
- ✅ Código `ESTOQUE_INSUFICIENTE` para detectar o tipo
- ✅ Mensagem descritiva para mostrar ao usuário
- ✅ Formato padronizado

---

## 🎯 Uso no Android

### Tratamento de Erros por Código

```kotlin
// Retrofit service
try {
    val entrega = apiService.createEntrega(request)
    // Sucesso
} catch (e: HttpException) {
    when (e.code()) {
        404 -> {
            // Recurso não encontrado
            val error = parseError(e.response()?.errorBody())
            showError("Não encontrado: ${error.message}")
        }
        409 -> {
            // Conflito
            val error = parseError(e.response()?.errorBody())
            when (error.code) {
                "ESTOQUE_INSUFICIENTE" -> {
                    showDialog("Sem Estoque", error.message)
                }
                "BUSINESS_CONFLICT" -> {
                    showError(error.message)
                }
            }
        }
        401 -> {
            // Não autenticado
            redirectToLogin()
        }
        500 -> {
            // Erro interno
            showError("Erro no servidor. Tente novamente mais tarde.")
        }
    }
}

// Helper para parsear erro
fun parseError(errorBody: ResponseBody?): ErrorResponse {
    val gson = Gson()
    return gson.fromJson(errorBody?.string(), ErrorResponse::class.java)
}

// DTO no Android
data class ErrorResponse(
    val code: String,
    val message: String,
    val timestamp: String
)
```

---

## 📝 Exemplos de Erros por Cenário

### Cenário 1: Funcionário Não Encontrado
```bash
POST /entregas
{
  "funcionarioId": 999,
  "epiId": 1
}

Response (404):
{
  "code": "RESOURCE_NOT_FOUND",
  "message": "Funcionário não encontrado com ID: 999",
  "timestamp": "2025-12-06T14:05:30"
}
```

---

### Cenário 2: EPI Sem Estoque
```bash
POST /entregas
{
  "funcionarioId": 1,
  "epiId": 5
}

Response (409):
{
  "code": "ESTOQUE_INSUFICIENTE",
  "message": "EPI 'Botina de segurança' sem estoque disponível",
  "timestamp": "2025-12-06T14:05:30"
}
```

---

### Cenário 3: Devolução Duplicada
```bash
PUT /entregas/1/devolver

Response (409):
{
  "code": "BUSINESS_CONFLICT",
  "message": "EPI já foi devolvido anteriormente em 2025-12-01",
  "timestamp": "2025-12-06T14:05:30"
}
```

---

### Cenário 4: Login Inválido
```bash
POST /auth/login
{
  "username": "admin",
  "password": "senhaerrada"
}

Response (401):
{
  "code": "INVALID_CREDENTIALS",
  "message": "Usuário ou senha inválidos",
  "timestamp": "2025-12-06T14:05:30"
}
```

---

### Cenário 5: Erro Inesperado
```bash
# Qualquer erro não tratado

Response (500):
{
  "code": "INTERNAL_ERROR",
  "message": "Erro interno do servidor. Contate o suporte.",
  "timestamp": "2025-12-06T14:05:30"
}
```

---

## 🔧 Services Atualizados

### EntregaService

**Mudanças:**
```java
// ANTES
throw new RuntimeException("Funcionário não encontrado");
throw new RuntimeException("EPI não encontrado");
throw new RuntimeException("EPI sem estoque");
throw new RuntimeException("Entrega não encontrada");
throw new RuntimeException("EPI já foi devolvido...");

// DEPOIS
throw new ResourceNotFoundException("Funcionário não encontrado com ID: " + id);
throw new ResourceNotFoundException("EPI não encontrado com ID: " + id);
throw new EstoqueInsuficienteException("EPI '" + epi.getNome() + "' sem estoque disponível");
throw new ResourceNotFoundException("Entrega não encontrada com ID: " + id);
throw new BusinessException("EPI já foi devolvido anteriormente em " + data);
```

---

## 💡 Benefícios

### Para o Backend:
- ✅ **Erros específicos** - Status HTTP corretos (404, 409, 401, 500)
- ✅ **Código de erro** - Facilita internacionalização
- ✅ **Mensagens descritivas** - Inclui IDs e nomes de recursos
- ✅ **Centralizado** - Um handler para toda a aplicação

### Para o Android:
- ✅ **Tratamento granular** - Pode mostrar UI diferente por tipo de erro
- ✅ **Mensagens ao usuário** - Feedback claro do que aconteceu
- ✅ **Debug facilitado** - Timestamp e código ajudam a rastrear
- ✅ **UX melhorada** - Pode sugerir ações (ex: "Sem estoque? Ver outros EPIs")

---

## 🧪 Teste Manual (cURL)

### Testar 404 - Funcionário não encontrado
```bash
curl -u admin:admin123 -X POST http://localhost:8080/entregas \
  -H "Content-Type: application/json" \
  -d '{
    "funcionarioId": 999,
    "epiId": 1
  }'

# Esperado: 404 + código RESOURCE_NOT_FOUND
```

### Testar 409 - Estoque insuficiente
```bash
# 1. Criar entrega até zerar estoque
# 2. Tentar criar mais uma

curl -u admin:admin123 -X POST http://localhost:8080/entregas \
  -H "Content-Type: application/json" \
  -d '{
    "funcionarioId": 1,
    "epiId": 1
  }'

# Esperado: 409 + código ESTOQUE_INSUFICIENTE
```

### Testar 409 - Devolução duplicada
```bash
# 1. Criar entrega
curl -u admin:admin123 -X POST http://localhost:8080/entregas \
  -H "Content-Type: application/json" \
  -d '{"funcionarioId": 1, "epiId": 2}'

# 2. Devolver
curl -u admin:admin123 -X PUT http://localhost:8080/entregas/1/devolver

# 3. Tentar devolver novamente
curl -u admin:admin123 -X PUT http://localhost:8080/entregas/1/devolver

# Esperado: 409 + código BUSINESS_CONFLICT
```

---

## 📚 Códigos de Erro Disponíveis

| Código | Significado | HTTP Status |
|--------|-------------|-------------|
| `RESOURCE_NOT_FOUND` | Recurso não existe | 404 |
| `ESTOQUE_INSUFICIENTE` | Sem estoque do EPI | 409 |
| `BUSINESS_CONFLICT` | Conflito de regra de negócio | 409 |
| `INVALID_CREDENTIALS` | Login inválido | 401 |
| `INTERNAL_ERROR` | Erro inesperado | 500 |

**Futuro:** Adicionar mais conforme necessário:
- `VALIDATION_ERROR` (400) - Dados inválidos
- `FORBIDDEN` (403) - Sem permissão
- `DUPLICATE_RESOURCE` (409) - Recurso já existe

---

## ✅ Checklist de Implementação

- [x] Criar custom exceptions (ResourceNotFoundException, EstoqueInsuficienteException, BusinessException)
- [x] Criar ErrorResponse DTO
- [x] Criar GlobalExceptionHandler com @ControllerAdvice
- [x] Atualizar EntregaService para usar exceptions customizadas
- [x] Testar compilação
- [ ] (Opcional) Atualizar outros services (EpiService, AuthService)
- [ ] (Opcional) Adicionar mais handlers conforme necessário

---

**Status**: ✅ Pronto para usar no Android  
**Fase 1 Completa**: 4/4 itens implementados! 🎉

**Próximo**: Iniciar desenvolvimento Android (Fase 2)
