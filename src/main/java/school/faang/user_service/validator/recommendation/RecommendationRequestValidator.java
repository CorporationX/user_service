package school.faang.user_service.validator.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.model.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecommendationRequestValidator {
    private static final int SIX_MONTH_IN_DAYS = 180;
    private final UserRepository userRepository;
    private final RecommendationRequestRepository requestRepository;
    private final SkillRequestRepository skillRequestRepository;

    public void validateRecommendationRequest(RecommendationRequestDto recommendationRequestDto) {
        validateUsers(recommendationRequestDto);
        validateRequestPeriod(recommendationRequestDto);
        validateSkills(recommendationRequestDto);
    }

    private void validateSkills(RecommendationRequestDto recommendationRequestDto) {
        boolean allSkillRequestsExist = recommendationRequestDto.skillsId()
                .stream()
                .allMatch(skillRequestRepository::existsById);

        if(!allSkillRequestsExist){
            throw new DataValidationException("Not all skill requests exist in database");
        }
    }

    private void validateUsers(RecommendationRequestDto recommendationRequestDto) {
        if (!userRepository.existsById(recommendationRequestDto.requesterId())) {
            throw new DataValidationException("Requester was not found");
        }

        if (!userRepository.existsById(recommendationRequestDto.receiverId())) {
            throw new DataValidationException("Receiver was not found");
        }
    }

    private void validateRequestPeriod(RecommendationRequestDto recommendationRequestDto) {
        Optional<RecommendationRequest> latestPendingRequest = requestRepository.
                findLatestPendingRequest(recommendationRequestDto.requesterId(),
                        recommendationRequestDto.receiverId());

        if (latestPendingRequest.isPresent()) {
            if (ChronoUnit.DAYS.between(latestPendingRequest.get().getCreatedAt(), recommendationRequestDto.createdAt()) <
                    SIX_MONTH_IN_DAYS) {
                throw new DataValidationException("60 days must pass for a new request");
            }
        }
    }
}
