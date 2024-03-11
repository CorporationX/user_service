package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class RecommendationRequestDto {
    private Long id;
    @NotBlank(message = "Message is blank")
    private String message;
    @NotNull
    private RequestStatus status;
    private List<Long> skillIds;
    @NotNull(message = "Requester can't be null")
    private Long requesterId;
    @NotNull(message = "Receiver can't be null")
    private Long receiverId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
