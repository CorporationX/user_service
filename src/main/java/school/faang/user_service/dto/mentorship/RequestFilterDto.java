package school.faang.user_service.dto.mentorship;

import lombok.AllArgsConstructor;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
@AllArgsConstructor
public class RequestFilterDto {
    private String description;
    private Long requesterId;
    private Long receiverId;
    private RequestStatus status;
}
