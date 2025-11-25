package pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JustificationRequest {
    
    @NotBlank(message = "Justification reason is required")
    private String justificationReason;
    
    private String justificationDocumentUrl;
}
