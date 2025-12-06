# Frontend Android - Planejamento e Questões Pendentes

**Data**: 30 de Novembro de 2025  
**Status**: Análise do Backend Concluída - Planejamento do Frontend  
**Objetivo**: Documentar sugestões de melhorias no backend e questões para definir arquitetura do frontend Android

---

## 📋 Índice

1. [Análise do Backend](#análise-do-backend)
2. [Sugestões de Melhorias](#sugestões-de-melhorias)
3. [Questões para Decisão](#questões-para-decisão)
4. [Próximos Passos](#próximos-passos)

---

## ✅ Análise do Backend

### Resumo
O backend está **muito bem estruturado** e **adequado para MVP**. A documentação está excelente e o sistema está funcional.

### Pontos Fortes
- ✅ Arquitetura em camadas clara (Controller → Service → Repository)
- ✅ Modelo de dados bem pensado com relacionamentos corretos
- ✅ Lógica de negócio importante implementada (cálculo de vencimento, controle de estoque)
- ✅ Segurança básica funcional (BCrypt + Spring Security)
- ✅ Data seeding para testes

### Endpoints Disponíveis para o Android

| Endpoint | Método | Descrição | Status |
|----------|--------|-----------|---------|
| `/auth/register` | POST | Registrar novo usuário | ✅ Pronto |
| `/auth/login` | POST | Autenticar usuário | ✅ Pronto |
| `/funcionarios` | GET | Listar funcionários | ✅ Pronto |
| `/funcionarios` | POST | Criar funcionário | ✅ Pronto |
| `/funcionarios/{id}` | GET | Buscar funcionário por ID | ✅ Pronto |
| `/epis` | GET | Listar EPIs | ✅ Pronto |
| `/epis` | POST | Criar novo EPI | ✅ Pronto |
| `/epis/recomendados?cargoId={id}` | GET | EPIs recomendados para cargo | ✅ Pronto |
| `/entregas` | POST | Registrar entrega de EPI | ✅ Pronto |
| `/entregas/vencendo?dias={n}` | GET | EPIs próximos do vencimento | ✅ Pronto |

---

## 💡 Sugestões de Melhorias

### 1. Autenticação (Prioridade: Média)

**Situação Atual**: Backend usa **HTTP Basic Auth** (credenciais em cada requisição)

**Opção A - Continuar com Basic Auth (Recomendado para MVP)**:
- ✅ Simples de implementar no Android
- ✅ Funciona perfeitamente para MVP interno
- ❌ Menos seguro (credenciais enviadas em toda requisição)

**Opção B - Migrar para JWT**:
- ✅ Mais seguro (token expira, stateless real)
- ✅ Padrão de mercado
- ❌ Requer implementação adicional no backend

**Recomendação**: 
- **Para MVP inicial**: Continuar com Basic Auth
- **Para produção**: Migrar para JWT

**Implementação JWT (se necessário)**:
```java
// Dependência adicional no pom.xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>

// JwtUtil.java - Geração e validação de tokens
// JwtAuthenticationFilter.java - Filtro para validar token
// Modificar SecurityConfig para usar JWT em vez de Basic Auth
```

---

### 2. Upload de Arquivos (Fotos e Assinaturas) (Prioridade: Alta)

**Situação Atual**: 
- ✅ Entidade `Entrega` possui campos `fotoPath` e `assinaturaPath`
- ❌ **Não há endpoints de upload implementados**

**Opção A - Upload Multipart (Tradicional)**:

```java
// Criar StorageService
@Service
public class StorageService {
    private final String uploadDir = "/uploads/";
    
    public String saveFoto(MultipartFile file) throws IOException {
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String path = uploadDir + "fotos/" + filename;
        Files.write(Paths.get(path), file.getBytes());
        return path;
    }
    
    public String saveAssinatura(MultipartFile file) throws IOException {
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String path = uploadDir + "assinaturas/" + filename;
        Files.write(Paths.get(path), file.getBytes());
        return path;
    }
}

// Adicionar endpoints em UploadController
@RestController
@RequestMapping("/uploads")
public class UploadController {
    
    @PostMapping("/foto")
    public ResponseEntity<Map<String, String>> uploadFoto(
        @RequestParam("file") MultipartFile file) {
        
        String path = storageService.saveFoto(file);
        return ResponseEntity.ok(Map.of("path", path));
    }
    
    @PostMapping("/assinatura")
    public ResponseEntity<Map<String, String>> uploadAssinatura(
        @RequestParam("file") MultipartFile file) {
        
        String path = storageService.saveAssinatura(file);
        return ResponseEntity.ok(Map.of("path", path));
    }
}

// Endpoint para servir arquivos
@GetMapping("/fotos/{filename}")
public ResponseEntity<Resource> servirFoto(@PathVariable String filename) {
    Resource file = storageService.loadAsResource("fotos/" + filename);
    return ResponseEntity.ok().body(file);
}
```

**Fluxo no Android**:
1. Usuário tira foto/assina
2. Android faz `POST /uploads/foto` com MultipartFile
3. Backend retorna `{"path": "/uploads/fotos/abc123.jpg"}`
4. Android usa esse path no `POST /entregas`

**Opção B - Base64 Inline (Mais Simples para MVP)**:

```java
// Modificar EntregaRequest
public record EntregaRequest(
    Long funcionarioId,
    Long epiId,
    String fotoBase64,        // "data:image/jpeg;base64,/9j/4AAQ..."
    String assinaturaBase64   // "data:image/png;base64,iVBORw0KG..."
) {}

// EntregaService
@Transactional
public Entrega registrarEntrega(EntregaRequest request) {
    // ... validações existentes ...
    
    // Decodificar e salvar Base64
    String fotoPath = null;
    if (request.fotoBase64() != null) {
        fotoPath = storageService.saveBase64Image(
            request.fotoBase64(), 
            "fotos"
        );
    }
    
    String assinaturaPath = null;
    if (request.assinaturaBase64() != null) {
        assinaturaPath = storageService.saveBase64Image(
            request.assinaturaBase64(), 
            "assinaturas"
        );
    }
    
    // ... resto da lógica ...
}
```

**Recomendação**: 
- **Para MVP**: Base64 inline (mais simples)
- **Para produção**: Multipart (melhor performance, permite validação de tamanho)

---

### 3. Endpoint de Devolução de EPI (Prioridade: Média)

**Situação Atual**:
- ✅ Campo `dataDevolucao` existe na entidade `Entrega`
- ❌ **Não há endpoint para registrar devoluções**

**Implementação Sugerida**:

```java
// EntregaController
@PutMapping("/{id}/devolver")
public ResponseEntity<Entrega> devolverEpi(@PathVariable Long id) {
    Entrega entrega = entregaService.registrarDevolucao(id);
    return ResponseEntity.ok(entrega);
}

// EntregaService
@Transactional
public Entrega registrarDevolucao(Long entregaId) {
    Entrega entrega = entregaRepository.findById(entregaId)
        .orElseThrow(() -> new RuntimeException("Entrega não encontrada"));
    
    if (entrega.getDataDevolucao() != null) {
        throw new RuntimeException("EPI já foi devolvido anteriormente");
    }
    
    // Incrementar estoque de volta
    Epi epi = entrega.getEpi();
    epi.setEstoqueAtual(epi.getEstoqueAtual() + 1);
    epiRepository.save(epi);
    
    // Marcar como devolvido
    entrega.setDataDevolucao(LocalDate.now());
    return entregaRepository.save(entrega);
}
```

**Casos de Uso**:
- Funcionário devolveu EPI antes do vencimento (troca antecipada)
- Funcionário foi desligado e devolveu todos os EPIs
- EPI foi danificado e precisa ser trocado

**Recomendação**: Implementar antes do MVP, pois completa o ciclo de vida do EPI.

---

### 4. Tratamento de Erros HTTP Adequado (Prioridade: Média)

**Problema Atual**: 
- Erro de estoque insuficiente retorna **500 Internal Server Error**
- Dificulta tratamento de erros específicos no Android

**Implementação Sugerida**:

```java
// Criar exceptions customizadas
public class EstoqueInsuficienteException extends RuntimeException {
    public EstoqueInsuficienteException(String message) {
        super(message);
    }
}

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

// Global Exception Handler
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(EstoqueInsuficienteException.class)
    public ResponseEntity<ErrorResponse> handleEstoqueInsuficiente(
        EstoqueInsuficienteException ex) {
        
        ErrorResponse error = new ErrorResponse(
            "ESTOQUE_INSUFICIENTE",
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
        ResourceNotFoundException ex) {
        
        ErrorResponse error = new ErrorResponse(
            "RESOURCE_NOT_FOUND",
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        ErrorResponse error = new ErrorResponse(
            "INTERNAL_ERROR",
            "Erro interno do servidor",
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

// DTO de resposta de erro
public record ErrorResponse(
    String code,
    String message,
    LocalDateTime timestamp
) {}
```

**Benefícios para o Android**:
- Pode mostrar mensagens específicas ("Sem estoque" vs "Funcionário não encontrado")
- Status HTTP corretos (409, 404, 400, etc.)
- Código de erro para internacionalização

**Recomendação**: Implementar antes do MVP para melhor UX no Android.

---

### 5. Paginação em Listagens (Prioridade: Baixa)

**Situação Atual**: 
- Endpoints retornam **todos** os registros
- OK para MVP com poucos dados (< 100 registros)

**Quando Implementar**: Se a base crescer (> 100 funcionários ou EPIs)

**Implementação Sugerida**:

```java
// FuncionarioController
@GetMapping
public Page<Funcionario> listar(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "20") int size,
    @RequestParam(defaultValue = "nome") String sort) {
    
    Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
    return funcionarioRepository.findAll(pageable);
}

// Resposta JSON
{
  "content": [...],        // Lista de funcionários
  "totalElements": 150,    // Total de registros
  "totalPages": 8,         // Total de páginas
  "size": 20,              // Tamanho da página
  "number": 0              // Página atual
}
```

**Recomendação**: Deixar para depois do MVP, a menos que você já saiba que terá muitos dados.

---

### 6. Filtros e Busca (Prioridade: Média)

**Funcionalidades Úteis para o Android**:

```java
// FuncionarioController
@GetMapping("/buscar")
public List<Funcionario> buscar(
    @RequestParam(required = false) String nome,
    @RequestParam(required = false) String cpf,
    @RequestParam(required = false) Long cargoId) {
    
    return funcionarioService.buscar(nome, cpf, cargoId);
}

// EpiController
@GetMapping("/buscar")
public List<Epi> buscar(@RequestParam String nome) {
    return epiRepository.findByNomeContainingIgnoreCase(nome);
}

// EntregaController
@GetMapping("/historico/{funcionarioId}")
public List<Entrega> historico(@PathVariable Long funcionarioId) {
    return entregaRepository.findByFuncionarioId(funcionarioId);
}
```

**Casos de Uso**:
- Buscar funcionário por nome parcial
- Ver histórico de entregas de um funcionário específico
- Buscar EPI pelo nome

**Recomendação**: Implementar ao menos busca por nome (melhora muito a UX).

---

### 7. Endpoint para Listar Cargos e Atividades (Prioridade: Alta)

**Situação Atual**: 
- Não há endpoints para listar Cargos e Atividades
- São necessários para criar Funcionários no Android

**Implementação Sugerida**:

```java
// CargoController (NOVO)
@RestController
@RequestMapping("/cargos")
public class CargoController {
    
    @GetMapping
    public List<Cargo> listar() {
        return cargoRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Cargo> buscar(@PathVariable Long id) {
        return cargoRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/atividade/{atividadeId}")
    public List<Cargo> listarPorAtividade(@PathVariable Long atividadeId) {
        return cargoRepository.findByAtividadeId(atividadeId);
    }
}

// AtividadeController (NOVO)
@RestController
@RequestMapping("/atividades")
public class AtividadeController {
    
    @GetMapping
    public List<Atividade> listar() {
        return atividadeRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Atividade> buscar(@PathVariable Long id) {
        return atividadeRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
```

**Fluxo no Android**:
1. Usuário abre tela de cadastro de funcionário
2. App faz `GET /atividades` para popular dropdown
3. Usuário seleciona atividade
4. App faz `GET /cargos/atividade/{id}` para carregar cargos daquela atividade
5. Usuário seleciona cargo e preenche nome/CPF
6. App faz `POST /funcionarios`

**Recomendação**: **ESSENCIAL** para o MVP - sem isso não dá para criar funcionários no Android.

---

## ❓ Questões para Decisão

### 1. Upload de Fotos e Assinaturas

**Qual abordagem devemos usar?**

- [ ] **Opção A**: Endpoint Multipart (`POST /uploads/foto`, `POST /uploads/assinatura`)
  - Mais profissional
  - Permite validação de tamanho/tipo
  - Requer mais código

- [ ] **Opção B**: Base64 inline no JSON da entrega
  - Mais simples para MVP
  - Tudo em uma requisição
  - Pode aumentar payload

- [ ] **Opção C**: Implementar junto com o desenvolvimento Android
  - Avaliar melhor opção durante implementação

**Recomendação do AI**: Opção B para MVP, migrar para Opção A se necessário.

---

### 2. Endpoint de Devolução

**Devemos implementar agora ou depois?**

- [ ] **Implementar agora** (antes do Android)
  - Completa o ciclo de vida do EPI
  - Permite testar fluxo completo

- [ ] **Implementar depois** (durante desenvolvimento Android)
  - Focar no MVP mínimo primeiro
  - Adicionar quando realmente necessário

**Recomendação do AI**: Implementar agora (é simples e importante para o fluxo).

---

### 3. Tecnologia do Frontend Android

**Qual stack vamos usar?**

- [ ] **Android Nativo** (Kotlin + Jetpack Compose)
  - ✅ Melhor performance
  - ✅ Acesso completo a APIs nativas
  - ✅ Padrão moderno do Android
  - ❌ Apenas Android (não funciona em iOS)

- [ ] **React Native**
  - ✅ Cross-platform (Android + iOS)
  - ✅ Pode reusar conhecimento web
  - ❌ Performance inferior
  - ❌ Pode ter limitações com câmera/assinatura

- [ ] **Flutter**
  - ✅ Cross-platform (Android + iOS)
  - ✅ UI moderna e bonita
  - ✅ Boa performance
  - ❌ Linguagem Dart (nova para aprender)

**Recomendação do AI**: Android Nativo (Kotlin + Jetpack Compose) - considerando que:
- Já existe estrutura Kotlin no projeto
- MVP focado em Android
- Precisa de recursos nativos (câmera, assinatura)

---

### 4. Design e UX do Aplicativo

**Você tem algum protótipo ou mockup?**

- [ ] **Sim** - Tenho designs prontos (Figma, XD, etc.)
  
- [ ] **Não** - Preciso que você sugira a arquitetura de telas

**Se não tiver, sugestão de telas para MVP**:

1. **Tela de Login**
2. **Dashboard** (Resumo com cards: Funcionários, EPIs, Entregas Vencendo)
3. **Lista de Funcionários** (com busca)
4. **Cadastro de Funcionário**
5. **Lista de EPIs** (com busca e estoque)
6. **Cadastro de EPI**
7. **Registrar Entrega** (seleciona funcionário, mostra EPIs recomendados, tira foto, assina)
8. **Entregas Vencendo** (lista com alerta)
9. **Histórico de Entregas** (por funcionário)

---

### 5. Funcionalidades Offline

**O app precisa funcionar offline?**

- [ ] **Sim** - Precisa salvar localmente e sincronizar depois
  - Requer Room Database no Android
  - Requer lógica de sincronização
  - Complexidade adicional

- [ ] **Não** - Apenas online (requer internet)
  - Mais simples
  - Suficiente para MVP se houver conexão garantida

**Recomendação do AI**: Apenas online para MVP (simplifica muito).

---

### 6. Controle de Permissões/Roles

**Vamos implementar diferentes níveis de acesso?**

- [ ] **Sim** - Administrador vs Operador
  - Admin: pode criar EPIs, funcionários
  - Operador: apenas registra entregas
  - Requer implementação de roles no backend

- [ ] **Não** - Todos usuários têm acesso total
  - Mais simples para MVP
  - Todos podem fazer tudo

**Recomendação do AI**: Não para MVP (deixar para depois).

---

## 🚀 Próximos Passos

### Fase 1: Ajustes no Backend (Prioridade Alta)

- [ ] Implementar endpoints de Cargos e Atividades (`GET /cargos`, `GET /atividades`)
- [ ] Decidir e implementar estratégia de upload (Base64 ou Multipart)
- [ ] Implementar endpoint de devolução (`PUT /entregas/{id}/devolver`)
- [ ] Implementar Exception Handler global para erros adequados
- [ ] (Opcional) Adicionar endpoints de busca/filtro

**Estimativa**: 1-2 dias de desenvolvimento

---

### Fase 2: Iniciar Frontend Android

Após ajustes no backend e decisões tomadas:

1. **Setup Inicial**
   - Configurar projeto Kotlin + Jetpack Compose
   - Configurar Retrofit para chamadas HTTP
   - Configurar Hilt para Dependency Injection

2. **Módulo de Autenticação**
   - Tela de Login
   - Gerenciamento de sessão (SharedPreferences ou DataStore)
   - Interceptor HTTP para adicionar Basic Auth

3. **Módulos Core**
   - Dashboard
   - CRUD de Funcionários
   - CRUD de EPIs
   - Registro de Entregas

4. **Funcionalidades Especiais**
   - Câmera para foto
   - Canvas para assinatura digital
   - Notificações de vencimento

**Estimativa**: 2-3 semanas de desenvolvimento

---

## 📝 Notas Finais

- Este documento deve ser usado como base para decisões de arquitetura
- Priorize funcionalidades essenciais para o MVP
- Documente decisões tomadas adicionando `[x]` nas opções escolhidas
- Mantenha este documento atualizado conforme o projeto evolui

---

**Próxima Ação**: Responder às questões acima para definir o caminho do desenvolvimento do frontend Android.
