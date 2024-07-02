package school.faang.user_service.dto.event;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RecommendationEventDto {
    private Long id;
    private Long authorId;
    private Long receiverId;
    private LocalDateTime createdAt;
}
