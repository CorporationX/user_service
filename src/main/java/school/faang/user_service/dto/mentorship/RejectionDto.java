package school.faang.user_service.dto.mentorship;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class RejectionDto {

    @NonNull
    private String reason;
}
