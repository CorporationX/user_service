package school.faang.user_service.dto;

import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.contact.PreferredContact;

import java.util.Locale;

@Data
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private Locale locale;
    private PreferredContact preferredContact;
    private boolean active;
    private Integer experience;
}
