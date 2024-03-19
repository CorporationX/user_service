package school.faang.user_service.service.recomendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RecommendationRequestEvent;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.RejectFailException;
import school.faang.user_service.filter.RecommendationRequestFilter;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.publisher.RecommendationRequestedEventPublisher;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {

    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final SkillRepository skillRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final UserService userService;
    private final List<RecommendationRequestFilter> recommendationRequestFilterList;
    private final RecommendationRequestedEventPublisher recommendationRequestedEventPublisher;

    @Transactional
    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequest) {
        validate(recommendationRequest);

        recommendationRequest.getSkills()
                .forEach(skillRequestId -> {
                    if (!skillRepository.existsById(skillRequestId.getSkillId()))
                        throw new DataValidationException("Skills not found");
                });

        recommendationRequest.getSkills()
                .forEach(skillRequestId -> skillRequestRepository.create(skillRequestId.getId(),
                        skillRequestId.getSkillId()));

        recommendationRequestRepository
                .create(recommendationRequest.getRequesterId(),
                        recommendationRequest.getReceiverId(),
                        recommendationRequest.getMessage());

        RecommendationRequestEvent recommendationRequestEvent = new RecommendationRequestEvent(
                recommendationRequest.getId(),
                recommendationRequest.getRequesterId(),
                recommendationRequest.getReceiverId(),
                LocalDateTime.now());
        recommendationRequestedEventPublisher.publish(recommendationRequestEvent);

        return recommendationRequest;
    }

    public RecommendationRequestDto getRequest(long id) {
        return recommendationRequestMapper
                .toDto(findRequestById(id)
                        .orElseThrow(() -> new DataValidationException("Request not found by id: " + id)));
    }

    public List<RecommendationRequestDto> getRequest(RequestFilterDto filter) {
        Stream<RecommendationRequest> recommendationRequests = recommendationRequestRepository.findAll().stream();
        for (RecommendationRequestFilter recommendationRequestFilter : recommendationRequestFilterList) {
            if (recommendationRequestFilter.isApplicable(filter))
                recommendationRequests = recommendationRequestFilter.apply(recommendationRequests, filter);
        }
        List<RecommendationRequestDto> recommendationRequestDtos = new ArrayList<>();
        for (RecommendationRequest requests : recommendationRequests.toList()) {
            recommendationRequestDtos.add(recommendationRequestMapper.toDto(requests));
        }
        return recommendationRequestDtos;
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        RecommendationRequest request = findRequestById(id).orElseThrow(() ->
                new DataValidationException("Request not found by id: " + id));
        if (request.getStatus().equals(RequestStatus.PENDING)) {
            request.setRejectionReason(rejection.getReason());
            request.setStatus(RequestStatus.REJECTED);
            recommendationRequestRepository.save(request);
        } else {
            throw new RejectFailException("Request is accepted or rejected");
        }
        return recommendationRequestMapper.toDto(request);
    }

    private Optional<RecommendationRequest> findRequestById(long id) {
        return recommendationRequestRepository.findById(id);
    }

    private void validate(RecommendationRequestDto recommendationRequest) {
        if (!userService.existById((recommendationRequest.getRequesterId())) ||
                !userService.existById(recommendationRequest.getReceiverId()))
            throw new DataValidationException("User not found");
        if (recommendationRequest.getSkills() == null)
            throw new DataValidationException("skills is null");
        if (recommendationRequest.getUpdatedAt().isAfter(LocalDateTime.now().minusMonths(6)))
            throw new DataValidationException("early request");
    }
}

