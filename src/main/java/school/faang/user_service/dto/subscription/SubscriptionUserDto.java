package school.faang.user_service.dto.subscription;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SubscriptionUserDto {
    private long id;
    private String username;
    private String email;
}
