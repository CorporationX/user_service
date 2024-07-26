package school.faang.user_service.dto.recommendation;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
public class RejectionDto {
    private String reason;
    private RequestStatus status;
}
