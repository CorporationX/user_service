package school.faang.user_service.service.recommendation;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilterStrategy;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RecommendationRequestServiceImpl implements RecommendationRequestService {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final List<RecommendationRequestFilterStrategy> recommendationRequestFilters;
    private final RecommendationRequestMapper recommendationRequestMapper;

    @Override
    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        User requester = userRepository.findById(recommendationRequestDto.getRequesterId())
                .orElseThrow(() -> new EntityNotFoundException("Requester with id %s was not found".formatted(recommendationRequestDto.getRequesterId())));
        User receiver = userRepository.findById(recommendationRequestDto.getReceiverId())
                .orElseThrow(() -> new EntityNotFoundException("Receiver with id %s was not found".formatted(recommendationRequestDto.getReceiverId())));

        RecommendationRequest recommendationRequest = recommendationRequestRepository.findLatestPendingRequest(
                        recommendationRequestDto.getRequesterId(),
                        recommendationRequestDto.getReceiverId())
                .orElse(recommendationRequestMapper.toEntity(recommendationRequestDto));

        recommendationRequest.setRequester(requester);
        recommendationRequest.setReceiver(receiver);

        if (recommendationRequest.getUpdatedAt().isAfter(LocalDateTime.now().minus(Period.ofMonths(6)))) {
            throw new IllegalArgumentException("Recommendation request has already been updated in the last 6 months.");
        }

        boolean allSkillsFound = recommendationRequestDto.getSkillIds().stream().allMatch(skillRepository::existsById);

        if (!allSkillsFound) {
            throw new EntityNotFoundException("Not all required skills exist in data base");
        }

        recommendationRequest.setSkills(
                recommendationRequestDto.getSkillIds()
                        .stream()
                        .map(skillRepository::findById)
                        .filter(Optional::isPresent)
                        .map(optionalSkill ->
                                skillRequestRepository.create(
                                        recommendationRequest.getId(),
                                        optionalSkill.get().getId()))
                        .toList());

        RecommendationRequest savedRecommendationRequest = recommendationRequestRepository.save(recommendationRequest);

        return recommendationRequestMapper.toDto(savedRecommendationRequest);
    }

    @Override
    public List<RecommendationRequestDto> getRequests(RequestFilterDto filter) {
        Stream<RecommendationRequest> recommendations = recommendationRequestRepository.findAll().stream();

        for (RecommendationRequestFilterStrategy recommendationRequestFilter : recommendationRequestFilters) {
            if (recommendationRequestFilter.isApplicable(filter)) {
                recommendations = recommendationRequestFilter.apply(recommendations, filter);
            }
        }

        return recommendations.map(recommendationRequestMapper::toDto).toList();
    }

    @Override
    public RecommendationRequestDto getRequest(long id) {
        return recommendationRequestMapper.toDto(recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recommendation request with id %s doesn't exist".formatted(id))));
    }

    @Override
    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recommendation request with id %s doesn't exist".formatted(id)));

        if (recommendationRequest.getStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException("Unable to reject request");
        }

        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason(rejection.getReason());

        recommendationRequestRepository.save(recommendationRequest);

        return recommendationRequestMapper.toDto(recommendationRequest);
    }
}
