package school.faang.user_service.dto;

import lombok.Data;
import school.faang.user_service.entity.contact.PregerredContactNotification;


import java.util.Locale;

@Data
public class UserEventDto {
    private long id;
    private String username;
    private String email;
    private String phone;
    private PregerredContactNotification preference;
    private Locale locale;

}
