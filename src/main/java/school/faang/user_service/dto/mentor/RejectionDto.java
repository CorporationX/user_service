package school.faang.user_service.dto.mentor;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
public class RejectionDto {
    private Long id;
    private RequestStatus status;
    private String rejectionReason;
}
