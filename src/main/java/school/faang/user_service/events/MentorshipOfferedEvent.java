package school.faang.user_service.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MentorshipOfferedEvent {
    private long authorId;
    private long mentorId;
    private long requestId;
}
