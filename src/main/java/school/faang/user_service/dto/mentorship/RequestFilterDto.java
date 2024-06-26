package school.faang.user_service.dto.mentorship;

import lombok.Data;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.RequestStatus;

@Component
@Data
public class RequestFilterDto {

    private String description;

    private Long requesterId;

    private Long receiverId;

    private RequestStatus status;
}
