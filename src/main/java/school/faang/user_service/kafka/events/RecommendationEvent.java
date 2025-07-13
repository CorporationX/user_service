package school.faang.user_service.kafka.events;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RecommendationEvent {
    private Long id;
    private Long authorId;
    private Long recipientId;
    private LocalDateTime timestamp;
}
