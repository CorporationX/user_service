package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
public class RequestFilterDto {
    String description;
    @Positive
    Long requesterId;
    @Positive
    Long responderId;
    RequestStatus status;
}
