package school.faang.user_service.event.mentorship.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorshipAcceptedEvent {

    private final UUID eventId = UUID.randomUUID();
    private long mentorshipRequestId;
    private long requesterId;
    private long receiverId;

}
