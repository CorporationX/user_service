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
import school.faang.user_service.entity.recommendation.SkillRequest;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static school.faang.user_service.validator.RecommendationRequestValidator.validateForCreate;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final List<RecommendationRequestFilterInterface> filters;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final RecommendationRequestMapper mapper;

    @Transactional
    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequest) {
        validateForCreate(recommendationRequest);
        Long requesterId = recommendationRequest.getRequesterId();
        boolean requesterExists = userRepository.existsById(requesterId);
        if (!requesterExists) {
            throw new UserNotFoundException("Requester user not found");
        }
        Long receiverId = recommendationRequest.getReceiverId();
        boolean receiverExists = userRepository.existsById(receiverId);
        if (!receiverExists) {
            throw new UserNotFoundException("Receiver user not found");
        }
        Optional<RecommendationRequest> latestPendingRequest = recommendationRequestRepository.findLatestPendingRequest(
                requesterId,
                receiverId
        );
        latestPendingRequest.ifPresent(request -> {
            if (request.getCreatedAt().plusMonths(6).isAfter(LocalDateTime.now())) {
                throw new RecommendationRequestTimeException(
                        "You can't send another recommendation request. 6 months haven't passed");
            }
        });
        List<SkillRequest> skills = recommendationRequest.getSkills();
        int existingSkills = skillRepository.countExisting(
                skills.stream()
                        .map(skillRequest -> skillRequest.getSkill().getId())
                        .toList()
        );
        if (existingSkills < skills.size()) {
            throw new DataValidationException("One or many recommendation request skills are not found");
        }
        RecommendationRequest savedRequest = recommendationRequestRepository.save(mapper.fromDto(recommendationRequest));
        skillRequestRepository.saveAll(recommendationRequest.getSkills());
        return mapper.toDto(savedRequest);
    }

    public List<RecommendationRequestDto> getRequests(RecommendationRequestFilter filter) {
        Stream<RecommendationRequest> requests = StreamSupport.stream(
                recommendationRequestRepository.findAll().spliterator(), false
        );
        return filters.stream()
                .filter(streamFilter -> streamFilter.isApplicable(filter))
                .flatMap(streamFilter -> streamFilter.apply(requests, filter))
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
            throw new RecommendationRequestRejectionException("You can't reject accepted or rejected request");
        }
        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason(rejection.getReason());
        recommendationRequestRepository.save(recommendationRequest);
        return mapper.toDto(recommendationRequest);
    }

    private RecommendationRequest requestById(Long id) {
        return recommendationRequestRepository.findById(id).orElseThrow(
                () -> new RecommendationRequestNotFoundException(String.format(
                        "Recommendation request %d not found",
                        id
                ))
        );
    }
}
