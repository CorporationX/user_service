package school.faang.user_service.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import school.faang.user_service.entity.RequestStatus;

@AllArgsConstructor
@Setter
@Getter
public class RejectionDto {
    RequestStatus status;
    String reason;
}
