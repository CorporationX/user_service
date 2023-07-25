package school.faang.user_service.dto;

import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;
import java.time.LocalDateTime;

@Data
@Builder
public class RequestFilterDto {
    private RequestStatus status;
    private Long requesterId;
    private Long receiverId;
    private LocalDateTime createdAt;
}