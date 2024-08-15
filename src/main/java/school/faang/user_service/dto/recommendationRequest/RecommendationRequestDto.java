package school.faang.user_service.dto.recommendationRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
public class RecommendationRequestDto {
    private long id;
    @NotBlank
    private String message;
    @NotNull
    private RequestStatus status;
    private List<Long> skillsId;
    private long requesterId;
    private long receiverId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}