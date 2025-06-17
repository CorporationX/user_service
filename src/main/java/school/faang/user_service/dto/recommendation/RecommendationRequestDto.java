package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

import java.util.List;

@Data
@Builder
public class RecommendationRequestDto {
    private Long id;
    private Long requesterId;
    private Long receiverId;

    @NotNull
    private String message;

    private RequestStatus status;
    private List<Long> skillIds;
}
