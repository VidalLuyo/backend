package pe.edu.vallegrande.vg_ms_assistance.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import pe.edu.vallegrande.vg_ms_assistance.domain.enums.AttendanceStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("attendance_records")
public class AttendanceRecord {
    
    @Id
    private UUID id;
    
    @Column("student_id")
    private String studentId;
    
    @Column("classroom_id")
    private String classroomId;
    
    @Column("institution_id")
    private String institutionId;
    
    @Column("attendance_date")
    private LocalDate attendanceDate;
    
    @Column("academic_year")
    private Integer academicYear;
    
    @Column("attendance_status")
    private AttendanceStatus attendanceStatus;
    
    @Column("arrival_time")
    private LocalTime arrivalTime;
    
    @Column("departure_time")
    private LocalTime departureTime;
    
    @Column("justified")
    private Boolean justified;
    
    @Column("justification_reason")
    private String justificationReason;
    
    @Column("justification_document_url")
    private String justificationDocumentUrl;
    
    @Column("registered_by")
    private String registeredBy;
    
    @Column("registered_at")
    private LocalDateTime registeredAt;
    
    @Column("updated_at")
    private LocalDateTime updatedAt;
}
