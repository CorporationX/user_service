package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MentorshipRequestedEvent {
    @NotNull
    @Min(0)
    private Long menteeId;
    @NotNull
    @Min(0)
    private Long mentorId;
    @NotNull
    LocalDateTime timestamp;
}
