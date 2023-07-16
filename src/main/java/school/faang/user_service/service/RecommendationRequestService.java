package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;
@Service
@RequiredArgsConstructor
public class RecommendationRequestService {

    private final RecommendationRequestRepository recommendationRequestRepository;

    private final UserRepository userRepository;

    private final SkillRepository skillRepository;

    private final RecommendationRequestMapper recommendationRequestMapper;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        validationExistById(recommendationRequestDto.getRequesterId());
        validationExistById(recommendationRequestDto.getReceiverId());
        validationRequestDate(recommendationRequestDto);
        validationExistSkill(recommendationRequestDto);
        RecommendationRequest entity = recommendationRequestMapper.toEntity(recommendationRequestDto);
        return recommendationRequestMapper.toDto(recommendationRequestRepository.save(entity));
    }

    private void validationExistSkill(RecommendationRequestDto recommendationRequestDto) {
        for (SkillRequest skill : recommendationRequestDto.getSkills()) {
            if (!skillRepository.existsById(skill.getSkill().getId())) {
                throw new DataValidationException("Skill with id " + skill.getId() + " does not exist");
            }
        }
    }

    private static void validationRequestDate(RecommendationRequestDto recommendationRequestDto) {
        LocalDateTime dateNowMinusSixMonths = LocalDateTime.now().minusMonths(6);
        LocalDateTime createdAt = recommendationRequestDto.getCreatedAt();
        if (createdAt.isAfter(dateNowMinusSixMonths)) {
            throw new DataValidationException(
                    "A recommendation request from the same user to another can be sent no more than once every 6 months");
        }
    }

    private void validationExistById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new DataValidationException("User with id " + id + " does not exist");
        }
    }

}
