package pe.edu.vallegrande.vg_ms_assistance.application.service;

import pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.request.AttendanceRequest;
import pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.request.JustificationRequest;
import pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.response.AttendanceResponse;
import pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.response.AttendanceStatsResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

public interface AttendanceService {
    
    Mono<AttendanceResponse> createAttendance(AttendanceRequest request);
    
    Mono<AttendanceResponse> getAttendanceById(UUID id);
    
    Flux<AttendanceResponse> getAllAttendances();
    
    Flux<AttendanceResponse> getAttendancesByStudent(String studentId);
    
    Flux<AttendanceResponse> getAttendancesByClassroom(String classroomId);
    
    Flux<AttendanceResponse> getAttendancesByInstitution(String institutionId);
    
    Flux<AttendanceResponse> getAttendancesByDate(LocalDate date);
    
    Flux<AttendanceResponse> getAttendancesByClassroomAndDate(String classroomId, LocalDate date);
    
    Flux<AttendanceResponse> getAttendancesByStudentAndDateRange(
            String studentId, LocalDate startDate, LocalDate endDate);
    
    Mono<AttendanceResponse> updateAttendance(UUID id, pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.request.UpdateAttendanceRequest request);
    
    Mono<AttendanceResponse> justifyAttendance(UUID id, JustificationRequest request);
    
    Mono<Void> deleteAttendance(UUID id);
    
    Mono<AttendanceStatsResponse> getAttendanceStats(
            String studentId, LocalDate startDate, LocalDate endDate);
    
    Flux<pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.response.ExtendedReferenceDTO> getAllStudents();
    
    Flux<pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.response.ExtendedReferenceDTO> getAllClassrooms();
    
    Flux<pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.response.ReferenceDTO> getAllInstitutions();
    
    Mono<pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.response.BulkAttendanceResponse> createBulkAttendance(
            pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.request.BulkAttendanceRequest request);
    
    Flux<pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.response.ExtendedReferenceDTO> getStudentsByInstitution(String institutionId);
    
    Flux<pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.response.ExtendedReferenceDTO> getClassroomsByInstitution(String institutionId);
}
