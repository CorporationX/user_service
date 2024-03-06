package school.faang.user_service.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class RecommendationDto {
    private long receiverId;
    private long authorId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
