package pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceStatsResponse {
    private Long totalRecords;
    private Long presentCount;
    private Long absentCount;
    private Long lateCount;
    private Long justifiedCount;
    private Long permissionCount;
    private Double attendanceRate;
}
