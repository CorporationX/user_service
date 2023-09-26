package school.faang.user_service.messaging.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import school.faang.user_service.entity.contact.PreferredContact;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ProjectFollowerEvent {
    private Long followerId;
    private Long followeeId;
    private PreferredContact preferredContact;


}
