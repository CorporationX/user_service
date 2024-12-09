package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationRequestFilterDto {

    @NotNull(message = "The status must be specified")
    private RequestStatus status;

    @NotNull(message = "The requester must exist")
    private Long requesterId;

    @NotNull(message = "The receiver must exist")
    private Long receiverId;
}
