package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@NotNull(message = "Recommendation request dto can't be null")
public class RecommendationRequestDto {
    private Long id;
    @NotBlank(message = "Recommendation request's message should not be blank!")
    private String message;
    private RequestStatus status;
    @NotEmpty(message = "Can't be neither null nor empty")
    private List<Long> skillIds;
    @NotNull(message = "Can't be null!")
    @Positive(message = "Should be more than 0!")
    private Long requesterId;
    @NotNull(message = "Can't be null!")
    @Positive(message = "Should be more than 0!")
    private Long receiverId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
