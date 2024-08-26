package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.contact.PreferredContact;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserReadDto {

    private long id;
    private String username;
    private String email;
    private String phone;
    private Long telegramId;
    private PreferredContact preference;
}
