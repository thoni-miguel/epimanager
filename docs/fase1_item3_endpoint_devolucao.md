# Resumo - Endpoint de Devolução de EPIs

**Data**: 06 de Dezembro de 2025  
**Status**: ✅ Implementado e Compilado

---

## ✅ O que foi criado

### 1. **EntregaService.registrarDevolucao()** (NOVO)

Método para processar devolução de EPIs.

**Funcionalidades:**
- ✅ Busca entrega por ID
- ✅ Valida se entrega existe
- ✅ Valida se EPI já foi devolvido (evita devolução dupla)
- ✅ Incrementa estoque de volta (`estoqueAtual + 1`)
- ✅ Marca data de devolução (`dataDevolucao = hoje`)
- ✅ Usa `@Transactional` para garantir atomicidade

**Código:**
```java
@Transactional
public Entrega registrarDevolucao(Long entregaId) {
    // 1. Buscar entrega
    Entrega entrega = entregaRepository.findById(entregaId)
        .orElseThrow(() -> new RuntimeException("Entrega não encontrada"));

    // 2. Validar se já não foi devolvida
    if (entrega.getDataDevolucao() != null) {
        throw new RuntimeException("EPI já foi devolvido anteriormente em " 
            + entrega.getDataDevolucao());
    }

    // 3. Incrementar estoque de volta
    Epi epi = entrega.getEpi();
    epi.setEstoqueAtual(epi.getEstoqueAtual() + 1);
    epiRepository.save(epi);

    // 4. Marcar como devolvido
    entrega.setDataDevolucao(LocalDate.now());
    
    return entregaRepository.save(entrega);
}
```

---

### 2. **EntregaController - PUT /entregas/{id}/devolver** (NOVO)

Endpoint para registrar devolução.

**Detalhes:**
- **Método**: PUT
- **URL**: `/entregas/{id}/devolver`
- **Autenticação**: Basic Auth
- **Path Parameter**: `id` (Long) - ID da entrega

**Swagger Annotations:**
```java
@Operation(
    summary = "Registrar devolução de EPI", 
    description = "Marca EPI como devolvido e incrementa estoque. Não pode devolver EPI já devolvido."
)
```

---

## 🎯 Fluxo no Android

### Devolver EPI

```kotlin
// 1. Usuário seleciona entrega ativa (dataDevolucao = null)
val entregaId = 1L

// 2. Chamar endpoint de devolução
apiService.devolverEpi(entregaId)

// Backend:
// - Valida entrega
// - Incrementa estoque (+1)
// - Marca dataDevolucao = hoje
// - Retorna entrega atualizada

// Response (200 OK):
{
  "id": 1,
  "funcionario": {...},
  "epi": {...},
  "dataEntrega": "2025-11-20",
  "dataLimiteTroca": "2026-05-19",
  "dataDevolucao": "2025-12-06",  // ← Marcado como devolvido
  "fotoPath": "uploads/fotos/...",
  "assinaturaPath": "uploads/assinaturas/..."
}
```

---

## 📝 Casos de Uso

### Caso 1: Devolução Normal (Sucesso)
```
Entrega criada em: 2025-11-20
Limite de troca: 2026-05-19
Estoque atual EPI: 9

→ PUT /entregas/1/devolver

Backend:
✅ Entrega encontrada
✅ dataDevolucao = null (pode devolver)
✅ Estoque incrementado: 9 → 10
✅ dataDevolucao = 2025-12-06

Response (200): Entrega atualizada
```

---

### Caso 2: EPI Já Devolvido (Erro)
```
Entrega já devolvida em: 2025-12-01

→ PUT /entregas/1/devolver

Backend:
❌ dataDevolucao != null
❌ Lança RuntimeException

Response (500): "EPI já foi devolvido anteriormente em 2025-12-01"
```

---

### Caso 3: Entrega Não Existe (Erro)
```
→ PUT /entregas/999/devolver

Backend:
❌ Entrega não encontrada

Response (500): "Entrega não encontrada"
```

---

## 🔄 Ciclo de Vida Completo do EPI

```
1. CRIAÇÃO
   POST /epis
   estoque = 10

2. ENTREGA
   POST /entregas {funcionarioId: 1, epiId: 5}
   estoque = 9
   dataDevolucao = null

3. USO
   (funcionário usando EPI)
   
4. DEVOLUÇÃO
   PUT /entregas/1/devolver
   estoque = 10 (de volta)
   dataDevolucao = 2025-12-06
   
5. NOVA ENTREGA
   POST /entregas {funcionarioId: 2, epiId: 5}
   estoque = 9
   (EPI pode ser reutilizado)
```

---

## 📊 Exemplo de Requisição

### PUT `/entregas/1/devolver`

**Headers:**
```
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

**Response (200 OK):**
```json
{
  "id": 1,
  "funcionario": {
    "id": 1,
    "nome": "João da Silva",
    "cpf": "123.456.789-00"
  },
  "epi": {
    "id": 5,
    "nome": "Botina de segurança",
    "ca": "00000",
    "estoqueAtual": 10,
    "limiteTrocaEmDias": 365
  },
  "dataEntrega": "2025-11-20",
  "dataLimiteTroca": "2026-05-19",
  "dataDevolucao": "2025-12-06",
  "fotoPath": "uploads/fotos/20251120_143706_abc123.jpg",
  "assinaturaPath": "uploads/assinaturas/20251120_143706_xyz789.png"
}
```

---

## 🧪 Teste Manual (cURL)

### Registrar Entrega (preparação)
```bash
curl -u admin:admin123 -X POST http://localhost:8080/entregas \
  -H "Content-Type: application/json" \
  -d '{
    "funcionarioId": 1,
    "epiId": 5,
    "fotoBase64": null,
    "assinaturaBase64": null
  }'

# Response: {"id": 1, ...}
```

### Devolver EPI
```bash
curl -u admin:admin123 -X PUT http://localhost:8080/entregas/1/devolver
```

### Verificar Estoque
```bash
curl -u admin:admin123 http://localhost:8080/epis/5

# estoqueAtual deve ter incrementado em 1
```

---

## 💡 Benefícios

### Para o Sistema:
- ✅ **Controle de estoque preciso** - EPIs devolvidos voltam ao estoque
- ✅ **Rastreabilidade** - Data exata de devolução registrada
- ✅ **Reutilização** - EPIs podem ser entregues novamente
- ✅ **Validação** - Impede devolução duplicada

### Para o Android:
- ✅ **Fluxo completo** - Entrega → Uso → Devolução
- ✅ **UI de histórico** - Pode mostrar EPIs devolvidos vs ativos
- ✅ **Filtros** - Entregas ativas (`dataDevolucao = null`) vs devolvidas
- ✅ **Ciclo completo** - Funcionário pode devolver e receber novo EPI

---

## 🎨 Sugestões de UI no Android

### Tela de Entregas Ativas
```
+---------------------------------+
| Entregas Ativas                 |
+---------------------------------+
| João da Silva                   |
| Botina de segurança             |
| Entrega: 20/11/2025             |
| Vencimento: 19/05/2026          |
|         [DEVOLVER EPI] ← Button |
+---------------------------------+
```

### Após Devolver
```
+---------------------------------+
| Entrega Devolvida ✓             |
+---------------------------------+
| João da Silva                   |
| Botina de segurança             |
| Entrega: 20/11/2025             |
| Devolução: 06/12/2025           |
+---------------------------------+
```

---

## 🔍 Queries Úteis no Android

### Listar Entregas Ativas (não devolvidas)
```kotlin
// Backend deveria ter endpoint:
// GET /entregas/ativas
// WHERE dataDevolucao IS NULL

// Por enquanto, filtrar no app:
val entregasAtivas = entregas.filter { it.dataDevolucao == null }
```

### Listar Entregas de um Funcionário
```kotlin
// Backend deveria ter endpoint:
// GET /entregas/funcionario/{id}

// Por enquanto, filtrar no app:
val entregasFuncionario = entregas.filter { it.funcionario.id == funcionarioId }
```

---

**Status**: ✅ Pronto para usar no Android  
**Próximo**: Exception Handler Global (Item 4)
