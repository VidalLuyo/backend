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
public class InstitutionDTO {
    private String id;

    @JsonProperty("institutionId")
    private String institutionId;

    private String name;

    @JsonProperty("institutionName")
    private String institutionName;

    private String code;

    @JsonProperty("institutionInformation")
    private InstitutionInformation institutionInformation;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InstitutionInformation {
        private String institutionName;
        private String codeInstitution;
    }

    // Método helper para obtener el ID real
    public String getId() {
        if (id != null && !id.isEmpty()) {
            return id;
        }
        if (institutionId != null && !institutionId.isEmpty()) {
            return institutionId;
        }
        return null;
    }

    // Setter personalizado para id
    public void setId(String id) {
        this.id = id;
    }

    // Método helper para obtener el nombre de visualización
    public String getDisplayName() {
        // Prioridad 1: institutionInformation.institutionName
        if (institutionInformation != null && institutionInformation.getInstitutionName() != null
                && !institutionInformation.getInstitutionName().trim().isEmpty()) {
            return institutionInformation.getInstitutionName().trim();
        }

        // Prioridad 2: institutionName
        if (institutionName != null && !institutionName.trim().isEmpty()) {
            return institutionName.trim();
        }

        // Prioridad 3: name
        if (name != null && !name.trim().isEmpty()) {
            return name.trim();
        }

        // Prioridad 4: code
        if (code != null && !code.trim().isEmpty()) {
            return code.trim();
        }

        // Fallback: ID truncado
        String actualId = getId();
        return actualId != null ? "Institución " + actualId.substring(0, Math.min(8, actualId.length()))
                : "Desconocido";
    }
}
