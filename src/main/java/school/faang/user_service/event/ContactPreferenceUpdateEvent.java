package school.faang.user_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.contact.PreferredContact;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactPreferenceUpdateEvent {
    private Long userId;
    private PreferredContact newPreference;
    private Long currentUserId;
}
