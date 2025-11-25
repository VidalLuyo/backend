package pe.edu.vallegrande.vg_ms_assistance.infrastructure.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.ClassroomDTO;
import pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.InstitutionDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InstitutionClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${external.services.institution.url:http://localhost:9080}")
    private String institutionServiceUrl;

    public Mono<InstitutionDTO> getInstitutionById(String institutionId) {
        return webClientBuilder.build()
                .get()
                .uri(institutionServiceUrl + "/api/v1/institutions/" + institutionId)
                .retrieve()
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.ApiResponse<InstitutionDTO>>() {})
                .flatMap(response -> {
                    if (response != null) {
                        List<InstitutionDTO> dataList = response.getDataAsList(InstitutionDTO.class);
                        if (!dataList.isEmpty()) {
                            return Mono.just(dataList.get(0));
                        }
                    }
                    log.warn("No institution data received for institutionId: {}", institutionId);
                    return Mono.empty();
                })
                .doOnError(error -> log.error("Error fetching institution {}: {}", institutionId, error.getMessage()))
                .onErrorResume(error -> Mono.empty());
    }

    public Flux<InstitutionDTO> getAllInstitutions() {
        return webClientBuilder.build()
                .get()
                .uri(institutionServiceUrl + "/api/v1/institutions")
                .retrieve()
                .bodyToMono(
                        new org.springframework.core.ParameterizedTypeReference<pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.ApiResponse<InstitutionDTO>>() {
                        })
                .flatMapMany(response -> {
                    if (response != null) {
                        List<InstitutionDTO> dataList = response.getDataAsList(InstitutionDTO.class);
                        log.debug("Received {} institutions from service", dataList.size());
                        return Flux.fromIterable(dataList);
                    }
                    log.warn("No institutions data received from service");
                    return Flux.empty();
                })
                .doOnNext(institution -> log.debug("Institution: institutionId={}",
                        institution.getInstitutionId()))
                .doOnError(error -> log.error("Error fetching institutions: {}", error.getMessage()))
                .onErrorResume(error -> Flux.empty());
    }

    public Mono<ClassroomDTO> getClassroomById(String classroomId) {
        return webClientBuilder.build()
                .get()
                .uri(institutionServiceUrl + "/api/v1/classrooms/" + classroomId)
                .retrieve()
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.ApiResponse<ClassroomDTO>>() {})
                .flatMap(response -> {
                    if (response != null) {
                        List<ClassroomDTO> dataList = response.getDataAsList(ClassroomDTO.class);
                        if (!dataList.isEmpty()) {
                            return Mono.just(dataList.get(0));
                        }
                    }
                    log.warn("No classroom data received for classroomId: {}", classroomId);
                    return Mono.empty();
                })
                .doOnError(error -> log.error("Error fetching classroom {}: {}", classroomId, error.getMessage()))
                .onErrorResume(error -> Mono.empty());
    }

    public Flux<ClassroomDTO> getAllClassrooms() {
        return webClientBuilder.build()
                .get()
                .uri(institutionServiceUrl + "/api/v1/classrooms")
                .retrieve()
                .bodyToMono(
                        new org.springframework.core.ParameterizedTypeReference<pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.ApiResponse<ClassroomDTO>>() {
                        })
                .flatMapMany(response -> {
                    if (response != null) {
                        List<ClassroomDTO> dataList = response.getDataAsList(ClassroomDTO.class);
                        log.debug("Received {} classrooms from service", dataList.size());
                        return Flux.fromIterable(dataList);
                    }
                    log.warn("No classrooms data received from service");
                    return Flux.empty();
                })
                .doOnNext(classroom -> log.debug("Classroom: classroomId={}, classroomName={}",
                        classroom.getClassroomId(), classroom.getClassroomName()))
                .doOnError(error -> log.error("Error fetching classrooms: {}", error.getMessage()))
                .onErrorResume(error -> Flux.empty());
    }

    public Flux<ClassroomDTO> getClassroomsByInstitution(String institutionId) {
        return webClientBuilder.build()
                .get()
                .uri(institutionServiceUrl + "/api/v1/classrooms/institution/" + institutionId)
                .retrieve()
                .bodyToMono(
                        new org.springframework.core.ParameterizedTypeReference<pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.ApiResponse<ClassroomDTO>>() {
                        })
                .flatMapMany(response -> {
                    if (response != null) {
                        List<ClassroomDTO> dataList = response.getDataAsList(ClassroomDTO.class);
                        log.debug("Received {} classrooms from institution {}", dataList.size(),
                                institutionId);
                        return Flux.fromIterable(dataList);
                    }
                    log.warn("No classrooms data received for institution {}", institutionId);
                    return Flux.empty();
                })
                .doOnError(error -> {
                    log.warn("Error fetching classrooms by institution endpoint, will try fallback: {}",
                            error.getMessage());
                })
                .onErrorResume(error -> {
                    // Fallback: obtener todas las aulas y filtrar por institutionId
                    log.info("Using fallback: filtering all classrooms by institutionId");
                    return getAllClassrooms()
                            .filter(classroom -> {
                                String classroomInstitutionId = classroom.getInstitutionId();
                                boolean matches = classroomInstitutionId != null
                                        && classroomInstitutionId.equals(institutionId);
                                if (matches) {
                                    log.debug("Classroom {} belongs to institution {}", classroom.getId(),
                                            institutionId);
                                }
                                return matches;
                            });
                });
    }
}
