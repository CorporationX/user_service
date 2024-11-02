package school.faang.user_service.validator.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.skill.SkillOfferDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.recommendation.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class RecommendationValidator {
    private static final int MIN_MONTH_AFTER_LAST_RECOMMENDATION = 6;
    private final UserRepository userRepository;
    private final RecommendationRepository recommendationRepository;
    private final SkillRepository skillRepository;

    public User validateUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new DataValidationException("Пользователь с id " + userId + " не найден"));
    }

    public void checkDate(RecommendationDto recommendationDto) {
        Optional<Recommendation> recommendationOptional = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                        recommendationDto.getAuthorId(), recommendationDto.getReceiverId());
        checkDate(recommendationDto, recommendationOptional);
    }

    private void checkDate(RecommendationDto recommendationDto, Optional<Recommendation> recommendationOptional) {
        if (recommendationOptional.isEmpty()){ //рекомендация дается впервые
            return;
        }
        Recommendation lastRecommendation = recommendationOptional.get();
        if (ChronoUnit.MONTHS.between(
                lastRecommendation.getCreatedAt(),
                LocalDateTime.of(2050, 1, 1, 12, 0)) < MIN_MONTH_AFTER_LAST_RECOMMENDATION){
            throw new DataValidationException("Со времени последней рекомендации прошло меньше 6 месяцев");
        }
    }

    public void checkSkills(RecommendationDto recommendationDto) {
        for (SkillOfferDto skillOffer : recommendationDto.getSkillOffers()) {
            long skillId = skillOffer.getSkillId();
            if (!skillRepository.existsById(skillId)){
                throw new DataValidationException("Скилл с id " + skillId + " не существует");
            }
        }
    }
}
