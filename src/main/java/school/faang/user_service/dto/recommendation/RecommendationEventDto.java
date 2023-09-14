package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationEventDto {
    @NotNull
    private Long authorId;
    @NotNull
    private Long receiverId;
}
