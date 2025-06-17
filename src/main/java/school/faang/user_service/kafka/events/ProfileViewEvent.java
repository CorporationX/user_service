package school.faang.user_service.kafka.events;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public final class ProfileViewEvent {
    private Long viewedUserId;
    private Long viewerUserId;
    private LocalDateTime localDateTime;
    private AnalyticsEventType eventType;
}
