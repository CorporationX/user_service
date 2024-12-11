package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationRejectionDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.exceptions.ResourceNotFoundException;
import school.faang.user_service.filter.recommendationRequestFilters.RecommendationRequestFilter;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private static final int ACCEPTABLE_MONTH_BETWEEN_REQUEST = 6;

    private final UserService userService;
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final SkillService skillService;
    private final SkillRequestService skillRequestService;
    private final List<RecommendationRequestFilter> recommendationRequestFilters;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        RecommendationRequest recommendationRequest = initializeRecommendationRequest(recommendationRequestDto);
        validateRecommendationRequest(recommendationRequest);

        recommendationRequest = recommendationRequestRepository.save(recommendationRequest);
        saveSkillRequests(recommendationRequest);
        return recommendationRequestMapper.toDto(recommendationRequest);
    }

    public List<RecommendationRequestDto> getRequests(RequestFilterDto filterDto) {
        Stream<RecommendationRequest> recommendationRequestsStream = recommendationRequestRepository.findAll().stream();
        List<RecommendationRequest> recommendationRequestList = recommendationRequestFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(recommendationRequestsStream, (stream, filter) -> filter.apply(stream, filterDto),
                        ((subGenStream, stream) -> stream))
                .distinct()
                .toList();

        return recommendationRequestMapper.toDtoList(recommendationRequestList);
    }

    public RecommendationRequestDto getRequest(long id) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unable to find recommendation request with id " + id));
        return recommendationRequestMapper.toDto(recommendationRequest);
    }

    public RecommendationRequestDto rejectRequest(RecommendationRejectionDto rejection) {
        RecommendationRequest recommendationRequest =
                recommendationRequestMapper.toEntity(getRequest(rejection.recommendationId()));
        if (recommendationRequest.getStatus().equals(RequestStatus.ACCEPTED) ||
                recommendationRequest.getStatus().equals(RequestStatus.REJECTED)) {
            throw new DataValidationException("The recommendation request has already been accepted or rejected");
        }

        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason(rejection.reason());
        recommendationRequest = recommendationRequestRepository.save(recommendationRequest);
        return recommendationRequestMapper.toDto(recommendationRequest);
    }

    private void validateRecommendationRequest(RecommendationRequest recommendationRequest) {
        long requesterId = recommendationRequest.getRequester().getId();
        long receiverId = recommendationRequest.getReceiver().getId();
        recommendationRequestRepository.findLatestPendingRequest(requesterId, receiverId)
                .ifPresent(lastRequest -> {
                    if (!(ChronoUnit.MONTHS.between(LocalDateTime.now(), lastRequest.getCreatedAt()) >= ACCEPTABLE_MONTH_BETWEEN_REQUEST)) {
                        throw new DataValidationException("Less than 6 months since the last request");
                    }
                });
    }

    private RecommendationRequest initializeRecommendationRequest(RecommendationRequestDto recommendationRequestDto) {
        RecommendationRequest recommendationRequest = recommendationRequestMapper.toEntity(recommendationRequestDto);

        User requester = userService.getUserById(recommendationRequestDto.requesterId());
        User receiver = userService.getUserById(recommendationRequestDto.receiverId());
        recommendationRequest.setRequester(requester);
        recommendationRequest.setReceiver(receiver);
        List<SkillRequest> skillRequests = recommendationRequestDto.skillIds().stream()
                .map(skillId -> {
                    Skill skill = skillService.getSkillById(skillId);
                    SkillRequest skillRequest = new SkillRequest();
                    skillRequest.setSkill(skill);
                    return skillRequest;
                }).toList();
        recommendationRequest.setSkills(skillRequests);

        return recommendationRequest;
    }

    private void saveSkillRequests(RecommendationRequest recommendationRequest) {
        List<SkillRequest> skillRequests = recommendationRequest.getSkills();
        skillRequests.forEach(skillRequestService::save);
    }
}
