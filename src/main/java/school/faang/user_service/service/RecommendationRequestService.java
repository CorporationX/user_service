package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.validator.RecommendationRequestValidator;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final RecommendationRequestValidator recommendationRequestValidator;

    public RecommendationRequestDto create (RecommendationRequestDto recommendationRequest) {

        recommendationRequestValidator.validateUsersExist(recommendationRequest);
        recommendationRequestValidator.validateSkillsExist(recommendationRequest);
        recommendationRequestValidator.validateRequestPeriod(recommendationRequest);

        RecommendationRequest createdRequest = recommendationRequestRepository.create(recommendationRequest.getRequesterId(), recommendationRequest.getReceiverId(), recommendationRequest.getMessage());
        createdRequest.getSkills().forEach(skill -> skillRequestRepository.create(recommendationRequest.getRequesterId(), skill.getId()));

        return recommendationRequestMapper.toDto(createdRequest);
    }

    public RecommendationRequestDto getRequest(long id) {
        RecommendationRequest request = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> {
                    throw new EntityNotFoundException("Recommendation does not exist");
                });
        return recommendationRequestMapper.toDto(request);
    }
}