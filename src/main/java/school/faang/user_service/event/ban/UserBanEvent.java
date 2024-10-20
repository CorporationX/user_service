package school.faang.user_service.event.ban;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserBanEvent {

    private final Long userId;
}
