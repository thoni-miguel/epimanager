# Resumo - Upload Base64 de Fotos e Assinaturas

**Data**: 06 de Dezembro de 2025  
**Status**: ✅ Implementado e Compilado

---

## ✅ O que foi criado

### 1. **StorageService** (NOVO)

Serviço para processar imagens Base64 e salvar em arquivos.

**Métodos principais:**
- `saveBase64Image(String base64Image, String tipo)` - Decodifica Base64 e salva arquivo
- `deleteFile(String filepath)` - Remove arquivo do sistema

**Diretórios criados automaticamente:**
- `uploads/fotos/` - Fotos de entregas
- `uploads/assinaturas/` - Assinaturas digitais

**Funcionalidades:**
- ✅ Aceita Base64 com ou sem header (`data:image/jpeg;base64,...`)
- ✅ Detecta tipo de imagem automaticamente (JPG/PNG)
- ✅ Gera nome único: `yyyyMMdd_HHmmss_uuid.ext`
- ✅ Retorna path relativo para salvar no banco

---

### 2. **EntregaRequest** (Modificado)

**Antes:**
```java
String fotoPath
String assinaturaPath
```

**Depois:**
```java
String fotoBase64       // Base64 da foto (opcional)
String assinaturaBase64 // Base64 da assinatura (opcional)
```

---

### 3. **EntregaService** (Modificado)

**Novo fluxo no método `registrarEntrega`:**

```java
// 1. Valida funcionário e EPI
// 2. Decrementa estoque
// 3. Processa foto Base64 → salva arquivo → pega path
// 4. Processa assinatura Base64 → salva arquivo → pega path
// 5. Salva entrega com paths dos arquivos
```

**Código adicionado:**
```java
if (fotoBase64 != null && !fotoBase64.isEmpty()) {
    String fotoPath = storageService.saveBase64Image(fotoBase64, "foto");
    entrega.setFotoPath(fotoPath);
}

if (assinaturaBase64 != null && !assinaturaBase64.isEmpty()) {
    String assinaturaPath = storageService.saveBase64Image(assinaturaBase64, "assinatura");
    entrega.setAssinaturaPath(assinaturaPath);
}
```

---

### 4. **EntregaController** (Modificado)

Atualizado para usar novos campos `fotoBase64` e `assinaturaBase64`.

---

## 🎯 Fluxo no Android

### Registrar Entrega com Foto e Assinatura

```kotlin
// 1. Usuário tira foto
val photoBitmap = camera.takePicture()
val photoBase64 = bitmapToBase64(photoBitmap)
// photoBase64 = "data:image/jpeg;base64,/9j/4AAQSkZJRg..."

// 2. Usuário assina
val signatureBitmap = signatureCanvas.getBitmap()
val signatureBase64 = bitmapToBase64(signatureBitmap)
// signatureBase64 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUg..."

// 3. Criar requisição
val entregaRequest = EntregaRequest(
    funcionarioId = 1,
    epiId = 5,
    fotoBase64 = photoBase64,
    assinaturaBase64 = signatureBase64
)

// 4. Enviar para backend
apiService.createEntrega(entregaRequest)

// Backend decodifica Base64, salva arquivos e responde:
{
  "id": 1,
  "funcionario": {...},
  "epi": {...},
  "dataEntrega": "2025-12-06",
  "dataLimiteTroca": "2026-06-04",
  "fotoPath": "uploads/fotos/20251206_134500_abc12345.jpg",
  "assinaturaPath": "uploads/assinaturas/20251206_134500_xyz98765.png"
}
```

---

## 📝 Exemplo de Requisição

### POST `/entregas`

**Request Body (JSON):**
```json
{
  "funcionarioId": 1,
  "epiId": 5,
  "fotoBase64": "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBD...",
  "assinaturaBase64": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAA..."
}
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
    "ca": "00000"
  },
  "dataEntrega": "2025-12-06",
  "dataLimiteTroca": "2026-06-04",
  "dataDevolucao": null,
  "fotoPath": "uploads/fotos/20251206_134706_a1b2c3d4.jpg",
  "assinaturaPath": "uploads/assinaturas/20251206_134706_e5f6g7h8.png"
}
```

---

## 🔍 Detalhes Técnicos

### Formato Base64 Aceito

**Opção 1 - Com header (Recomendado):**
```
data:image/jpeg;base64,/9j/4AAQSkZJRg...
data:image/png;base64,iVBORw0KGgo...
```

**Opção 2 - Apenas Base64:**
```
/9j/4AAQSkZJRg...
iVBORw0KGgo...
```

### Nomenclatura de Arquivos

Padrão: `{timestamp}_{uuid}.{ext}`

Exemplo: `20251206_134706_a1b2c3d4.jpg`

---

## ✅ Vantagens da Abordagem Base64

### Para MVP:
- ✅ **Uma única requisição** - Tudo em um POST
- ✅ **Simples no Android** - Não precisa Multipart
- ✅ **JSON puro** - Fácil de debugar
- ✅ **Swagger funciona** - Pode testar direto na UI

### Limitações (para futuro):
- ⚠️ Payload maior (Base64 = +33% de tamanho)
- ⚠️ Sem validação de tamanho antes de decodificar
- ⚠️ Toda imagem precisa estar na memória

**Solução futura**: Migrar para Multipart se imagens > 1MB

---

## 🧪 Teste Manual (cURL)

### Criar entrega com imagens pequenas

```bash
curl -u admin:admin123 -X POST http://localhost:8080/entregas \
  -H "Content-Type: application/json" \
  -d '{
    "funcionarioId": 1,
    "epiId": 5,
    "fotoBase64": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==",
    "assinaturaBase64": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg=="
  }'
```

**Nota**: O Base64 acima é uma imagem 1x1px vermelha (apenas para teste)

---

## 📂 Estrutura de Arquivos

Após algumas entregas, a estrutura fica assim:

```
epimanager/
├── uploads/
│   ├── fotos/
│   │   ├── 20251206_134706_a1b2c3d4.jpg
│   │   ├── 20251206_135012_b2c3d4e5.jpg
│   │   └── 20251206_140230_c3d4e5f6.jpg
│   └── assinaturas/
│       ├── 20251206_134706_e5f6g7h8.png
│       ├── 20251206_135012_f6g7h8i9.png
│       └── 20251206_140230_g7h8i9j0.png
└── src/
    └── ...
```

---

## 🔧 Configuração Necessária no Android

### Converter Bitmap para Base64

```kotlin
fun bitmapToBase64(bitmap: Bitmap): String {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
    val byteArray = outputStream.toByteArray()
    val base64String = Base64.encodeToString(byteArray, Base64.NO_WRAP)
    return "data:image/jpeg;base64,$base64String"
}
```

---

**Status**: ✅ Pronto para usar no Android  
**Próximo**: Endpoint de Devolução (Item 3)
