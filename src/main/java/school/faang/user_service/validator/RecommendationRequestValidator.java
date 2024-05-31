package school.faang.user_service.validator;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.skill.SkillRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
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
    
    public void verifyCanCreate(RecommendationRequestDto recommendationRequest) {
        Long requesterId = recommendationRequest.getRequesterId();
        verifyUserExists(requesterId);
        Long receiverId = recommendationRequest.getReceiverId();
        verifyUserExists(receiverId);
        verifyLastRequestMoreThanSixMonths(requesterId, receiverId);
        verifySkillsExists(recommendationRequest);
    }
    
    private void verifySkillsExists(RecommendationRequestDto recommendationRequest) {
        List<Long> skills = recommendationRequest.getSkills()
                .stream()
                .map(@Valid SkillRequestDto::getSkillId)
                .toList();
        if (skillRepository.countExisting(skills) < skills.size()) {
            throw new DataValidationException("One or many recommendation request skills are not found");
        }
    }
    
    private void verifyLastRequestMoreThanSixMonths(Long requesterId, Long receiverId) {
        Optional<RecommendationRequest> latestPendingRequest = recommendationRequestRepository.findLatestPendingRequest(
            requesterId,
            receiverId
        );
        latestPendingRequest.ifPresent(request -> {
            if (request.getCreatedAt().plusMonths(MONTHS_SHOULD_PASS).isAfter(LocalDateTime.now())) {
                throw new RecommendationRequestTimeException(REQUEST_EXPIRATION_TIME_NOT_PASSED.getMessage());
            }
        });
    }
    
    private void verifyUserExists(Long requesterId) {
        if (!userRepository.existsById(requesterId)) {
            throw new EntityNotFoundException("Requester user not found");
        }
    }
    
    public void verifyStatusIsPending(RecommendationRequest recommendationRequest) {
        if (recommendationRequest.getStatus() != RequestStatus.PENDING) {
            throw new RecommendationRequestRejectionException(REJECT_REQUEST_STATUS_NOT_VALID.getMessage());
        }
    }
}
