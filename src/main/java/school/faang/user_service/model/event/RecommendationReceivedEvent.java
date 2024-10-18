package school.faang.user_service.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RecommendationReceivedEvent {
    private Long authorId;
    private Long receiverUserId;
    private Long recommendationId;
}
