package school.faang.user_service.service.publisher.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class AnalyticsEvent {
    private Long id;
    private long receiverId;
    private long actorId;
    private LocalDateTime receivedAt;
}
