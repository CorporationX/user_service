package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecommendationValidator {

    private static final int RECOMMENDATION_INTERVAL_MONTHS = 6;

    private final RecommendationRepository recommendationRepository;
    private final SkillRepository skillRepository;

    public void ValidateRecommendationContent(RecommendationDto recommendation) {
        String content = recommendation.getContent();

        if (content == null || content.isBlank()) {
            throw new DataValidationException("Content can't be empty");
        }
    }

    public void validateLastUpdate(RecommendationDto recommendation) {
        long authorId = recommendation.getAuthorId();
        long userId = recommendation.getReceiverId();
        Optional<Recommendation> lastRecommendation =
                recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, userId);

        if (lastRecommendation.isPresent()) {
            LocalDateTime lastUpdate = lastRecommendation.get().getUpdatedAt();
            LocalDateTime currentDate = LocalDateTime.now();

            if (lastUpdate.plusMonths(RECOMMENDATION_INTERVAL_MONTHS).isAfter(currentDate)) {
                throw new DataValidationException("You've already recommended this user in the last 6 months");
            }
        }
    }

    public void validateSkills(RecommendationDto recommendation) {
        List<SkillOfferDto> skills = recommendation.getSkillOffers();
        List<Long> skillIds = getUniqueSkillIds(skills);
        int countedSkills = skillRepository.countExisting(skillIds);

        if (skills.size() != countedSkills) {
            throw new DataValidationException("Invalid skills offered within the recommendation");
        }
    }

    private List<Long> getUniqueSkillIds(List<SkillOfferDto> skills) {
        return skills.stream()
                .map(SkillOfferDto::getSkillId)
                .distinct()
                .toList();
    }
}
