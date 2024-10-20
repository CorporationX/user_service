package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecommendationRequestDto {
    private long id;
    @Size(max = 500, message = "Message cannot exceed 500 characters")
    @NotBlank(message = "Message cannot be empty")
    private String message;
    private RequestStatus status;
    @NotEmpty(message = "Skill list cannot be empty")
    private List<Long> skillId;
    @NotNull(message = "Requester ID cannot be null")
    private Long requesterId;
    @NotNull(message = "Receiver ID cannot be null")
    private Long receiverId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
