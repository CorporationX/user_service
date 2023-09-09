package school.faang.user_service.service.redis.events;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ProfileViewEvent {
    private Long userId;
    private Long profileViewedId;
    private LocalDateTime date;
}
