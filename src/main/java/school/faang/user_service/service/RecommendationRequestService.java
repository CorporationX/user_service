package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.dto.recommendation.SkillRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilter;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.mapper.SkillRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    @Value("${recommendation-min-distance-months}")
    private int recommendationRequestMinDistanceMonths;

    private final UserService userService;
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRequestService skillRequestService;
    private final SkillService skillService;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final SkillRequestMapper skillRequestMapper;
    private final List<RecommendationRequestFilter> filters;

    @Transactional
    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequest) {
        var recommendationRequestEntity = validateRecommendationRequest(recommendationRequest);

        recommendationRequestEntity.setStatus(RequestStatus.PENDING);
        recommendationRequestEntity.setSkills(new ArrayList<>());

        recommendationRequestEntity = recommendationRequestRepository.save(recommendationRequestEntity);
        var recommendationRequestId = recommendationRequestEntity.getId();

        recommendationRequest.getSkills()
                .forEach(skill -> skillRequestService.createSkillRequest(recommendationRequestId, skill.skillId()));

        var createdDto = recommendationRequestMapper.toDto(recommendationRequestEntity);
        createdDto.setSkills(recommendationRequest.getSkills());

        return createdDto;
    }

    public List<RecommendationRequestDto> getRequests(RequestFilterDto filterDto) {
        var requests = recommendationRequestRepository.findAllWithSkills().stream();
        for (var filter : filters) {
            if (filter.isApplicable(filterDto)) {
                requests = filter.apply(requests, filterDto);
            }
        }

        return requests.map(entity -> {
            var dto = recommendationRequestMapper.toDto(entity);
            dto.setSkills(skillRequestMapper.toDtos(entity.getSkills()));

            return dto;
        }).toList();
    }

    public RecommendationRequestDto getRequest(long id) {
        var entity = recommendationRequestRepository.findById(id)
                .orElseThrow(
                        () -> new DataRetrievalFailureException(
                                "Recommendation request with id %d is not found".formatted(id)));

        var dto = recommendationRequestMapper.toDto(entity);
        var skillRequests = skillRequestService.getSkillRequestsByRequestId(dto.getId());
        dto.setSkills(skillRequestMapper.toDtos(skillRequests));

        return dto;
    }

    public boolean rejectRequest(long id, RejectionDto rejection) {
        var entity = recommendationRequestRepository.findById(id).orElseThrow(
                () -> new DataRetrievalFailureException(
                        "Recommendation request with id %d is not found".formatted(id)));
        if (entity.getStatus() == RequestStatus.ACCEPTED || entity.getStatus() == RequestStatus.REJECTED) {
            return false;
        }

        entity.setStatus(RequestStatus.REJECTED);
        entity.setRejectionReason(rejection.reason());
        entity.setUpdatedAt(LocalDateTime.now());

        recommendationRequestRepository.save(entity);

        return true;
    }

    private RecommendationRequest validateRecommendationRequest(RecommendationRequestDto recommendationRequest) {
        var result = recommendationRequestMapper.toEntity(recommendationRequest);

        var requesterId = recommendationRequest.getRequesterId();
        // Метод findById уже кидает исключение, если пользователь не найден
        result.setRequester(userService.findUserById(requesterId));

        var receiverId = recommendationRequest.getReceiverId();
        result.setReceiver(userService.findUserById(receiverId));

        var latestRequest = recommendationRequestRepository.findLatestRequest(requesterId, receiverId);
        if (latestRequest.isPresent()
                && LocalDateTime.now()
                .minusMonths(recommendationRequestMinDistanceMonths)
                .isBefore(latestRequest.get().getCreatedAt())) {
            throw new DataValidationException(
                    "Less than %d months have passed since the last recommendation request from user %d to user %d"
                            .formatted(
                                    recommendationRequestMinDistanceMonths,
                                    requesterId,
                                    receiverId));
        }

        validateSkills(recommendationRequest);

        return result;
    }

    private void validateSkills(RecommendationRequestDto recommendationRequest) {
        if (recommendationRequest.getSkills() == null || recommendationRequest.getSkills().isEmpty()) {
            throw new DataValidationException("At least 1 skill must be send");
        }

        var requestedSkillIds = recommendationRequest.getSkills()
                .stream()
                .map(SkillRequestDto::skillId)
                .collect(Collectors.toSet());

        var missingSkillIds = requestedSkillIds.stream()
                .filter(skillId -> !skillService.doesSkillExists(skillId))
                .toList();

        if (!missingSkillIds.isEmpty()) {
            var missingSkills = missingSkillIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
            throw new DataValidationException("Skills %s are not registered".formatted(missingSkills));
        }
    }
}
