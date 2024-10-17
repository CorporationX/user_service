package school.faang.user_service.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class MentorshipOfferedEvent {
    private Long requesterId;
    private Long requestId;
    private Long mentorId;
}
