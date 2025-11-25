package pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.vallegrande.vg_ms_assistance.domain.enums.AttendanceStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponse {
    private UUID id;
    private String studentId;
    private String studentName;
    private String classroomId;
    private String classroomName;
    private String institutionId;
    private String institutionName;
    private LocalDate attendanceDate;
    private Integer academicYear;
    private AttendanceStatus attendanceStatus;
    private LocalTime arrivalTime;
    private LocalTime departureTime;
    private Boolean justified;
    private String justificationReason;
    private String justificationDocumentUrl;
    private String registeredBy;
    private LocalDateTime registeredAt;
    private LocalDateTime updatedAt;
}
