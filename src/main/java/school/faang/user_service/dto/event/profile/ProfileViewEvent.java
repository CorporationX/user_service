package school.faang.user_service.dto.event.profile;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
public class ProfileViewEvent {
    private long userId;
    private long viewerId;
    @CreationTimestamp
    private LocalDateTime viewedAt;
}

