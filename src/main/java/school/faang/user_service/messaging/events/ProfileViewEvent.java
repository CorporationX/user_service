package school.faang.user_service.messaging.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.contact.PreferredContact;

@Data
@AllArgsConstructor
public class ProfileViewEvent {
    private Long idVisitor;
    private Long idVisited;
    private PreferredContact preferredContact;

    @Override
    public String toString() {
        return idVisitor + "\n" + idVisited;
    }
}
