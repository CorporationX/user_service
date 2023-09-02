package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.RecommendationRequestService;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RecommendationRequestValidator {
    private final SkillService skillValidator;
    private final UserService userValidator;
    private final RecommendationRequestService recommendationRequestService;
    private static final int MONTHS_BETWEEN_RECOMMENDATIONS = 6;

    public void validateUsersExist(RecommendationRequestDto recommendationRequest) {
        long requesterId = recommendationRequest.getRequesterId();
        long receiverId = recommendationRequest.getReceiverId();

        userValidator.validateUsers(requesterId, receiverId);
    }

    public void validateSkillsExist(RecommendationRequestDto recommendationRequest) {
        List<Long> skillIds = recommendationRequest.getSkills().stream().map(SkillRequest::getId).toList();
        skillValidator.validateSkills(skillIds);
    }

    public void validateRequestPeriod (RecommendationRequestDto recommendationRequest) {

        RecommendationRequest lastRequest = recommendationRequestService.getLastRequest(recommendationRequest);

        LocalDateTime lastRequestsDate = lastRequest.getUpdatedAt();
        LocalDateTime currentRequestDate = recommendationRequest.getCreatedAt();

        if (currentRequestDate.minusMonths(MONTHS_BETWEEN_RECOMMENDATIONS).isAfter(lastRequestsDate)) {
            throw new DataValidationException("A recommendation request from the same requester to the receiver " +
                    "has already been made in the last 6 months");
        }
    }
}
