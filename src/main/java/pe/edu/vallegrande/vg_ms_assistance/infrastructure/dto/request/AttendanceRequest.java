package pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.vallegrande.vg_ms_assistance.domain.enums.AttendanceStatus;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRequest {
    
    @NotBlank(message = "Student ID is required")
    private String studentId;
    
    @NotBlank(message = "Classroom ID is required")
    private String classroomId;
    
    @NotBlank(message = "Institution ID is required")
    private String institutionId;
    
    @NotNull(message = "Attendance date is required")
    private LocalDate attendanceDate;
    
    @NotNull(message = "Academic year is required")
    private Integer academicYear;
    
    @NotNull(message = "Attendance status is required")
    private AttendanceStatus attendanceStatus;
    
    private LocalTime arrivalTime;
    private LocalTime departureTime;
    private Boolean justified;
    private String justificationReason;
    private String justificationDocumentUrl;
    
    @NotBlank(message = "Registered by is required")
    private String registeredBy;
}
