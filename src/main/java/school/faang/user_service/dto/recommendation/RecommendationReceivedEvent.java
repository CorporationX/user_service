package school.faang.user_service.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecommendationReceivedEvent {
    private long id;
    private long authorId;
    private long receiveId;
}
