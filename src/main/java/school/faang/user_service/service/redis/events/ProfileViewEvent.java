package school.faang.user_service.service.redis.events;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ProfileViewEvent implements Serializable {
    private Long userId;
    private Long profileViewedId;
    private LocalDateTime date;
}
