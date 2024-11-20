package school.faang.user_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RejectionDto {
    @NotNull(groups = {RecommendationRequestDto.After.class})
    private Long id;

    @NotNull(groups = {RecommendationRequestDto.After.class})
    private String message;

    @NotNull(groups = {RecommendationRequestDto.After.class})
    private RequestStatus status;

    @NotNull(groups = {RecommendationRequestDto.Before.class})
    private Long requesterId;

    @NotNull(groups = {RecommendationRequestDto.Before.class})
    private Long receiverId;

    @NotNull
    private String reason;

    private LocalDateTime createdAt;
}
