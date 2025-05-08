package school.faang.user_service.dto.mentor;

import lombok.Data;

@Data
public class RejectionDto {
    private Long id;
    private RequestStatusDto status;
    private String rejectionReason;
}
