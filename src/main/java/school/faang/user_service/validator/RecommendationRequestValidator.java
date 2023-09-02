package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RecommendationRequestValidator {
    private final SkillService skillValidator;
    private final UserService userValidator;

    public void validateUsersExist(RecommendationRequestDto recommendationRequest) {
        long requesterId = recommendationRequest.getRequesterId();
        long receiverId = recommendationRequest.getReceiverId();

        userValidator.validateUsers(requesterId, receiverId);
    }

    public void validateSkillsExist(RecommendationRequestDto recommendationRequest) {
        List<Long> skillIds = recommendationRequest.getSkills().stream().map(SkillRequest::getId).toList();
        skillValidator.validateSkills(skillIds);
    }
}
