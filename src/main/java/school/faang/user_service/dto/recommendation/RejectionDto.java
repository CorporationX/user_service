package school.faang.user_service.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
@AllArgsConstructor
public class RejectionDto {
    private Long id;
    private String reason;
    private RequestStatus status;
}
