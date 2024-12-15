package school.faang.user_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationRequestEvent {
    private Long requestId;
    private Long receiverId;
    private Long requesterId;
}
