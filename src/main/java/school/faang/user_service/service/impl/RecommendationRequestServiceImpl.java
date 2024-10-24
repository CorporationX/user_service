package school.faang.user_service.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.dto.RecommendationRequestDto;
import school.faang.user_service.model.event.RecommendationRequestedEvent;
import school.faang.user_service.model.filter_dto.RecommendationRequestFilterDto;
import school.faang.user_service.model.dto.RejectionDto;
import school.faang.user_service.model.enums.RequestStatus;
import school.faang.user_service.model.entity.RecommendationRequest;
import school.faang.user_service.model.entity.SkillRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.publisher.RecommendationRequestedEventPublisher;
import school.faang.user_service.repository.RecommendationRequestRepository;
import school.faang.user_service.repository.SkillRequestRepository;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilter;
import school.faang.user_service.service.RecommendationRequestService;
import school.faang.user_service.validator.RecommendationRequestValidator;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class RecommendationRequestServiceImpl implements RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final SkillRequestRepository skillRequestRepository;
    private final RecommendationRequestValidator recommendationRequestValidator;
    private final List<RecommendationRequestFilter> recommendationRequestFilters;
    private final RecommendationRequestedEventPublisher recommendationRequestedEventPublisher;

    @Transactional
    @Override
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
        RecommendationRequest recommendationRequest = recommendationRequestRepository.save(recommendationRequestEntity);
        recommendationRequestedEventPublisher.publish(new RecommendationRequestedEvent(
                recommendationRequestDto.getRequesterId(),
                recommendationRequestDto.getReceiverId(),
                recommendationRequest.getId()));
        return recommendationRequestMapper.mapToDto(recommendationRequest);
    }

    @Override
    public List<RecommendationRequestDto> getRequests(RecommendationRequestFilterDto requestFilterDto) {
        List<RecommendationRequest> recommendationRequests = recommendationRequestRepository.findAll();
        List<RecommendationRequest> recommendationRequestsEntity = recommendationRequestFilters.stream()
                .filter(filter -> filter.isApplicable(requestFilterDto))
                .reduce(recommendationRequests.stream(), (stream, filter) -> filter.apply(stream, requestFilterDto),
                        (s1, s2) -> s1)
                .toList();
        return recommendationRequestMapper.mapToDto(recommendationRequestsEntity);
    }

    @Override
    public RecommendationRequestDto getRequest(Long id) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("No such element in db"));
        return recommendationRequestMapper.mapToDto(recommendationRequest);
    }

    @Override
    public RejectionDto rejectRequest(Long id, RejectionDto rejectionDto) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("No such element in db"));
        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason(rejectionDto.getRejectionReason());
        recommendationRequestRepository.save(recommendationRequest);
        return recommendationRequestMapper.mapToRejectionDto(recommendationRequest);
    }
}







