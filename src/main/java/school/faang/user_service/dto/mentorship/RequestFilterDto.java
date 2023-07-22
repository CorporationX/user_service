package school.faang.user_service.dto.mentorship;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import school.faang.user_service.entity.RequestStatus;

@Getter
@Builder
@AllArgsConstructor
public class RequestFilterDto {
    private String description;
    private long requesterId;
    private long receiverId;
    private RequestStatus status;
}
