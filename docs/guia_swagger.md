# Guia de Acesso ao Swagger UI

**Data**: 30 de Novembro de 2025  
**Implementação**: OpenAPI 3.0 (springdoc-openapi v2.3.0)

---

## 🚀 Como Acessar

### 1. Iniciar Aplicação
```bash
./mvnw spring-boot:run
```

### 2. Abrir Swagger UI no Navegador
```
http://localhost:8080/swagger-ui.html
```

ou

```
http://localhost:8080/swagger-ui/index.html
```

### 3. OpenAPI JSON (para importar em outras ferramentas)
```
http://localhost:8080/v3/api-docs
```

---

## 🎯 O Que Você Verá

### Interface Swagger UI

A interface está organizada em **4 grupos** (tags):

#### 🔐 **Autenticação** (Pública)
- `POST /auth/register` - Registrar novo usuário
- `POST /auth/login` - Fazer login

#### 👤 **Funcionários** (Requer autenticação)
- `GET /funcionarios` - Listar todos
- `POST /funcionarios` - Criar funcionário
- `GET /funcionarios/{id}` - Buscar por ID

#### 🛡️ **EPIs** (Requer autenticação)
- `GET /epis` - Listar todos EPIs
- `GET /epis/recomendados` - EPIs recomendados por cargo
- `POST /epis` - Criar novo EPI

#### 📦 **Entregas** (Requer autenticação)
- `POST /entregas` - Registrar entrega
- `GET /entregas/vencendo` - Listar vencimentos próximos

---

## 🔑 Como Testar Endpoints Protegidos no Swagger

### Passo 1: Registrar Usuário
1. Clique em **"Autenticação" → "POST /auth/register"**
2. Clique em **"Try it out"**
3. Cole o JSON:
   ```json
   {
     "username": "admin",
     "password": "admin123"
   }
   ```
4. Clique em **"Execute"**
5. Deve retornar **201 Created**

### Passo 2: Autenticar no Swagger UI
1. Clique no botão **"Authorize"** (cadeado no topo da página)
2. Preencha:
   - **Username**: `admin`
   - **Password**: `admin123`
3. Clique em **"Authorize"**
4. Clique em **"Close"**

### Passo 3: Testar Endpoints Protegidos
Agora todos os endpoints protegidos funcionarão automaticamente com sua autenticação!

**Exemplo**: Listar EPIs
1. Clique em **"EPIs" → "GET /epis"**
2. Clique em **"Try it out"**
3. Clique em **"Execute"**
4. Deve retornar **200 OK** com lista de EPIs

---

## 📋 Recursos do Swagger Implementados

### Metadata da API
- **Título**: EPI Manager API
- **Versão**: v1.0.0
- **Descrição**: API REST para gerenciamento de EPIs
- **Contato**: Thoni Miguel (thoni@epimanager.com)
- **Licença**: MIT License

### Esquema de Segurança
- **Tipo**: HTTP Basic Authentication
- **Nome**: `basicAuth`
- **Descrição**: Autenticação via HTTP Basic Auth (username:password)

### Annotations Usadas

#### Controllers
```java
@Tag(name = "EPIs", description = "Gerenciamento de Equipamentos...")
@SecurityRequirement(name = "basicAuth")
```

#### Endpoints
```java
@Operation(
  summary = "Listar todos EPIs", 
  description = "Retorna lista completa de EPIs cadastrados"
)
```

#### Parâmetros
```java
@Parameter(description = "Número de dias à frente", example = "7")
@RequestParam(defaultValue = "7") int dias
```

---

## 🧪 Fluxo de Teste Completo no Swagger

### 1. Criar Usuário
```
POST /auth/register
Body: {"username": "admin", "password": "admin123"}
```

### 2. Autenticar
- Clicar em **"Authorize"**
- Username: `admin`
- Password: `admin123`

### 3. Testar CRUD Completo

**a) Listar EPIs**
```
GET /epis
→ Retorna EPIs do seed
```

**b) Criar Funcionário**
```
POST /funcionarios
Body: {
  "nome": "João da Silva",
  "cpf": "123.456.789-00",
  "cargo": {"id": 1}
}
```

**c) Registrar Entrega**
```
POST /entregas
Body: {
  "funcionarioId": 1,
  "epiId": 1,
  "fotoPath": "/storage/foto.jpg",
  "assinaturaPath": "/storage/assinatura.png"
}
```

**d) Listar Vencimentos**
```
GET /entregas/vencendo?dias=30
→ Retorna entregas que vencem nos próximos 30 dias
```

---

## 🎨 Benefícios do Swagger UI

✅ **Documentação Automática**: Sempre atualizada com o código  
✅ **Teste Interativo**: Executa chamadas reais à API  
✅ **Schemas JSON**: Mostra estrutura de request/response  
✅ **Autenticação Integrada**: Botão "Authorize" gerencia credenciais  
✅ **Exportação**: Pode exportar OpenAPI spec para outras ferramentas

---

## 📤 Exportar para Postman

1. Acesse: `http://localhost:8080/v3/api-docs`
2. Copie o JSON completo
3. No Postman: **Import → Raw Text → Cole o JSON**
4. Postman criará automaticamente todos os endpoints!

---

## ⚙️ Configuração Implementada

### `OpenApiConfig.java`
```java
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI epiManagerOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("EPI Manager API")
                .version("v1.0.0")
                ...
            )
            .addSecurityItem(new SecurityRequirement()
                .addList("basicAuth"))
            .components(new Components()
                .addSecuritySchemes("basicAuth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("basic")));
    }
}
```

### `SecurityConfig.java`
```java
.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
```

### `pom.xml`
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

---

## 🐛 Troubleshooting

**Erro: "404 Not Found" ao acessar Swagger**
→ Verifique se a aplicação está rodando (`./mvnw spring-boot:run`)

**Erro: "401 Unauthorized" em endpoints protegidos**
→ Clique em "Authorize" e configure username/password

**Swagger não mostra meus endpoints**
→ Verifique se os controllers têm `@RestController` e `@RequestMapping`

**Schemas não aparecem**
→ Adicione annotations `@Schema` nas entidades (opcional, mas melhora docs)

---

## 📚 Referências

- **SpringDoc**: https://springdoc.org/
- **OpenAPI 3.0**: https://swagger.io/specification/
- **Swagger Editor**: https://editor.swagger.io/ (para validar spec)

---

**Status**: ✅ Implementado e Testado  
**Action Plan**: Dia 3 Concluído  
**Próximo**: Validação final e início do Android
