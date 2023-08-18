package school.faang.user_service.messaging.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfileViewEvent {
    private Long idVisitor;
    private Long idVisited;
}
