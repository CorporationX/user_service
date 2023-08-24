package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilter;
import java.util.List;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.validator.RecommendationRequestValidator;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final List<RecommendationRequestFilter> recommendationRequestFilters;
    private final SkillRequestRepository skillRequestRepository;
    private final RecommendationRequestValidator recommendationRequestValidator;

    public RecommendationRequestDto create (RecommendationRequestDto recommendationRequest) {

        recommendationRequestValidator.validateUsersExist(recommendationRequest);
        recommendationRequestValidator.validateSkillsExist(recommendationRequest);
        recommendationRequestValidator.validateRequestPeriod(recommendationRequest);

        RecommendationRequest createdRequest = recommendationRequestRepository.create(recommendationRequest.getRequesterId(), recommendationRequest.getReceiverId(), recommendationRequest.getMessage());
        createdRequest.getSkills().forEach(skill -> skillRequestRepository.create(recommendationRequest.getRequesterId(), skill.getId()));

        return recommendationRequestMapper.toDto(createdRequest);
    }
  
    public List<RecommendationRequestDto> getRequests(RequestFilterDto filter) {
        List<RecommendationRequest> recommendationRequests = (List<RecommendationRequest>) recommendationRequestRepository.findAll();

        for (RecommendationRequestFilter recommendationRequestFilter : recommendationRequestFilters) {
            if (recommendationRequestFilter.isApplicable(filter)) {
                recommendationRequests = recommendationRequestFilter.apply(recommendationRequests.stream(), filter).toList();
            }
        }
        return recommendationRequests.stream().map(recommendationRequestMapper::toDto).toList();
    }
      
    public RecommendationRequestDto getRequest(long id) {
        RecommendationRequest request = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> {
                    throw new EntityNotFoundException("Recommendation does not exist");
                });
        return recommendationRequestMapper.toDto(request);
    }
  
    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recommendation Request does not exist"));

        if (recommendationRequest.getStatus().equals(RequestStatus.PENDING)) {
            recommendationRequest.setStatus(RequestStatus.REJECTED);
            recommendationRequest.setRejectionReason(rejection.getReason());
            recommendationRequestRepository.save(recommendationRequest);

            return recommendationRequestMapper.toDto(recommendationRequest);
        } else {
            throw new EntityNotFoundException("The request has already been accepted or rejected");
        }
  }
}
