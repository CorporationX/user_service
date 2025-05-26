package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

@Data
public class RecommendationRequestDto {
    private Long id;
    @NotBlank(message = "message cannot be blank")
    private String message;
    @NotNull(message = "requesterId cannot be blank")
    private Long requesterId;
    @NotNull(message = "receiverId cannot be blank")
    private Long receiverId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private RequestStatus status;
    private String rejectionReason;
}
