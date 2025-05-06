package school.faang.user_service.model.promotion.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MentorshipStartEvent {
    private Long mentorId;
    private Long menteeId;
}
