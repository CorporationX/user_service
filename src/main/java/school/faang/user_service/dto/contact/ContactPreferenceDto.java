package school.faang.user_service.dto.contact;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.PreferredContact;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactPreferenceDto {
    private long id;
    private User user;
    private PreferredContact preference;
}
