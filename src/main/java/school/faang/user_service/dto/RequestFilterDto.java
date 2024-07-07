package school.faang.user_service.dto;

import lombok.*;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.util.List;

@Data
@Builder
public class RequestFilterDto {
    private long id;
    private RequestStatus status;
    private Long requesterId;
    private Long receiverId;
    private Recommendation recommendation;
    private List<SkillRequestDto> skills;
}
