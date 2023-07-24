package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.validator.RecommendationRequestValidator;
import school.faang.user_service.validator.SkillValidator;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {

    private final RecommendationRequestRepository recommendationRequestRepository;

    private final RecommendationRequestMapper recommendationRequestMapper;

    private final RecommendationRequestValidator recommendationRequestValidator;

    private final SkillValidator skillValidator;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        recommendationRequestValidator.validationExistById(recommendationRequestDto.getRequesterId());
        recommendationRequestValidator.validationExistById(recommendationRequestDto.getReceiverId());
        recommendationRequestValidator.validationRequestDate(recommendationRequestDto);
        skillValidator.validationExistSkill(recommendationRequestDto);
        RecommendationRequest entity = recommendationRequestMapper.toEntity(recommendationRequestDto);
        return recommendationRequestMapper.toDto(recommendationRequestRepository.save(entity));
    }
}
