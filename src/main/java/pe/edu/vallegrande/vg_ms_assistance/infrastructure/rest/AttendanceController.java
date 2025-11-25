package pe.edu.vallegrande.vg_ms_assistance.infrastructure.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.vg_ms_assistance.application.service.AttendanceService;
import pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.request.AttendanceRequest;
import pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.request.JustificationRequest;
import pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.response.AttendanceResponse;
import pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.response.AttendanceStatsResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance", description = "Attendance management endpoints")
public class AttendanceController {
    
    private final AttendanceService attendanceService;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create attendance record")
    public Mono<AttendanceResponse> createAttendance(@Valid @RequestBody AttendanceRequest request) {
        return attendanceService.createAttendance(request);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get attendance by ID")
    public Mono<AttendanceResponse> getAttendanceById(@PathVariable UUID id) {
        return attendanceService.getAttendanceById(id);
    }
    
    @GetMapping
    @Operation(summary = "Get all attendance records")
    public Flux<AttendanceResponse> getAllAttendances() {
        return attendanceService.getAllAttendances();
    }
    
    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get attendance by student")
    public Flux<AttendanceResponse> getAttendancesByStudent(@PathVariable String studentId) {
        return attendanceService.getAttendancesByStudent(studentId);
    }
    
    @GetMapping("/classroom/{classroomId}")
    @Operation(summary = "Get attendance by classroom")
    public Flux<AttendanceResponse> getAttendancesByClassroom(@PathVariable String classroomId) {
        return attendanceService.getAttendancesByClassroom(classroomId);
    }
    
    @GetMapping("/institution/{institutionId}")
    @Operation(summary = "Get attendance by institution")
    public Flux<AttendanceResponse> getAttendancesByInstitution(@PathVariable String institutionId) {
        return attendanceService.getAttendancesByInstitution(institutionId);
    }
    
    @GetMapping("/date/{date}")
    @Operation(summary = "Get attendance by date")
    public Flux<AttendanceResponse> getAttendancesByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return attendanceService.getAttendancesByDate(date);
    }
    
    @GetMapping("/classroom/{classroomId}/date/{date}")
    @Operation(summary = "Get attendance by classroom and date")
    public Flux<AttendanceResponse> getAttendancesByClassroomAndDate(
            @PathVariable String classroomId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return attendanceService.getAttendancesByClassroomAndDate(classroomId, date);
    }
    
    @GetMapping("/student/{studentId}/range")
    @Operation(summary = "Get attendance by student and date range")
    public Flux<AttendanceResponse> getAttendancesByStudentAndDateRange(
            @PathVariable String studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return attendanceService.getAttendancesByStudentAndDateRange(studentId, startDate, endDate);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update attendance record (only departure time and status)")
    public Mono<AttendanceResponse> updateAttendance(
            @PathVariable UUID id,
            @Valid @RequestBody pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.request.UpdateAttendanceRequest request) {
        return attendanceService.updateAttendance(id, request);
    }
    
    @PatchMapping("/{id}/justify")
    @Operation(summary = "Justify attendance")
    public Mono<AttendanceResponse> justifyAttendance(
            @PathVariable UUID id,
            @Valid @RequestBody JustificationRequest request) {
        return attendanceService.justifyAttendance(id, request);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete attendance record")
    public Mono<Void> deleteAttendance(@PathVariable UUID id) {
        return attendanceService.deleteAttendance(id);
    }
    
    @GetMapping("/student/{studentId}/stats")
    @Operation(summary = "Get attendance statistics for student")
    public Mono<AttendanceStatsResponse> getAttendanceStats(
            @PathVariable String studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return attendanceService.getAttendanceStats(studentId, startDate, endDate);
    }
    
    @GetMapping("/reference/students")
    @Operation(summary = "Get all students for reference")
    public Flux<pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.response.ExtendedReferenceDTO> getAllStudents() {
        return attendanceService.getAllStudents();
    }
    
    @GetMapping("/reference/classrooms")
    @Operation(summary = "Get all classrooms for reference")
    public Flux<pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.response.ExtendedReferenceDTO> getAllClassrooms() {
        return attendanceService.getAllClassrooms();
    }
    
    @GetMapping("/reference/institutions")
    @Operation(summary = "Get all institutions for reference")
    public Flux<pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.response.ReferenceDTO> getAllInstitutions() {
        return attendanceService.getAllInstitutions();
    }
    
    @PostMapping("/bulk")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create bulk attendance records for multiple students")
    public Mono<pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.response.BulkAttendanceResponse> createBulkAttendance(
            @Valid @RequestBody pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.request.BulkAttendanceRequest request) {
        return attendanceService.createBulkAttendance(request);
    }
    
    @GetMapping("/reference/students/institution/{institutionId}")
    @Operation(summary = "Get students by institution for reference")
    public Flux<pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.response.ExtendedReferenceDTO> getStudentsByInstitution(
            @PathVariable String institutionId) {
        return attendanceService.getStudentsByInstitution(institutionId);
    }
    
    @GetMapping("/reference/classrooms/institution/{institutionId}")
    @Operation(summary = "Get classrooms by institution for reference")
    public Flux<pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.response.ExtendedReferenceDTO> getClassroomsByInstitution(
            @PathVariable String institutionId) {
        return attendanceService.getClassroomsByInstitution(institutionId);
    }
}
