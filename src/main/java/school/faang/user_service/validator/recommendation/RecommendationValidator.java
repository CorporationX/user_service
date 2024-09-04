package school.faang.user_service.validator.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecommendationValidator {
    @Value("${recommendation.last-recommendation-limit-months}")
    private int lastRecommendationLimit;
    private final RecommendationRepository recommendationRepository;
    private final SkillRepository skillRepository;

    public void validateDateOfLastRecommendation(long authorId, long receiverId) {
        LocalDateTime currentDate = LocalDateTime.now();
        Optional<Recommendation> lastRecommendation = recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, receiverId);

        if (lastRecommendation.isPresent()) {
            LocalDateTime dateOfLastRecommendation = lastRecommendation.get().getUpdatedAt();

            if (ChronoUnit.MONTHS.between(dateOfLastRecommendation, currentDate) < lastRecommendationLimit) {
                String errorMessage = String.format("The author (ID : %d) cannot give a recommendation to a user (ID : %d) because it hasn't been %d months or more."
                        , authorId, receiverId, lastRecommendationLimit);
                log.error(errorMessage);
                throw new DataValidationException(errorMessage);
            }
        }
    }

    public void validateSkillOffers(RecommendationDto recommendationDto) {
        List<SkillOfferDto> skillOfferDtos = recommendationDto.getSkillOffers();
        if (skillOfferDtos == null || skillOfferDtos.isEmpty()) {
            return;
        }

        for (var skillOffer : skillOfferDtos) {
            if (!skillRepository.existsById(skillOffer.getSkillId())) {
                String errorMessage = String.format("The skill (ID : %d) doesn't exists in the system", skillOffer.getSkillId());
                log.error(errorMessage);
                throw new DataValidationException(errorMessage);
            }
        }
    }
}
