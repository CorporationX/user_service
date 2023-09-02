package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecommendationRequestValidator {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillService skillValidator;
    private final UserService userValidator;
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
        long requesterId = recommendationRequest.getRequesterId();
        long receiverId = recommendationRequest.getReceiverId();
        Optional<RecommendationRequest> lastRequest = recommendationRequestRepository.findLatestPendingRequest(requesterId, receiverId);
        LocalDateTime lastRequestsDate = lastRequest.get().getUpdatedAt();
        LocalDateTime currentRequestDate = recommendationRequest.getCreatedAt();

        if (lastRequest.isPresent() && currentRequestDate.minusMonths(MONTHS_BETWEEN_RECOMMENDATIONS).isAfter(lastRequestsDate)) {
            throw new DataValidationException("A recommendation request from the same requester to the receiver has already been made in the last 6 months");
        }
    }
}
