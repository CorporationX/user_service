package school.faang.user_service.dto.subscription;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowerEvent {
    private Long subscriberId;
    private Long targetUserId;
    private Long projectId;
    private LocalDateTime subscriptionDateTime;
}
