package pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkAttendanceResponse {
    private Integer totalRequested;
    private Integer successCount;
    private Integer failureCount;
    private List<AttendanceResponse> successfulRecords;
    private List<FailedRecord> failedRecords;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FailedRecord {
        private String studentId;
        private String studentName;
        private String reason;
    }
}
