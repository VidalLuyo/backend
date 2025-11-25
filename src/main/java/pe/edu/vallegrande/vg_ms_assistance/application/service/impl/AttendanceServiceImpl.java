package pe.edu.vallegrande.vg_ms_assistance.application.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.vallegrande.vg_ms_assistance.infrastructure.client.InstitutionClient;
import pe.edu.vallegrande.vg_ms_assistance.infrastructure.client.StudentClient;
import pe.edu.vallegrande.vg_ms_assistance.application.service.AttendanceService;
import pe.edu.vallegrande.vg_ms_assistance.domain.enums.AttendanceStatus;
import pe.edu.vallegrande.vg_ms_assistance.domain.model.AttendanceRecord;
import pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.request.AttendanceRequest;
import pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.request.JustificationRequest;
import pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.response.AttendanceResponse;
import pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.response.AttendanceStatsResponse;
import pe.edu.vallegrande.vg_ms_assistance.infrastructure.repository.AttendanceRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentClient studentClient;
    private final InstitutionClient institutionClient;

    @Override
    @Transactional
    public Mono<AttendanceResponse> createAttendance(AttendanceRequest request) {
        return attendanceRepository.countByStudentIdAndDate(
                request.getStudentId(), request.getAttendanceDate())
                .flatMap(count -> {
                    if (count > 0) {
                        return Mono.error(new IllegalArgumentException(
                                "Ya existe un registro de asistencia para este estudiante en esta fecha"));
                    }
                    
                    // Al crear, no se debe permitir hora de salida
                    if (request.getDepartureTime() != null) {
                        return Mono.error(new IllegalArgumentException(
                                "No se puede registrar hora de salida al crear la asistencia"));
                    }

                    AttendanceRecord record = mapToEntity(request);
                    record.setRegisteredAt(LocalDateTime.now());
                    record.setUpdatedAt(LocalDateTime.now());

                    return attendanceRepository.save(record)
                            .map(this::mapToResponse);
                });
    }

    @Override
    public Mono<AttendanceResponse> getAttendanceById(UUID id) {
        return attendanceRepository.findById(id)
                .flatMap(this::enrichAttendanceResponse)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Attendance record not found")));
    }

    @Override
    public Flux<AttendanceResponse> getAllAttendances() {
        return attendanceRepository.findAll()
                .flatMap(this::enrichAttendanceResponse);
    }

    @Override
    public Flux<AttendanceResponse> getAttendancesByStudent(String studentId) {
        return attendanceRepository.findByStudentId(studentId)
                .flatMap(this::enrichAttendanceResponse);
    }

    @Override
    public Flux<AttendanceResponse> getAttendancesByClassroom(String classroomId) {
        return attendanceRepository.findByClassroomId(classroomId)
                .flatMap(this::enrichAttendanceResponse);
    }

    @Override
    public Flux<AttendanceResponse> getAttendancesByInstitution(String institutionId) {
        return attendanceRepository.findByInstitutionId(institutionId)
                .flatMap(this::enrichAttendanceResponse);
    }

    @Override
    public Flux<AttendanceResponse> getAttendancesByDate(LocalDate date) {
        return attendanceRepository.findByAttendanceDate(date)
                .flatMap(this::enrichAttendanceResponse);
    }

    @Override
    public Flux<AttendanceResponse> getAttendancesByClassroomAndDate(String classroomId, LocalDate date) {
        return attendanceRepository.findByClassroomIdAndAttendanceDate(classroomId, date)
                .flatMap(this::enrichAttendanceResponse);
    }

    @Override
    public Flux<AttendanceResponse> getAttendancesByStudentAndDateRange(
            String studentId, LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findByStudentIdAndAttendanceDateBetween(
                studentId, startDate, endDate)
                .flatMap(this::enrichAttendanceResponse);
    }

    @Override
    @Transactional
    public Mono<AttendanceResponse> updateAttendance(UUID id, pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.request.UpdateAttendanceRequest request) {
        return attendanceRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Attendance record not found")))
                .flatMap(existing -> {
                    // Solo se puede modificar el estado, hora de salida y justificación
                    existing.setAttendanceStatus(request.getAttendanceStatus());
                    existing.setDepartureTime(request.getDepartureTime());
                    existing.setJustified(request.getJustified() != null ? request.getJustified() : existing.getJustified());
                    existing.setJustificationReason(request.getJustificationReason());
                    existing.setJustificationDocumentUrl(request.getJustificationDocumentUrl());
                    existing.setUpdatedAt(LocalDateTime.now());
                    
                    // Validar que la hora de salida sea después de la hora de entrada
                    if (existing.getArrivalTime() != null && request.getDepartureTime() != null) {
                        if (request.getDepartureTime().isBefore(existing.getArrivalTime())) {
                            return Mono.error(new IllegalArgumentException(
                                "La hora de salida debe ser posterior a la hora de entrada"));
                        }
                    }

                    return attendanceRepository.save(existing)
                            .map(this::mapToResponse);
                });
    }

    @Override
    @Transactional
    public Mono<AttendanceResponse> justifyAttendance(UUID id, JustificationRequest request) {
        return attendanceRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Attendance record not found")))
                .flatMap(existing -> {
                    existing.setJustified(true);
                    existing.setJustificationReason(request.getJustificationReason());
                    existing.setJustificationDocumentUrl(request.getJustificationDocumentUrl());

                    if (existing.getAttendanceStatus() == AttendanceStatus.AUSENTE) {
                        existing.setAttendanceStatus(AttendanceStatus.JUSTIFICADO);
                    }

                    existing.setUpdatedAt(LocalDateTime.now());

                    return attendanceRepository.save(existing)
                            .map(this::mapToResponse);
                });
    }

    @Override
    @Transactional
    public Mono<Void> deleteAttendance(UUID id) {
        return attendanceRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Attendance record not found")))
                .flatMap(attendanceRepository::delete);
    }

    @Override
    public Mono<AttendanceStatsResponse> getAttendanceStats(
            String studentId, LocalDate startDate, LocalDate endDate) {

        return Mono.zip(
                attendanceRepository.countByStudentIdAndDateRange(studentId, startDate, endDate),
                attendanceRepository.countByStudentIdAndStatusAndDateRange(
                        studentId, AttendanceStatus.PRESENTE, startDate, endDate),
                attendanceRepository.countByStudentIdAndStatusAndDateRange(
                        studentId, AttendanceStatus.AUSENTE, startDate, endDate),
                attendanceRepository.countByStudentIdAndStatusAndDateRange(
                        studentId, AttendanceStatus.TARDANZA, startDate, endDate),
                attendanceRepository.countByStudentIdAndStatusAndDateRange(
                        studentId, AttendanceStatus.JUSTIFICADO, startDate, endDate),
                attendanceRepository.countByStudentIdAndStatusAndDateRange(
                        studentId, AttendanceStatus.PERMISO, startDate, endDate))
                .map(tuple -> {
                    Long total = tuple.getT1();
                    Long present = tuple.getT2();
                    Long absent = tuple.getT3();
                    Long late = tuple.getT4();
                    Long justified = tuple.getT5();
                    Long permission = tuple.getT6();

                    Double attendanceRate = total > 0
                            ? ((present + late) * 100.0) / total
                            : 0.0;

                    return AttendanceStatsResponse.builder()
                            .totalRecords(total)
                            .presentCount(present)
                            .absentCount(absent)
                            .lateCount(late)
                            .justifiedCount(justified)
                            .permissionCount(permission)
                            .attendanceRate(attendanceRate)
                            .build();
                });
    }

    private AttendanceRecord mapToEntity(AttendanceRequest request) {
        return AttendanceRecord.builder()
                .studentId(request.getStudentId())
                .classroomId(request.getClassroomId())
                .institutionId(request.getInstitutionId())
                .attendanceDate(request.getAttendanceDate())
                .academicYear(request.getAcademicYear())
                .attendanceStatus(request.getAttendanceStatus())
                .arrivalTime(request.getArrivalTime())
                .departureTime(request.getDepartureTime())
                .justified(request.getJustified() != null ? request.getJustified() : false)
                .justificationReason(request.getJustificationReason())
                .justificationDocumentUrl(request.getJustificationDocumentUrl())
                .registeredBy(request.getRegisteredBy())
                .build();
    }

    private AttendanceResponse mapToResponse(AttendanceRecord record) {
        return AttendanceResponse.builder()
                .id(record.getId())
                .studentId(record.getStudentId())
                .classroomId(record.getClassroomId())
                .institutionId(record.getInstitutionId())
                .attendanceDate(record.getAttendanceDate())
                .academicYear(record.getAcademicYear())
                .attendanceStatus(record.getAttendanceStatus())
                .arrivalTime(record.getArrivalTime())
                .departureTime(record.getDepartureTime())
                .justified(record.getJustified())
                .justificationReason(record.getJustificationReason())
                .justificationDocumentUrl(record.getJustificationDocumentUrl())
                .registeredBy(record.getRegisteredBy())
                .registeredAt(record.getRegisteredAt())
                .updatedAt(record.getUpdatedAt())
                .build();
    }
    
    private Mono<AttendanceResponse> enrichAttendanceResponse(AttendanceRecord record) {
        return Mono.zip(
                studentClient.getStudentById(record.getStudentId())
                        .doOnNext(student -> {
                            log.debug("[ENRICH] Student data received: id={}, personalInfo={}", 
                                student.getId(), student.getPersonalInfo());
                            if (student.getPersonalInfo() != null) {
                                log.debug("[ENRICH] PersonalInfo: names={}, lastNames={}", 
                                    student.getPersonalInfo().getNames(), 
                                    student.getPersonalInfo().getLastNames());
                            }
                            log.debug("[ENRICH] DisplayName result: {}", student.getDisplayName());
                        })
                        .map(student -> student.getDisplayName())
                        .defaultIfEmpty("Nombre no disponible"),
                institutionClient.getClassroomById(record.getClassroomId())
                        .doOnNext(classroom -> log.debug("[ENRICH] Classroom: {}", classroom.getDisplayName()))
                        .map(classroom -> classroom.getDisplayName())
                        .defaultIfEmpty("Aula no disponible"),
                institutionClient.getInstitutionById(record.getInstitutionId())
                        .doOnNext(institution -> log.debug("[ENRICH] Institution: {}", institution.getDisplayName()))
                        .map(institution -> institution.getDisplayName())
                        .defaultIfEmpty("Institución no disponible")
        ).map(tuple -> {
            String studentName = tuple.getT1();
            String classroomName = tuple.getT2();
            String institutionName = tuple.getT3();
            
            return AttendanceResponse.builder()
                    .id(record.getId())
                    .studentId(record.getStudentId())
                    .studentName(studentName)
                    .classroomId(record.getClassroomId())
                    .classroomName(classroomName)
                    .institutionId(record.getInstitutionId())
                    .institutionName(institutionName)
                    .attendanceDate(record.getAttendanceDate())
                    .academicYear(record.getAcademicYear())
                    .attendanceStatus(record.getAttendanceStatus())
                    .arrivalTime(record.getArrivalTime())
                    .departureTime(record.getDepartureTime())
                    .justified(record.getJustified())
                    .justificationReason(record.getJustificationReason())
                    .justificationDocumentUrl(record.getJustificationDocumentUrl())
                    .registeredBy(record.getRegisteredBy())
                    .registeredAt(record.getRegisteredAt())
                    .updatedAt(record.getUpdatedAt())
                    .build();
        });
    }
    
    @Override
    public Flux<pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.response.ExtendedReferenceDTO> getAllStudents() {
        return studentClient.getAllStudents()
                .map(student -> pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.response.ExtendedReferenceDTO.builder()
                        .id(student.getId())
                        .name(student.getDisplayName())
                        .institutionId(student.getInstitutionId())
                        .classroomId(student.getClassroomId())
                        .build())
                .doOnNext(ref -> log.debug("Student mapped: id={}, name={}, institutionId={}, classroomId={}", 
                        ref.getId(), ref.getName(), ref.getInstitutionId(), ref.getClassroomId()));
    }
    
    @Override
    public Flux<pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.response.ExtendedReferenceDTO> getAllClassrooms() {
        return institutionClient.getAllClassrooms()
                .map(classroom -> pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.response.ExtendedReferenceDTO.builder()
                        .id(classroom.getId())
                        .name(classroom.getDisplayName())
                        .institutionId(classroom.getInstitutionId())
                        .build())
                .doOnNext(ref -> log.debug("Classroom mapped: id={}, name={}, institutionId={}", 
                        ref.getId(), ref.getName(), ref.getInstitutionId()));
    }
    
    @Override
    public Flux<pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.response.ReferenceDTO> getAllInstitutions() {
        return institutionClient.getAllInstitutions()
                .map(institution -> pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.response.ReferenceDTO.builder()
                        .id(institution.getId())
                        .name(institution.getDisplayName())
                        .build())
                .doOnNext(ref -> log.debug("Institution mapped: id={}, name={}", ref.getId(), ref.getName()));
    }
    
    @Override
    @Transactional
    public Mono<pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.response.BulkAttendanceResponse> createBulkAttendance(
            pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.request.BulkAttendanceRequest request) {
        
        log.info("Creating bulk attendance for {} students", request.getStudentIds().size());
        
        return Flux.fromIterable(request.getStudentIds())
                .flatMap(studentId -> {
                    // Verificar si ya existe registro para este estudiante en esta fecha
                    return attendanceRepository.countByStudentIdAndDate(studentId, request.getAttendanceDate())
                            .flatMap(count -> {
                                if (count > 0) {
                                    // Ya existe, retornar error
                                    return studentClient.getStudentById(studentId)
                                            .map(student -> pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.response.BulkAttendanceResponse.FailedRecord.builder()
                                                    .studentId(studentId)
                                                    .studentName(student.getDisplayName())
                                                    .reason("Ya existe un registro de asistencia para esta fecha")
                                                    .build())
                                            .defaultIfEmpty(pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.response.BulkAttendanceResponse.FailedRecord.builder()
                                                    .studentId(studentId)
                                                    .studentName("Desconocido")
                                                    .reason("Ya existe un registro de asistencia para esta fecha")
                                                    .build())
                                            .map(failed -> new BulkResult(null, failed));
                                }
                                
                                // Crear el registro
                                AttendanceRecord record = AttendanceRecord.builder()
                                        .studentId(studentId)
                                        .classroomId(request.getClassroomId())
                                        .institutionId(request.getInstitutionId())
                                        .attendanceDate(request.getAttendanceDate())
                                        .academicYear(request.getAcademicYear())
                                        .attendanceStatus(request.getAttendanceStatus())
                                        .arrivalTime(request.getArrivalTime())
                                        .justified(false)
                                        .registeredBy(request.getRegisteredBy())
                                        .registeredAt(LocalDateTime.now())
                                        .updatedAt(LocalDateTime.now())
                                        .build();
                                
                                return attendanceRepository.save(record)
                                        .map(saved -> new BulkResult(mapToResponse(saved), null))
                                        .onErrorResume(error -> {
                                            log.error("Error saving attendance for student {}: {}", studentId, error.getMessage());
                                            return studentClient.getStudentById(studentId)
                                                    .map(student -> new BulkResult(null, 
                                                            pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.response.BulkAttendanceResponse.FailedRecord.builder()
                                                                    .studentId(studentId)
                                                                    .studentName(student.getDisplayName())
                                                                    .reason("Error al guardar: " + error.getMessage())
                                                                    .build()))
                                                    .defaultIfEmpty(new BulkResult(null,
                                                            pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.response.BulkAttendanceResponse.FailedRecord.builder()
                                                                    .studentId(studentId)
                                                                    .studentName("Desconocido")
                                                                    .reason("Error al guardar: " + error.getMessage())
                                                                    .build()));
                                        });
                            });
                })
                .collectList()
                .map(results -> {
                    var successful = results.stream()
                            .filter(r -> r.success != null)
                            .map(r -> r.success)
                            .toList();
                    
                    var failed = results.stream()
                            .filter(r -> r.failed != null)
                            .map(r -> r.failed)
                            .toList();
                    
                    return pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.response.BulkAttendanceResponse.builder()
                            .totalRequested(request.getStudentIds().size())
                            .successCount(successful.size())
                            .failureCount(failed.size())
                            .successfulRecords(successful)
                            .failedRecords(failed)
                            .build();
                });
    }
    
    @Override
    public Flux<pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.response.ExtendedReferenceDTO> getStudentsByInstitution(String institutionId) {
        return studentClient.getStudentsByInstitution(institutionId)
                .map(student -> pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.response.ExtendedReferenceDTO.builder()
                        .id(student.getId())
                        .name(student.getDisplayName())
                        .institutionId(student.getInstitutionId())
                        .classroomId(student.getClassroomId())
                        .build())
                .doOnNext(ref -> log.debug("Student from institution mapped: id={}, name={}, institutionId={}, classroomId={}", 
                        ref.getId(), ref.getName(), ref.getInstitutionId(), ref.getClassroomId()));
    }
    
    @Override
    public Flux<pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.response.ExtendedReferenceDTO> getClassroomsByInstitution(String institutionId) {
        return institutionClient.getClassroomsByInstitution(institutionId)
                .map(classroom -> pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.response.ExtendedReferenceDTO.builder()
                        .id(classroom.getId())
                        .name(classroom.getDisplayName())
                        .institutionId(classroom.getInstitutionId())
                        .build())
                .doOnNext(ref -> log.debug("Classroom from institution mapped: id={}, name={}, institutionId={}", 
                        ref.getId(), ref.getName(), ref.getInstitutionId()));
    }
    
    // Clase auxiliar para manejar resultados del bulk
    private static class BulkResult {
        final AttendanceResponse success;
        final pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.response.BulkAttendanceResponse.FailedRecord failed;
        
        BulkResult(AttendanceResponse success, pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.response.BulkAttendanceResponse.FailedRecord failed) {
            this.success = success;
            this.failed = failed;
        }
    }
}
