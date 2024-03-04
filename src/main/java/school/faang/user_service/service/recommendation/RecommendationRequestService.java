package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.validator.recommendation.RecommendationRequestValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final RecommendationRequestValidator recommendationRequestValidator;
    private final RecommendationRequestMapper recommendationRequestMapper;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        recommendationRequestValidator.validate(recommendationRequestDto);

        RecommendationRequest entity = recommendationRequestMapper.toEntity(recommendationRequestDto);

        RecommendationRequest request = recommendationRequestRepository.save(entity);

        List<Long> skills = recommendationRequestDto.getSkillIds();

        if (skills != null) {
            skills.forEach(skillId -> {
                skillRequestRepository.create(request.getId(), skillId);
            });
        }

        return recommendationRequestMapper.toDto(request);
    }
}
