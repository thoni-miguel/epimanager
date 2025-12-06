package com.thoni.epimanager.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;

@Service
public class StorageService {

    private static final String UPLOAD_DIR = "uploads/";
    private static final String FOTOS_DIR = UPLOAD_DIR + "fotos/";
    private static final String ASSINATURAS_DIR = UPLOAD_DIR + "assinaturas/";

    public StorageService() {
        // Criar diretórios se não existirem
        try {
            Files.createDirectories(Paths.get(FOTOS_DIR));
            Files.createDirectories(Paths.get(ASSINATURAS_DIR));
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível criar diretórios de upload", e);
        }
    }

    /**
     * Salva uma imagem em Base64 no sistema de arquivos
     * 
     * @param base64Image String no formato "data:image/jpeg;base64,/9j/4AAQ..." ou
     *                    apenas o Base64
     * @param tipo        "foto" ou "assinatura"
     * @return Path relativo ao arquivo salvo (ex: "uploads/fotos/abc123.jpg")
     */
    public String saveBase64Image(String base64Image, String tipo) {
        if (base64Image == null || base64Image.isEmpty()) {
            return null;
        }

        try {
            // Extrair dados do Base64 (remover data:image/...;base64, se presente)
            String base64Data = base64Image;
            String extension = "jpg"; // padrão

            if (base64Image.contains(",")) {
                String[] parts = base64Image.split(",");
                String header = parts[0]; // data:image/jpeg;base64
                base64Data = parts[1]; // dados Base64

                // Extrair extensão do header
                if (header.contains("image/png")) {
                    extension = "png";
                } else if (header.contains("image/jpeg") || header.contains("image/jpg")) {
                    extension = "jpg";
                }
            }

            // Decodificar Base64
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);

            // Gerar nome único
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String uniqueId = UUID.randomUUID().toString().substring(0, 8);
            String filename = timestamp + "_" + uniqueId + "." + extension;

            // Definir diretório baseado no tipo
            String dir = tipo.equalsIgnoreCase("foto") ? FOTOS_DIR : ASSINATURAS_DIR;
            Path filepath = Paths.get(dir + filename);

            // Salvar arquivo
            Files.write(filepath, imageBytes);

            // Retornar path relativo
            return dir + filename;

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Base64 inválido", e);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar arquivo", e);
        }
    }

    /**
     * Deleta um arquivo do sistema
     * 
     * @param filepath Path do arquivo a ser deletado
     */
    public void deleteFile(String filepath) {
        if (filepath == null || filepath.isEmpty()) {
            return;
        }

        try {
            Path path = Paths.get(filepath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // Log error mas não falha - arquivo pode já ter sido deletado
            System.err.println("Erro ao deletar arquivo: " + filepath);
        }
    }
}
