# Respostas - Preparação Backend para Frontend Android

**Data**: 06 de Dezembro de 2025  
**Status**: ✅ Backend Preparado - Fase 1 Completa  
**Documento de Referência**: [frontend_planning.md](frontend_planning.md)

---

## 📋 Índice

1. [Resumo Executivo](#resumo-executivo)
2. [Decisões Tomadas](#decisões-tomadas)
3. [Implementações Realizadas](#implementações-realizadas)
4. [Endpoints Disponíveis](#endpoints-disponíveis)
5. [Guias de Integração](#guias-de-integração)

---

## 🎯 Resumo Executivo

### O que foi feito

Implementamos **TODOS** os 4 itens essenciais da Fase 1 para preparação do backend:

- ✅ **Item 1**: Endpoints de Cargos e Atividades
- ✅ **Item 2**: Sistema de Upload Base64
- ✅ **Item 3**: Endpoint de Devolução de EPIs
- ✅ **Item 4**: Exception Handler Global

### Backend agora possui

- **16 endpoints** (antes: 11)
- **Erros HTTP adequados** (404, 409, 401, 500)
- **Upload de imagens** via Base64 inline
- **Ciclo completo** de entregas (criar → devolver)
- **Endpoints para população de dropdowns** (atividades, cargos)

---

## ✅ Decisões Tomadas

### Questão 1: Upload de Fotos e Assinaturas

**Decisão**: ✅ **Opção B - Base64 inline no JSON**

**Implementação:**
- Campos em `EntregaRequest`: `fotoBase64` e `assinaturaBase64`
- `StorageService` decodifica Base64 e salva em `uploads/fotos/` e `uploads/assinaturas/`
- Retorna path do arquivo salvo

**Motivos:**
- ✅ Uma única requisição POST
- ✅ Mais simples para MVP
- ✅ Funciona bem para imagens < 1MB
- ✅ JSON puro (fácil de testar no Swagger)

**Como usar no Android:**
```kotlin
val photoBase64 = bitmapToBase64(photoBitmap)
val signatureBase64 = bitmapToBase64(signatureBitmap)

val request = EntregaRequest(
    funcionarioId = 1,
    epiId = 5,
    fotoBase64 = photoBase64,
    assinaturaBase64 = signatureBase64
)
```

**Documentação**: [fase1_item2_upload_base64.md](fase1_item2_upload_base64.md)

---

### Questão 2: Endpoint de Devolução

**Decisão**: ✅ **Implementar agora**

**Implementação:**
- Endpoint: `PUT /entregas/{id}/devolver`
- Valida se EPI já foi devolvido
- Incrementa estoque de volta
- Marca `dataDevolucao = hoje`

**Motivos:**
- ✅ Simples de implementar (15 min)
- ✅ Completa ciclo de vida do EPI
- ✅ Essencial para fluxo completo

**Como usar no Android:**
```kotlin
apiService.devolverEpi(entregaId = 1)
// Backend incrementa estoque e marca dataDevolucao
```

**Documentação**: [fase1_item3_endpoint_devolucao.md](fase1_item3_endpoint_devolucao.md)

---

### Questão 3: Tecnologia do Frontend

**Decisão**: ✅ **Android Nativo (Kotlin + Jetpack Compose)**

**Motivos:**
- ✅ Projeto já configurado em Kotlin
- ✅ MVP focado apenas em Android
- ✅ Acesso nativo a câmera e canvas (assinatura)
- ✅ Jetpack Compose = padrão moderno

**Confirmado**: Prosseguir com Android nativo.

---

### Questão 4: Design e UX do Aplicativo

**Decisão**: ❌ **Não tenho mockups** - Sugiro arquitetura de telas

**Telas sugeridas para MVP:**

1. **Login** (`/auth/login`)
2. **Dashboard** (resumo com cards)
3. **Lista de Funcionários** (`GET /funcionarios`)
   - Busca por nome
   - Botão "Criar Funcionário"
4. **Cadastro de Funcionário** (`POST /funcionarios`)
   - Dropdown Atividade (`GET /atividades`)
   - Dropdown Cargo filtrado (`GET /cargos/atividade/{id}`)
5. **Lista de EPIs** (`GET /epis`)
   - Mostra estoque atual
6. **Cadastro de EPI** (`POST /epis`)
7. **Registrar Entrega** (`POST /entregas`)
   - Seleciona funcionário
   - Mostra EPIs recomendados (`GET /epis/recomendados?cargoId={id}`)
   - Tira foto
   - Captura assinatura
8. **Entregas Vencendo** (`GET /entregas/vencendo?dias=30`)
9. **Histórico de Entregas** (por funcionário)

---

### Questão 5: Funcionalidades Offline

**Decisão**: ❌ **Não - Apenas online**

**Motivos:**
- ✅ MVP interno (Wi-Fi garantido)
- ✅ Offline = MUITA complexidade (Room, sync, conflitos)
- ✅ Pode adicionar depois se necessário

**Confirmado**: App funciona apenas com internet.

---

### Questão 6: Controle de Permissões/Roles

**Decisão**: ❌ **Não - Todos usuários com acesso total**

**Motivos:**
- ✅ Simplifica MVP
- ✅ Backend já tem infraestrutura (campo `role` em `User`)
- ✅ Fácil de adicionar depois

**Confirmado**: Todos usuários têm acesso total no MVP.

---

## 🛠️ Implementações Realizadas

### 1. Endpoints de Cargos e Atividades

**Novos endpoints:**

| Endpoint | Método | Descrição |
|----------|--------|-----------|
| `GET /cargos` | GET | Lista todos os cargos |
| `GET /cargos/{id}` | GET | Busca cargo por ID |
| `GET /cargos/atividade/{atividadeId}` | GET | **ESSENCIAL**: Cargos de uma atividade |
| `GET /atividades` | GET | Lista todas as atividades |
| `GET /atividades/{id}` | GET | Busca atividade por ID |

**Fluxo no Android:**
```
1. Listar atividades (GET /atividades)
2. Usuário seleciona atividade
3. Listar cargos daquela atividade (GET /cargos/atividade/{id})
4. Usuário seleciona cargo
5. Criar funcionário (POST /funcionarios)
```

**Documentação**: [fase1_item1_cargos_atividades.md](fase1_item1_cargos_atividades.md)

---

### 2. Sistema de Upload Base64

**Modificado:**
- `EntregaRequest`: Campos `fotoBase64` e `assinaturaBase64`
- `EntregaService`: Decodifica Base64 e salva arquivos
- `StorageService`: Novo serviço para processar imagens

**Formato aceito:**
```
"data:image/jpeg;base64,/9j/4AAQ..."  (com header)
ou
"/9j/4AAQ..."  (apenas Base64)
```

**Arquivos salvos em:**
```
uploads/fotos/20251206_140530_abc123.jpg
uploads/assinaturas/20251206_140530_xyz789.png
```

**Conversão no Android:**
```kotlin
fun bitmapToBase64(bitmap: Bitmap): String {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
    val byteArray = outputStream.toByteArray()
    val base64String = Base64.encodeToString(byteArray, Base64.NO_WRAP)
    return "data:image/jpeg;base64,$base64String"
}
```

**Documentação**: [fase1_item2_upload_base64.md](fase1_item2_upload_base64.md)

---

### 3. Endpoint de Devolução

**Novo endpoint:**
- `PUT /entregas/{id}/devolver`

**Funcionalidades:**
- ✅ Valida se entrega existe
- ✅ Valida se já não foi devolvida
- ✅ Incrementa estoque (+1)
- ✅ Marca `dataDevolucao = hoje`

**Ciclo completo:**
```
Criar EPI → Entregar → Usar → Devolver → Reutilizar
```

**Documentação**: [fase1_item3_endpoint_devolucao.md](fase1_item3_endpoint_devolucao.md)

---

### 4. Exception Handler Global

**Custom exceptions criadas:**
- `ResourceNotFoundException` → 404 Not Found
- `EstoqueInsuficienteException` → 409 Conflict
- `BusinessException` → 409 Conflict

**ErrorResponse padronizado:**
```json
{
  "code": "ESTOQUE_INSUFICIENTE",
  "message": "EPI 'Botina de segurança' sem estoque disponível",
  "timestamp": "2025-12-06T14:05:30"
}
```

**Tratamento no Android:**
```kotlin
try {
    val entrega = apiService.createEntrega(request)
} catch (e: HttpException) {
    when (e.code()) {
        404 -> showError("Não encontrado")
        409 -> {
            val error = parseError(e.response()?.errorBody())
            when (error.code) {
                "ESTOQUE_INSUFICIENTE" -> showDialog("Sem Estoque", error.message)
                "BUSINESS_CONFLICT" -> showError(error.message)
            }
        }
        401 -> redirectToLogin()
        500 -> showError("Erro no servidor")
    }
}
```

**Documentação**: [fase1_item4_exception_handler.md](fase1_item4_exception_handler.md)

---

## 🌐 Endpoints Disponíveis

### Resumo Completo (16 endpoints)

| Endpoint | Método | Acesso | Descrição |
|----------|--------|--------|-----------|
| **Autenticação** ||||
| `/auth/register` | POST | 🌍 Público | Registrar usuário |
| `/auth/login` | POST | 🌍 Público | Login |
| **Funcionários** ||||
| `/funcionarios` | GET | 🔒 Auth | Listar todos |
| `/funcionarios` | POST | 🔒 Auth | Criar |
| `/funcionarios/{id}` | GET | 🔒 Auth | Buscar por ID |
| **EPIs** ||||
| `/epis` | GET | 🔒 Auth | Listar todos |
| `/epis` | POST | 🔒 Auth | Criar |
| `/epis/recomendados?cargoId={id}` | GET | 🔒 Auth | Recomendados |
| **Atividades** ||||
| `/atividades` | GET | 🔒 Auth | Listar todas |
| `/atividades/{id}` | GET | 🔒 Auth | Buscar por ID |
| **Cargos** ||||
| `/cargos` | GET | 🔒 Auth | Listar todos |
| `/cargos/{id}` | GET | 🔒 Auth | Buscar por ID |
| `/cargos/atividade/{atividadeId}` | GET | 🔒 Auth | Por atividade |
| **Entregas** ||||
| `/entregas` | POST | 🔒 Auth | Registrar |
| `/entregas/vencendo?dias={n}` | GET | 🔒 Auth | Vencendo |
| `/entregas/{id}/devolver` | PUT | 🔒 Auth | Devolver |

**Total**: 2 públicos + 14 protegidos

---

## 📚 Guias de Integração

### Autenticação (Basic Auth)

**Setup no Retrofit:**
```kotlin
val interceptor = Interceptor { chain ->
    val credentials = "$username:$password"
    val basic = "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
    
    val request = chain.request().newBuilder()
        .addHeader("Authorization", basic)
        .build()
    
    chain.proceed(request)
}

val client = OkHttpClient.Builder()
    .addInterceptor(interceptor)
    .build()

val retrofit = Retrofit.Builder()
    .client(client)
    .baseUrl("http://10.0.2.2:8080/")  // Emulador Android
    .addConverterFactory(GsonConverterFactory.create())
    .build()
```

---

### Fluxo Completo: Cadastro de Funcionário

```kotlin
// 1. Carregar atividades
val atividades = apiService.getAtividades()
// [{id: 1, nome: "FLORESTAL"}, {id: 2, nome: "CONSTRUÇÃO"}]

// 2. Usuário seleciona atividade (id: 1)

// 3. Carregar cargos daquela atividade
val cargos = apiService.getCargosByAtividade(1)
// [{id: 5, nome: "OPERADOR MOTOSSERAS", atividade: {...}}]

// 4. Usuário preenche dados e seleciona cargo (id: 5)

// 5. Criar funcionário
val funcionario = FuncionarioRequest(
    nome = "João da Silva",
    cpf = "123.456.789-00",
    cargo = CargoRef(id = 5)
)
val created = apiService.createFuncionario(funcionario)
```

---

### Fluxo Completo: Registro de Entrega

```kotlin
// 1. Selecionar funcionário
val funcionario = selectedFuncionario // do dropdown

// 2. Buscar EPIs recomendados para o cargo do funcionário
val episRecomendados = apiService.getEpisRecomendados(funcionario.cargo.id)

// 3. Usuário seleciona EPI e tira foto
val photoBitmap = camera.takePicture()
val photoBase64 = bitmapToBase64(photoBitmap)

// 4. Usuário assina
val signatureBitmap = signatureCanvas.getBitmap()
val signatureBase64 = bitmapToBase64(signatureBitmap)

// 5. Criar entrega
val entrega = EntregaRequest(
    funcionarioId = funcionario.id,
    epiId = selectedEpi.id,
    fotoBase64 = photoBase64,
    assinaturaBase64 = signatureBase64
)

try {
    val created = apiService.createEntrega(entrega)
    showSuccess("Entrega registrada!")
} catch (e: HttpException) {
    when (e.code()) {
        409 -> {
            val error = parseError(e.response()?.errorBody())
            if (error.code == "ESTOQUE_INSUFICIENTE") {
                showDialog("Sem Estoque", error.message)
            }
        }
    }
}
```

---

## 🔍 Informações Importantes

### Base URL

**Desenvolvimento Local:**
- Emulador Android: `http://10.0.2.2:8080/`
- Dispositivo físico: `http://<IP_DA_MAQUINA>:8080/`

### Autenticação

**Tipo**: HTTP Basic Auth  
**Como usar**: Header `Authorization: Basic base64(username:password)`

**Exemplo:**
```
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

### Swagger UI

**URL**: `http://localhost:8080/swagger-ui.html`  
**Uso**: Testar endpoints manualmente durante desenvolvimento

---

## 📂 Documentação Adicional

Todos os detalhes de implementação estão documentados em:

1. [fase1_item1_cargos_atividades.md](fase1_item1_cargos_atividades.md) - Endpoints de cargos/atividades
2. [fase1_item2_upload_base64.md](fase1_item2_upload_base64.md) - Upload de imagens
3. [fase1_item3_endpoint_devolucao.md](fase1_item3_endpoint_devolucao.md) - Devolução de EPIs
4. [fase1_item4_exception_handler.md](fase1_item4_exception_handler.md) - Tratamento de erros

**Referências gerais:**
- [backend_review.md](backend_review.md) - Análise completa da arquitetura
- [guia_swagger.md](guia_swagger.md) - Como usar Swagger UI
- [guia_autenticacao.md](guia_autenticacao.md) - Detalhes de autenticação

---

## ✅ Checklist de Preparação

### Backend
- [x] Endpoints de Cargos e Atividades
- [x] Upload Base64 de fotos/assinaturas
- [x] Endpoint de devolução
- [x] Exception handler global
- [x] Swagger documentado
- [x] Compilação bem-sucedida
- [x] Documentação completa

### Próximos Passos (Android)
- [ ] Setup projeto Kotlin + Jetpack Compose
- [ ] Configurar Retrofit + Hilt
- [ ] Implementar tela de Login
- [ ] Implementar CRUD de Funcionários
- [ ] Implementar CRUD de EPIs
- [ ] Implementar Registro de Entregas (com câmera + assinatura)
- [ ] Implementar listagem de vencimentos

---

## 🎯 Conclusão

**Backend está 100% preparado para iniciar desenvolvimento Android!**

- ✅ Todos os endpoints necessários implementados
- ✅ Upload de imagens via Base64
- ✅ Erros HTTP adequados
- ✅ Documentação completa
- ✅ Testado e compilado

**Pode começar o desenvolvimento do app Android com segurança!** 🚀📱

---

**Data de conclusão**: 06 de Dezembro de 2025  
**Backend Version**: v1.0.0-MVP  
**Status**: ✅ Pronto para Produção (MVP)
