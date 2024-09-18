package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recomendation.RecommendationRequestDto;
import school.faang.user_service.dto.recomendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recomendation.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.recomendation.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.recommendation.filters.RecommendationRequestFilter;
import school.faang.user_service.validator.recommendation.RecommendationRequestValidator;

import java.util.List;
import java.util.NoSuchElementException;
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
                skillRequestRepository.create((int) skillRequest.getId(), (int) skillRequest.getSkill().getId()));
        recommendationRequestRepository.save(recommendationRequestEntity);
        return recommendationRequestMapper.mapToDto(recommendationRequestEntity);
    }

    public List<RecommendationRequestDto> getRequests(RecommendationRequestFilterDto requestFilterDto) {
        List<RecommendationRequest> recommendationRequests = recommendationRequestRepository.findAll();
        List<RecommendationRequest> recommendationRequestsEntity = recommendationRequestFilters.stream()
                .filter(filter -> filter.isApplicable(requestFilterDto))
                .reduce(recommendationRequests.stream(), (stream, filter) -> filter.apply(stream, requestFilterDto),
                        (s1, s2) -> s1)
                .toList();
        return recommendationRequestMapper.mapToDto(recommendationRequestsEntity);
    }

    public RecommendationRequestDto getRequest(Long id) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("No such element in db"));
        return recommendationRequestMapper.mapToDto(recommendationRequest);
    }

    public RejectionDto rejectRequest(Long id, RejectionDto rejectionDto) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("No such element in db"));
        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason(rejectionDto.getRejectionReason());
        recommendationRequestRepository.save(recommendationRequest);
        return recommendationRequestMapper.mapToRejectionDto(recommendationRequest);
    }
}







