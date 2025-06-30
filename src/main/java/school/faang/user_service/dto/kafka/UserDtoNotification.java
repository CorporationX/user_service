package school.faang.user_service.dto.kafka;

import lombok.Data;
import school.faang.user_service.entity.contact.PreferredContact;

import java.util.Locale;

@Data
public class UserDtoNotification {
    private long id;
    private String username;
    private String email;
    private String phone;
    private PreferredContact preference;
    private Locale locale;
}
