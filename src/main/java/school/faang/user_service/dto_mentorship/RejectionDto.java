package school.faang.user_service.dto_mentorship;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
public class RejectionDto {
    private String rejectionReason;
    private RequestStatus status;
}
