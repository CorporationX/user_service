package school.faang.user_service.event.mentorship.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MentorshipOfferedEvent {
    private final UUID eventId = UUID.randomUUID();
    private long mentorshipOfferId;
    private long requesterId;
    private long receiverId;
}
