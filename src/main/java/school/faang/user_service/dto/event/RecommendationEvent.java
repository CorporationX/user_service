package school.faang.user_service.dto.event;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationEvent {
    @NotNull(message = "Recommendation ID cannot be null")
    private Long recommendationId;

    @NotNull(message = "Author ID cannot be null")
    private Long authorId;

    @NotNull(message = "Receiver ID cannot be null")
    private Long receiverId;

    @NotNull(message = "Created At cannot be null")
    private LocalDateTime createdAt;
}
