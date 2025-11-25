package pe.edu.vallegrande.vg_ms_assistance.infrastructure.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/files")
@Tag(name = "File Upload", description = "File upload endpoints")
public class FileUploadController {
    
    private static final String UPLOAD_DIR = "uploads/justifications/";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_EXTENSIONS = {".pdf", ".jpg", ".jpeg", ".png", ".doc", ".docx"};
    
    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Upload justification document")
    public Mono<Map<String, String>> uploadFile(@RequestPart("file") Mono<FilePart> filePartMono) {
        return filePartMono.flatMap(filePart -> {
            String originalFilename = filePart.filename();
            
            // Validar extensión
            if (!isValidExtension(originalFilename)) {
                return Mono.error(new IllegalArgumentException(
                    "Tipo de archivo no permitido. Solo se permiten: PDF, JPG, PNG, DOC, DOCX"));
            }
            
            // Generar nombre único
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String uniqueId = UUID.randomUUID().toString().substring(0, 8);
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = timestamp + "_" + uniqueId + extension;
            
            // Crear directorio si no existe
            Path uploadPath = Paths.get(UPLOAD_DIR);
            uploadPath.toFile().mkdirs();
            
            // Guardar archivo
            Path filePath = uploadPath.resolve(newFilename);
            
            return filePart.transferTo(filePath)
                .then(Mono.fromCallable(() -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("filename", newFilename);
                    response.put("url", "/uploads/justifications/" + newFilename);
                    response.put("originalName", originalFilename);
                    log.info("File uploaded successfully: {}", newFilename);
                    return response;
                }));
        });
    }
    
    private boolean isValidExtension(String filename) {
        String lowerFilename = filename.toLowerCase();
        for (String ext : ALLOWED_EXTENSIONS) {
            if (lowerFilename.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }
}
