package school.faang.user_service.dto.recomendation;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class RecommendationRequestDto {
    private Long id, requesterId, receiverId;
    private String message;
    RequestStatus status;
    List<Long> skillsId;
    private LocalDateTime createdAt, updatedAt;
}
