package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.skill.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecommendationValidator {

    private final RecommendationRepository recommendationRepository;
    private final SkillRepository skillRepository;
    private static final int MONTHS_LAST_RECOMMENDATION = 6;

    public void validateToCreate(RecommendationDto recommendationDto) {
        getByAuthorIdAndReceiverId(recommendationDto)
                .ifPresent(recommendation -> validateRecommendationTerm(recommendation, recommendationDto));
        validateSkillOffersDto(recommendationDto);
    }

    public void validateToUpdate(RecommendationDto recommendationDto) {
        Recommendation recommendation = getByAuthorIdAndReceiverId(recommendationDto)
                .orElseThrow(() -> new DataValidationException("The author has not given a recommendation to this user"));
        validateRecommendationTerm(recommendation, recommendationDto);
        validateSkillOffersDto(recommendationDto);
    }

    private void validateRecommendationTerm(Recommendation recommendation, RecommendationDto recommendationDto) {
        LocalDateTime lastRecommendation = recommendation.getCreatedAt();
        LocalDateTime recommendationTerm = recommendationDto.getCreatedAt();

        if (recommendationTerm.isBefore(lastRecommendation.plusMonths(MONTHS_LAST_RECOMMENDATION))) {
            throw new DataValidationException("The author has already recommended this user in the last 6 months.");
        }
    }

    private void validateSkillOffersDto(RecommendationDto recommendationDto) {
        if (recommendationDto.getSkillOffers() != null || !recommendationDto.getSkillOffers().isEmpty()) {
            recommendationDto.getSkillOffers().stream().
                    map(SkillOfferDto::getSkill)
                    .forEach(skillId -> skillRepository.findById(skillId)
                            .orElseThrow(() -> new DataValidationException(
                                    "One or more suggested skills do not exist in the system.")));
        }
    }

    private Optional<Recommendation> getByAuthorIdAndReceiverId(RecommendationDto recommendationDto) {
        return recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId());
    }
}
