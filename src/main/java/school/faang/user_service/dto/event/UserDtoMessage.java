package school.faang.user_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.contact.PreferredContact;

@Data
@AllArgsConstructor
@Builder
public class UserDtoMessage {
    private long id;
    private String username;
    private String email;
    private String phone;

}
