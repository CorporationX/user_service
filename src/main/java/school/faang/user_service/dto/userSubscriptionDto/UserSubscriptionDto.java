package school.faang.user_service.dto.userSubscriptionDto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserSubscriptionDto {
    private Long id;
    private String username;
    private String email;
}
