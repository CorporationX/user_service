package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationEventDto {
    private Long recomendationId;
    @NotNull
    private Long authorId;
    @NotNull
    private Long receiverId;
    private ZonedDateTime dateTime;
}