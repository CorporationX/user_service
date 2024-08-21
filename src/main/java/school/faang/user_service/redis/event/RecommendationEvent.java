package school.faang.user_service.redis.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationEvent {

    private long authorId;

    private long receiverId;

    private String recommendationText;

    private LocalDateTime sendAt;
}
