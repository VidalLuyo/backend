package pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.vallegrande.vg_ms_assistance.domain.enums.AttendanceStatus;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAttendanceRequest {
    
    @NotNull(message = "Attendance status is required")
    private AttendanceStatus attendanceStatus;
    
    // Solo se puede modificar la hora de salida al editar
    private LocalTime departureTime;
    
    private Boolean justified;
    private String justificationReason;
    private String justificationDocumentUrl;
}
