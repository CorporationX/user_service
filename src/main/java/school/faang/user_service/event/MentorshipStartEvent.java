package school.faang.user_service.event;

import lombok.Data;

@Data
public class MentorshipStartEvent {

    private Long id;
    private Long requesterId;
    private Long receiverId;
}
