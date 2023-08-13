package school.faang.user_service.dto;

import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class RequestFilterDto {
    private String messagePattern;
    private RequestStatus status;
    private LocalDateTime createdAfter;
    private LocalDateTime createdBefore;
    private LocalDateTime updatedAfter;
    private LocalDateTime updatedBefore;

}
