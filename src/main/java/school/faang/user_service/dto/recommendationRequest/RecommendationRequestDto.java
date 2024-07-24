package school.faang.user_service.dto.recommendationRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecommendationRequestDto {
    private long id;
    @NotBlank
    private String message;
    @NotNull
    private RequestStatus status;
    private List<Long> skillsId;
    private long requesterId;
    private long recieverId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}