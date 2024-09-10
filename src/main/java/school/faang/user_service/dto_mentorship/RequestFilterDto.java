package school.faang.user_service.dto_mentorship;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
public class RequestFilterDto {
    private String description;
    private Long requester;
    private Long receiver;
    private RequestStatus status;
}
