package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recomendation.RecommendationRequestDto;
import school.faang.user_service.dto.recomendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.recomendation.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.recommendation.filters.RecommendationRequestFilter;
import school.faang.user_service.validator.recommendation.RecommendationRequestValidator;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final SkillRequestRepository skillRequestRepository;
    private final RecommendationRequestValidator recommendationRequestValidator;
    private final List<RecommendationRequestFilter> recommendationRequestFilters;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        recommendationRequestValidator.isUsersInDb(recommendationRequestDto);
        recommendationRequestValidator.isSkillsInDb(recommendationRequestDto);
        recommendationRequestValidator.isRequestAllowed(recommendationRequestDto);

        List<SkillRequest> skillRequests = StreamSupport.stream(skillRequestRepository
                .findAllById(recommendationRequestDto.getSkillsIds()).spliterator(), false).toList();
        RecommendationRequest recommendationRequestEntity = recommendationRequestMapper.mapToEntity(recommendationRequestDto);
        recommendationRequestEntity.setSkills(skillRequests);
        skillRequests.forEach(skillRequest ->
                skillRequestRepository.create(skillRequest.getId(), skillRequest.getSkill().getId()));
        recommendationRequestRepository.save(recommendationRequestEntity);
        return recommendationRequestMapper.mapToDto(recommendationRequestEntity);
    }

    public List<RecommendationRequest> getRequests(RecommendationRequestFilterDto requestFilterDto) {
        Stream<RecommendationRequest> recommendationRequests = recommendationRequestRepository.findAll().stream();

        return recommendationRequestFilters.stream()
                .filter(filter -> filter.isApplicable(requestFilterDto))
                .flatMap(filter -> filter.apply(recommendationRequests, requestFilterDto))
                .toList();
    }
}







