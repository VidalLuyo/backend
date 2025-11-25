package pe.edu.vallegrande.vg_ms_assistance.infrastructure.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vg_ms_assistance.domain.enums.AttendanceStatus;
import pe.edu.vallegrande.vg_ms_assistance.domain.model.AttendanceRecord;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface AttendanceRepository extends R2dbcRepository<AttendanceRecord, UUID> {
    
    Flux<AttendanceRecord> findByStudentId(String studentId);
    
    Flux<AttendanceRecord> findByClassroomId(String classroomId);
    
    Flux<AttendanceRecord> findByInstitutionId(String institutionId);
    
    Flux<AttendanceRecord> findByAttendanceDate(LocalDate date);
    
    Flux<AttendanceRecord> findByStudentIdAndAttendanceDateBetween(
        String studentId, LocalDate startDate, LocalDate endDate);
    
    Flux<AttendanceRecord> findByClassroomIdAndAttendanceDate(
        String classroomId, LocalDate date);
    
    Flux<AttendanceRecord> findByInstitutionIdAndAttendanceDateBetween(
        String institutionId, LocalDate startDate, LocalDate endDate);
    
    Flux<AttendanceRecord> findByAttendanceStatus(AttendanceStatus status);
    
    @Query("SELECT COUNT(*) FROM attendance_records WHERE student_id = $1 AND attendance_date = $2")
    Mono<Long> countByStudentIdAndDate(String studentId, LocalDate date);
    
    @Query("SELECT COUNT(*) FROM attendance_records WHERE student_id = :studentId " +
           "AND attendance_date BETWEEN :startDate AND :endDate")
    Mono<Long> countByStudentIdAndDateRange(String studentId, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT COUNT(*) FROM attendance_records WHERE student_id = $1 " +
           "AND attendance_status = $2 AND attendance_date BETWEEN $3 AND $4")
    Mono<Long> countByStudentIdAndStatusAndDateRange(
        String studentId, AttendanceStatus status, LocalDate startDate, LocalDate endDate);
}
