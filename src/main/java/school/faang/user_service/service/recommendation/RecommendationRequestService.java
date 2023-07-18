package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        validateUser(recommendationRequestDto.getRequesterId());
        validateUser(recommendationRequestDto.getReceiverId());
        validateSkills(recommendationRequestDto);
        validateRequestTime(recommendationRequestDto);
        RecommendationRequest entity = recommendationRequestMapper.toEntity(recommendationRequestDto);
        return recommendationRequestMapper.toDto(recommendationRequestRepository.save(entity));
    }

    private void validateUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new DataValidationException("User " + userId + " does not exist!");
        }
    }

    private void validateSkills(RecommendationRequestDto recommendationRequestDto) {
        List<Long> skillIds = recommendationRequestDto.getSkillIds();
        for (Long skillId : skillIds) {
            if (!skillRepository.existsById(skillId)) {
                throw new DataValidationException("Skill " + skillId + " does not exist!");
            }
        }
    }

    private void validateRequestTime(RecommendationRequestDto recommendationRequestDto) {
        LocalDateTime createdAt = recommendationRequestDto.getCreatedAt();
        LocalDateTime nowTimeMinusSixMonths = LocalDateTime.now().minusMonths(6);
        if (!nowTimeMinusSixMonths.isAfter(createdAt)) {
            throw new DataValidationException("Recommendation can be requested once in six month!");
        }
    }
}
