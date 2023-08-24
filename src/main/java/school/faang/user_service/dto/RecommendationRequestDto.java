package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationRequestDto {
    private long id;
    private Long requesterId;
    private Long receiverId;
    private String message;
    private RequestStatus status;
    private String rejectionReason;
    private Long recommendationId;
    private List<Long> skillsId;
}
