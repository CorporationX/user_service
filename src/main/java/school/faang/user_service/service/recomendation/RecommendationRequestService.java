package school.faang.user_service.service.recomendation;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recomendation.FilterRecommendationRequestsDto;
import school.faang.user_service.dto.recomendation.RejectRecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.recomendation.request.RecommendationRequestNotFoundException;
import school.faang.user_service.exception.recomendation.request.RecommendationRequestRejectException;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.recomendation.filters.RecommendationRequestFilter;
import school.faang.user_service.validator.RecommendationRequestValidator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@Data
public class RecommendationRequestService {
    private final List<RecommendationRequestFilter> filters;
    private final SkillRequestRepository skillRequestRepository;
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestValidator validator;

    @Transactional
    public Long create(RecommendationRequest recommendationRequest, List<Long> skillIds) {
        validator.validateCreateRecommendationRequest(recommendationRequest, skillIds);

        recommendationRequest.setStatus(RequestStatus.PENDING);
        RecommendationRequest savedRequest = this.recommendationRequestRepository.save(
            recommendationRequest
        );

        skillIds.forEach((skillId) -> {
            this.skillRequestRepository.create(savedRequest.getId(), skillId);
        });

        return savedRequest.getId();
    }

    public List<RecommendationRequest> getRecommendationRequests(
            FilterRecommendationRequestsDto filterRecommendationRequestsDto
    ) {
        Stream<RecommendationRequest> requests = this.recommendationRequestRepository.findAll().stream();

        return this.filters.stream()
            .filter(filter -> filter.isApplicable(filterRecommendationRequestsDto))
            .reduce(requests,
                    (stream, filter) -> filter.apply(stream, filterRecommendationRequestsDto),
                    (s1, s2) -> s1)
            .toList();
    }

    public RecommendationRequest findRequestById(Long id) {
        return this.getRequestById(id);
    }

    @Transactional
    public RecommendationRequest rejectRequest(RejectRecommendationRequestDto rejection) {
        RecommendationRequest recommendationRequest = getRequestById(rejection.getId());

        if (recommendationRequest.getStatus() != RequestStatus.PENDING) {
            throw new RecommendationRequestRejectException();
        }

        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason(rejection.getReason());
        return this.recommendationRequestRepository.save(recommendationRequest);
    }

    private RecommendationRequest getRequestById(Long id) {
        Optional<RecommendationRequest> recommendationRequest =  this.recommendationRequestRepository.findById(id);

        if (recommendationRequest.isEmpty()) {
            throw new RecommendationRequestNotFoundException();
        }

        return recommendationRequest.get();
    }
}
