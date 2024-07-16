package school.faang.user_service.event.user_ban;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserBanEvent {
    private List<Long> userIds;
}
