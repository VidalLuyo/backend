package pe.edu.vallegrande.vg_ms_assistance.infrastructure.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.StudentDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StudentClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${external.services.student.url:http://localhost:9081}")
    private String studentServiceUrl;

    public Mono<StudentDTO> getStudentById(String studentId) {
        return webClientBuilder.build()
                .get()
                .uri(studentServiceUrl + "/api/v1/students/" + studentId)
                .retrieve()
                .bodyToMono(
                        new org.springframework.core.ParameterizedTypeReference<pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.ApiResponse<StudentDTO>>() {
                        })
                .flatMap(response -> {
                    if (response != null) {
                        List<StudentDTO> dataList = response.getDataAsList(StudentDTO.class);
                        if (!dataList.isEmpty()) {
                            StudentDTO student = dataList.get(0);
                            log.debug("[CLIENT] Student received: id={}, personalInfo={}",
                                    student.getId(), student.getPersonalInfo());
                            if (student.getPersonalInfo() != null) {
                                log.debug("[CLIENT] PersonalInfo: names={}, lastNames={}",
                                        student.getPersonalInfo().getNames(),
                                        student.getPersonalInfo().getLastNames());
                            }
                            return Mono.just(student);
                        }
                    }
                    log.warn("No student data received for studentId: {}", studentId);
                    return Mono.empty();
                })
                .doOnError(error -> log.error("Error fetching student {}: {}", studentId, error.getMessage()))
                .onErrorResume(error -> Mono.empty());
    }

    public Flux<StudentDTO> getAllStudents() {
        return webClientBuilder.build()
                .get()
                .uri(studentServiceUrl + "/api/v1/students")
                .retrieve()
                .bodyToMono(
                        new org.springframework.core.ParameterizedTypeReference<pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.ApiResponse<StudentDTO>>() {
                        })
                .flatMapMany(response -> {
                    if (response != null) {
                        List<StudentDTO> dataList = response.getDataAsList(StudentDTO.class);
                        log.debug("Received {} students from service", dataList.size());
                        return Flux.fromIterable(dataList);
                    }
                    log.warn("No students data received from service");
                    return Flux.empty();
                })
                .doOnNext(student -> log.debug("Student: studentId={}, names={}",
                        student.getStudentId(),
                        student.getPersonalInfo() != null ? student.getPersonalInfo().getNames() : "null"))
                .doOnError(error -> log.error("Error fetching students: {}", error.getMessage()))
                .onErrorResume(error -> Flux.empty());
    }

    public Mono<Boolean> validateStudent(String studentId) {
        return getStudentById(studentId)
                .map(student -> "ACTIVE".equalsIgnoreCase(student.getStatus()))
                .defaultIfEmpty(true);
    }

    public Flux<StudentDTO> getStudentsByInstitution(String institutionId) {
        return webClientBuilder.build()
                .get()
                .uri(studentServiceUrl + "/api/v1/students/institution/" + institutionId)
                .retrieve()
                .bodyToMono(
                        new org.springframework.core.ParameterizedTypeReference<pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.ApiResponse<StudentDTO>>() {
                        })
                .flatMapMany(response -> {
                    if (response != null) {
                        List<StudentDTO> dataList = response.getDataAsList(StudentDTO.class);
                        log.debug("Received {} students from institution {}", dataList.size(), institutionId);
                        return Flux.fromIterable(dataList);
                    }
                    log.warn("No students data received for institution {}", institutionId);
                    return Flux.empty();
                })
                .doOnError(error -> {
                    log.warn("Error fetching students by institution endpoint, will try fallback: {}",
                            error.getMessage());
                })
                .onErrorResume(error -> {
                    // Fallback: obtener todos los estudiantes y filtrar por institutionId
                    log.info("Using fallback: filtering all students by institutionId");
                    return getAllStudents()
                            .filter(student -> {
                                String studentInstitutionId = student.getInstitutionId();
                                boolean matches = studentInstitutionId != null
                                        && studentInstitutionId.equals(institutionId);
                                if (matches) {
                                    log.debug("Student {} belongs to institution {}", student.getId(), institutionId);
                                }
                                return matches;
                            });
                });
    }
}
