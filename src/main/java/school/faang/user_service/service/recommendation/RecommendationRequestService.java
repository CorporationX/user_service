package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.filter.RecommendationRequestFilterInterface;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilter;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.exception.recommendation.RecommendationRequestNotFoundException;
import school.faang.user_service.exception.recommendation.RecommendationRequestRejectionException;
import school.faang.user_service.exception.recommendation.RecommendationRequestTimeException;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.validator.RecommendationRequestValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static school.faang.user_service.exception.recommendation.RecommendationRequestExceptions.*;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final List<RecommendationRequestFilterInterface> filters;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final RecommendationRequestMapper mapper;
    private final RecommendationRequestValidator validator;

    @Transactional
    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequest) {
        validator.validateForCreate(recommendationRequest);
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
            if (request.getCreatedAt().plusMonths(6).isAfter(LocalDateTime.now())) {
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
        RecommendationRequest savedRequest = recommendationRequestRepository.save(mapper.fromDto(recommendationRequest));
        skillRequestRepository.saveAll(recommendationRequest.getSkills());
        return mapper.toDto(savedRequest);
    }

    public List<RecommendationRequestDto> getRequests(RecommendationRequestFilter filter) {
        List<RecommendationRequest> requests = StreamSupport.stream(
                recommendationRequestRepository.findAll().spliterator(),
                false
        ).toList();
        return filters.stream()
                .filter(streamFilter -> streamFilter.isApplicable(filter))
                .flatMap(streamFilter -> streamFilter.apply(requests.stream(), filter))
                .distinct()
                .map(mapper::toDto)
                .toList();
    }

    public RecommendationRequestDto getRequestById(Long id) {
        RecommendationRequest recommendationRequest = requestById(id);
        return mapper.toDto(recommendationRequest);
    }

    @Transactional
    public RecommendationRequestDto rejectRequest(Long id, RejectionDto rejection) {
        RecommendationRequest recommendationRequest = requestById(id);
        if (recommendationRequest.getStatus() != RequestStatus.PENDING) {
            throw new RecommendationRequestRejectionException(REJECT_REQUEST_STATUS_NOT_VALID.getMessage());
        }
        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason(rejection.getReason());
        recommendationRequestRepository.save(recommendationRequest);
        return mapper.toDto(recommendationRequest);
    }

    private RecommendationRequest requestById(Long id) {
        return recommendationRequestRepository.findById(id).orElseThrow(
                () -> new RecommendationRequestNotFoundException(String.format(REQUEST_NOT_FOUND.getMessage()))
        );
    }
}
