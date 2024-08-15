package school.faang.user_service.dto.event.recomendationRerquested;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record RecommendationRequestedEvent(String message, long requesterId, long receiverId, LocalDateTime localDateTime) {

}
