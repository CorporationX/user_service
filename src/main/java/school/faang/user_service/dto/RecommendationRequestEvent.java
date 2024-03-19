package school.faang.user_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecommendationRequestEvent {
    @NotNull
    private Long id;
    @NotNull
    private Long authorId;
    @NotNull
    private Long receiverId;
    private LocalDateTime createdAt;
}
