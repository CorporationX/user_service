package school.faang.user_service.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RecommendationRequestDto {

    private long id;
    private String message;
    private UserDto requester;
    private UserDto receiver;
    private RequestStatus status;
    private String rejectionReason;
    private List<SkillRequest> skillRequests;
    private LocalDateTime createdAt;

}
