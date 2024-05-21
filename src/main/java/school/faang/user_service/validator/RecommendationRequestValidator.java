package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.exception.recommendation.RecommendationRequestRejectionException;
import school.faang.user_service.exception.recommendation.RecommendationRequestTimeException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static school.faang.user_service.exception.recommendation.RecommendationRequestExceptions.*;

@Component
@RequiredArgsConstructor
public class RecommendationRequestValidator {
    private static final int MONTHS_SHOULD_PASS = 6;

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final RecommendationRequestRepository recommendationRequestRepository;

    //TODO: Подумать над общим валидатором полей
    public void verifyCanCreate(RecommendationRequestDto recommendationRequest) {
        if (recommendationRequest.getRequesterId() == null) {
            throw new DataValidationException(REQUEST_REQUESTER_ID_EMPTY.getMessage());
        }
        if (recommendationRequest.getReceiverId() == null) {
            throw new DataValidationException(REQUEST_RECEIVER_ID_EMPTY.getMessage());
        }
        if (CollectionUtils.isEmpty(recommendationRequest.getSkills())) {
            throw new DataValidationException(REQUEST_SKILLS_EMPTY.getMessage());
        }
        Long requesterId = recommendationRequest.getRequesterId();
        if (!userRepository.existsById(requesterId)) {
            throw new UserNotFoundException("Requester user not found");
        }
        Long receiverId = recommendationRequest.getReceiverId();
        if (!userRepository.existsById(receiverId)) {
            throw new UserNotFoundException("Receiver user not found");
        }
        Optional<RecommendationRequest> latestPendingRequest = recommendationRequestRepository.findLatestPendingRequest(
                requesterId,
                receiverId
        );
        latestPendingRequest.ifPresent(request -> {
            if (request.getCreatedAt().plusMonths(MONTHS_SHOULD_PASS).isAfter(LocalDateTime.now())) {
                throw new RecommendationRequestTimeException(REQUEST_EXPIRATION_TIME_NOT_PASSED.getMessage());
            }
        });
        List<Long> skills = recommendationRequest.getSkills()
                .stream()
                .map(skillRequest -> skillRequest.getSkill().getId())
                .toList();
        int existingSkills = skillRepository.countExisting(skills);
        if (existingSkills < skills.size()) {
            throw new DataValidationException("One or many recommendation request skills are not found");
        }
    }

    public void verifyStatusIsPending(RecommendationRequest recommendationRequest) {
        if (recommendationRequest.getStatus() != RequestStatus.PENDING) {
            throw new RecommendationRequestRejectionException(REJECT_REQUEST_STATUS_NOT_VALID.getMessage());
        }
    }

    public void checkMessageIsBlank(String message) {
        if (StringUtils.isBlank(message)) {
            throw new DataValidationException(REQUEST_MESSAGE_EMPTY.getMessage());
        }
    }
}
