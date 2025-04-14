package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recomendation.RecommendationDto;
import school.faang.user_service.dto.recomendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationValidator {

    private static final Duration MIN_RECOMMENDATION_INTERVAL = Duration.ofDays(181);
    private final RecommendationRepository recommendationRepository;
    private final SkillRepository skillRepository;

    public boolean textAvailability(RecommendationDto recommendationDto) {
        if (!recommendationDto.getContent().isEmpty()) {
            return true;
        }
        throw new DataValidationException("В рецензии должен содержатся текст");
    }

    public void checkRecommendationInterval(RecommendationDto recommendationDto) {
        Optional<Recommendation> lastRecommendation = recommendationRepository.
                findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationDto.getAuthorId(),
                        recommendationDto.getReceiverId());
        if (lastRecommendation.isPresent()) {
            Duration interval = Duration.between(lastRecommendation.get().getCreatedAt(),
                    recommendationDto.getCreatedAt());
            if (interval.compareTo(MIN_RECOMMENDATION_INTERVAL) < 0) {
                throw new DataValidationException("Минимальный интервал между рекомендациями составляет "
                        + MIN_RECOMMENDATION_INTERVAL.toDays() + " дней.");
            }
        }
    }

    public boolean checkingSkills(RecommendationDto recommendationDto) {
        List<Long> recommendedSkills = recommendationDto.getSkillOffers().stream()
                .map(SkillOfferDto::getSkillId)
                .toList();
        List<Long> existingSkills = skillRepository.findAll().stream()
                .map(Skill::getId)
                .toList();
        if (existingSkills.containsAll(recommendedSkills)) {
            return true;
        } else {
            throw new DataValidationException("Skill не найден в базе данных");
        }
    }
}

