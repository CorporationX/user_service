package school.faang.user_service.dto.rejection;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RejectionDto {
    private String reason;
}
