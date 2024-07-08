package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.filter.RecommendationFilter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final UserService userService;
    private final SkillRequestService skillRequestService;
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final List<RecommendationFilter<RecommendationRequest, RequestFilterDto>> recommendationFilters;
    private static final int SIX_MONTHS_INTERVAL = 6;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequest) {
        User requester = userService.findById(recommendationRequest.getRequesterId());
        User receiver = userService.findById(recommendationRequest.getReceiverId());

        List<Long> skillIds = recommendationRequest.getSkills();
        if (skillIds.isEmpty()) {
            throw new DataValidationException("Список навыков пуст");
        }
        List<Long> existingSkillIds = skillRequestService.findExistingSkillIds(skillIds);

        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(SIX_MONTHS_INTERVAL);
        Optional<RecommendationRequest> existingRequest = recommendationRequestRepository.findLatestPendingRequest(requester.getId(), receiver.getId());
        if (existingRequest.isPresent() && existingRequest.get().getCreatedAt().isAfter(sixMonthsAgo)) {
            throw new IllegalArgumentException("Запрос был менее чем " + SIX_MONTHS_INTERVAL + " месяцев назад");
        }

        RecommendationRequest recommendationRequestEntity = recommendationRequestMapper.toEntity(recommendationRequest);
        List<SkillRequest> skills = skillRequestRepository.findAllById(skillIds).stream().toList();
        recommendationRequestEntity.setSkills(skills);
        saveSkills(skillIds, recommendationRequestEntity.getId());
        return recommendationRequestMapper.toDto(recommendationRequestRepository.save(recommendationRequestEntity));
    }

    private void saveSkills(List<Long> skillIds, long requestId) {
        for (Long skillId : skillIds) {
            skillRequestRepository.create(requestId, skillId);
        }
    }

    public RecommendationRequest getRequestEntity(long id) {
        return recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Запрос рекомендации не найден по идентификатору: " + id));
    }

    public RecommendationRequestDto getRequest(long id) {
        RecommendationRequest recommendationRequest = getRequestEntity(id);
        return recommendationRequestMapper.toDto(recommendationRequest);
    }

    public List<RecommendationRequestDto> getRequests(RequestFilterDto filters) {
        Stream<RecommendationRequest> recommendations = recommendationRequestRepository.findAll().stream();
        recommendations = recommendationFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(recommendations, (stream, filter) -> filter.apply(stream, filters), (s1, s2) -> s1);
        return recommendations.map(recommendationRequestMapper::toDto).toList();
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        RecommendationRequest recommendationRequest = getRequestEntity(id);
        if (recommendationRequest.getStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException("Запрос уже обработан: " + id);
        }
        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason(rejection.getReason());
        recommendationRequest.setUpdatedAt(LocalDateTime.now());
        return recommendationRequestMapper.toDto(recommendationRequestRepository.save(recommendationRequest));
    }
}
