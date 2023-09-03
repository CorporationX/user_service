package school.faang.user_service.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.contact.PreferredContact;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserNotificationDto {
    private long id;
    private String username;
    private String email;
    private String phone;
    private List<ContactDto> contacts;
    private PreferredContact preference;
}
