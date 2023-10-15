package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class RecommendationReceivedEvent {
    @NotNull
    private Long id;

    @NotNull
    private Long authorId;

    @NotNull
    private Long receiverId;

    private LocalDateTime createdAt;
}
