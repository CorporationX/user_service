package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecommendationRequestDto {
    @Min(value = 0, message = "Id should be a positive value")
    private long id;
    @NotNull(message = "Recommendation request must contain requester Id")
    private Long requesterId;
    @NotNull(message = "Recommendation request must contain receiver Id")
    private Long receiverId;
    @NotBlank(message = "Message cannot be blank")
    private String message;
    private RequestStatus status;
    private String rejectionReason;
    private Long recommendationId;
    @NotEmpty(message = "Recommendation request must contain at least one skill.")
    private List<Long> skillsId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
