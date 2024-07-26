package school.faang.user_service.dto.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.filter.FilterDto;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RequestFilterDto extends FilterDto {
    private String requesterName;
    private String receiverName;
    private String message;
    private RequestStatus status;
    private String rejectionReason;
    private Long recommendationId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
