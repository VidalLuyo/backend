package pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClassroomDTO {
    private String id;
    
    @JsonProperty("classroomId")
    private String classroomId;
    
    private String name;
    
    @JsonProperty("classroomName")
    private String classroomName;
    
    private String code;
    private String description;
    
    @JsonProperty("institutionId")
    private String institutionId;
    
    @JsonProperty("institution")
    private InstitutionInfo institution;
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InstitutionInfo {
        private String id;
        private String institutionId;
        private String name;
        private String institutionName;
    }
    
    // Método helper para obtener el ID real
    public String getId() {
        if (id != null && !id.isEmpty()) {
            return id;
        }
        if (classroomId != null && !classroomId.isEmpty()) {
            return classroomId;
        }
        return null;
    }
    
    // Setter personalizado para id
    public void setId(String id) {
        this.id = id;
    }
    
    // Método helper para obtener el institutionId
    public String getInstitutionId() {
        if (institutionId != null && !institutionId.isEmpty()) {
            return institutionId;
        }
        if (institution != null) {
            if (institution.getInstitutionId() != null && !institution.getInstitutionId().isEmpty()) {
                return institution.getInstitutionId();
            }
            if (institution.getId() != null && !institution.getId().isEmpty()) {
                return institution.getId();
            }
        }
        return null;
    }
    
    // Método helper para obtener el nombre de visualización
    public String getDisplayName() {
        // Prioridad 1: classroomName
        if (classroomName != null && !classroomName.trim().isEmpty()) {
            return classroomName.trim();
        }
        
        // Prioridad 2: name
        if (name != null && !name.trim().isEmpty()) {
            return name.trim();
        }
        
        // Prioridad 3: code
        if (code != null && !code.trim().isEmpty()) {
            return "Aula " + code.trim();
        }
        
        // Fallback: ID truncado
        String actualId = getId();
        return actualId != null ? "Aula " + actualId.substring(0, Math.min(8, actualId.length())) : "Desconocido";
    }
}
