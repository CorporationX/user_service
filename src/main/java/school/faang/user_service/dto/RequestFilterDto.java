package school.faang.user_service.dto;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RequestFilterDto {
    private User requester;
    private User receiver;
    private RequestStatus status;
    private List<SkillRequest> skills;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
