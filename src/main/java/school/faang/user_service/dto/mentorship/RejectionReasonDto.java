package school.faang.user_service.dto.mentorship;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RejectionReasonDto {
    private String reason;
}
