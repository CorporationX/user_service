package school.faang.user_service.dto;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
public class RequestFilterDto {
    private Long requesterId;
    private Long receiverId;
    private RequestStatus status;
}
