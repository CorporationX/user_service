package school.faang.user_service.dto.mentorship;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class MentorshipOfferedEvent {
    private Long requesterId;
    private Long receiverId;
    private Long requestId;
}