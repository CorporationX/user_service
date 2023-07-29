package school.faang.user_service.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@AllArgsConstructor
@Data
public class RejectionDto {
    RequestStatus status;
    String reason;
}
