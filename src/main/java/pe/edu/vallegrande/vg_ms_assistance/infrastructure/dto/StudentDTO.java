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
public class StudentDTO {
    private String id;
    
    @JsonProperty("studentId")
    private String studentId;
    
    private String name;
    private String firstName;
    private String lastName;
    private String fullName;
    private String status;
    
    @JsonProperty("institutionId")
    private String institutionId;
    
    @JsonProperty("classroomId")
    private String classroomId;
    
    @JsonProperty("personalInfo")
    private PersonalInfo personalInfo;
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PersonalInfo {
        private String names;
        
        @JsonProperty("lastNames")
        private String lastNames;
        
        private String lastnames; // Fallback por si viene en minúsculas
        
        public String getLastNames() {
            return lastNames != null ? lastNames : lastnames;
        }
    }
    
    // Método helper para obtener el ID real
    public String getId() {
        // Priorizar studentId sobre id
        if (studentId != null && !studentId.isEmpty()) {
            return studentId;
        }
        if (id != null && !id.isEmpty()) {
            return id;
        }
        return null;
    }
    
    // Setter personalizado para id
    public void setId(String id) {
        this.id = id;
        // Si no hay studentId, usar el id también como studentId
        if (this.studentId == null || this.studentId.isEmpty()) {
            this.studentId = id;
        }
    }
    
    // Método helper para obtener el institutionId
    public String getInstitutionId() {
        return institutionId;
    }
    
    // Método helper para obtener el classroomId
    public String getClassroomId() {
        return classroomId;
    }
    
    // Método helper para obtener el nombre completo
    public String getDisplayName() {
        // Prioridad 1: personalInfo (estructura del microservicio de estudiantes)
        if (personalInfo != null) {
            String names = personalInfo.getNames();
            String lastNames = personalInfo.getLastNames();
            if (names != null && !names.trim().isEmpty() && lastNames != null && !lastNames.trim().isEmpty()) {
                return names.trim() + " " + lastNames.trim();
            }
            if (names != null && !names.trim().isEmpty()) {
                return names.trim();
            }
            if (lastNames != null && !lastNames.trim().isEmpty()) {
                return lastNames.trim();
            }
        }
        
        // Prioridad 2: fullName
        if (fullName != null && !fullName.trim().isEmpty()) {
            return fullName.trim();
        }
        
        // Prioridad 3: name
        if (name != null && !name.trim().isEmpty()) {
            return name.trim();
        }
        
        // Prioridad 4: firstName + lastName
        if (firstName != null && !firstName.trim().isEmpty() && lastName != null && !lastName.trim().isEmpty()) {
            return firstName.trim() + " " + lastName.trim();
        }
        if (firstName != null && !firstName.trim().isEmpty()) {
            return firstName.trim();
        }
        
        // Fallback: ID truncado
        String actualId = getId();
        return actualId != null ? "Estudiante " + actualId.substring(0, Math.min(8, actualId.length())) : "Desconocido";
    }
}
