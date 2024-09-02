package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.validator.recommendation.RecommendationRequestValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestMapper requestMapper;
    private final RecommendationRequestValidator requestValidator;
    private final SkillRequestRepository skillRequestRepository;
    private final RecommendationRequestRepository recommendationRequestRepository;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        requestValidator.validateRecommendationRequest(recommendationRequestDto);

        RecommendationRequest recommendationRequest = requestMapper.toEntity(recommendationRequestDto);
        RecommendationRequest savedRecommendationRequest = recommendationRequestRepository.save(recommendationRequest);

        saveSkillRequestsByNewRecommendation(recommendationRequestDto, savedRecommendationRequest);

        return requestMapper.toDto(savedRecommendationRequest);
    }

    private void saveSkillRequestsByNewRecommendation(RecommendationRequestDto recommendationRequestDto, RecommendationRequest savedRecommendationRequest) {
        List<Long> skillRequests = recommendationRequestDto.getSkillsId();

        skillRequests
                .forEach(skillRequestId -> skillRequestRepository
                        .create(savedRecommendationRequest.getId(), skillRequestId));
    }
}
