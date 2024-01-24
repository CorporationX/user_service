package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.validator.mentorship.recommendation.RecommendationRequestValidator;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepo;
    private final SkillRequestRepository skillRequestRepo;
    private final RecommendationRequestMapper requestMapper;
    private final RecommendationRequestValidator validator;

    public RecommendationRequestDto create(RecommendationRequestDto requestDto) {
        validator.validate(requestDto);
        validator.validateSkills(requestDto);
        for (Skill skill : requestDto.getSkills()) {
            skillRequestRepo.create(requestDto.getRequesterId(), skill.getId());
        }
        return requestMapper.toDto(recommendationRequestRepo.create(requestDto.getRequesterId(),
                requestDto.getRecieverId(), requestDto.getMessage()));
    }
}
