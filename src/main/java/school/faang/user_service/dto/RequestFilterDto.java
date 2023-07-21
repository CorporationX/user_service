package school.faang.user_service.dto;

import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RequestFilterDto {
    private String messagePattern;
    private RequestStatus statusPattern;
    private List<Long> skillsPattern;
    private Long requesterIdPattern;
    private Long receiverIdPattern;
    private LocalDateTime createdAtPattern;
    private LocalDateTime updatedAtPattern;
}
