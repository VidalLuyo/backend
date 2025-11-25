package pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.vallegrande.vg_ms_assistance.domain.enums.AttendanceStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkAttendanceRequest {
    
    @NotEmpty(message = "La lista de estudiantes no puede estar vacía")
    private List<String> studentIds;
    
    @NotNull(message = "El ID del aula es requerido")
    private String classroomId;
    
    @NotNull(message = "El ID de la institución es requerido")
    private String institutionId;
    
    @NotNull(message = "La fecha de asistencia es requerida")
    private LocalDate attendanceDate;
    
    @NotNull(message = "El año académico es requerido")
    private Integer academicYear;
    
    @NotNull(message = "El estado de asistencia es requerido")
    private AttendanceStatus attendanceStatus;
    
    @NotNull(message = "La hora de llegada es requerida")
    private LocalTime arrivalTime;
    
    @NotNull(message = "El usuario que registra es requerido")
    private String registeredBy;
}
