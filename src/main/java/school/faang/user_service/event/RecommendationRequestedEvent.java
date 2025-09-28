package school.faang.user_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationRequestedEvent implements Serializable {
    private Long authorId;
    private Long receiverId;
    private Long requestId;
}