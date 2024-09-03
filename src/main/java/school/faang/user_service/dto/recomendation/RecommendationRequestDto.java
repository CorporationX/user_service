package school.faang.user_service.dto.recomendation;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class RecommendationRequestDto {
    private Long id, requesterId, recieverId;
    private String message;
    Enum<RequestStatus> status;
    List<Long> skillsId;
    private LocalDateTime createdAt, updatedAt;
}
