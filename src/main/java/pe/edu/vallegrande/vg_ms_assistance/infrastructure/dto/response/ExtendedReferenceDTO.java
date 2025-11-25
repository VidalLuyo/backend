package pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtendedReferenceDTO {
    private String id;
    private String name;
    private String institutionId;
    private String classroomId;
}
