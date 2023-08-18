package school.faang.user_service.service.redis.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.model.EventType;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationReceivedEvent implements Serializable {

        private EventType eventType;
        private Date receivedAt;
        private Long authorId;
        private Long recipientId;
        private Long recommendationId;
}
