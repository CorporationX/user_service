package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Data
@NoArgsConstructor
public class RequestFilterDto {
    String description;
    @Positive
    Long requesterId;
    @Positive
    Long responderId;
    RequestStatus status;
}
