package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.service.RecommendationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecommendationValidator {
    @Value("${recommendation.service.recommendation_period_in_month}")
    private int RECOMMENDATION_PERIOD_IN_MONTH;
    private final RecommendationRepository recommendationRepository;
    private final RecommendationService recommendationService;
    private final SkillRepository skillRepository;

    public void validateRecommendation(RecommendationDto recommendationDto) {
        if (recommendationDto.getContent() == null || recommendationDto.getContent().trim().isEmpty()) {
            throw new DataValidationException("Validation failed. The text cannot be empty.");
        }
    }

    public void validateId(long id) {
        if (!recommendationRepository.existsById(id)) {
            throw new DataValidationException("User with this id not found");
        }

        if (id <= 0) {
            throw new DataValidationException("Id is not correct.");
        }
    }

    public void validateUserAndAuthorIds(long userId, long authorId) {
        if (userId != authorId) {
            throw new DataValidationException("Only the author can update the recommendation.");
        }
    }

    public void validateAll(RecommendationDto recommendationDto) {
        List<SkillOfferDto> skillOffersDto = recommendationDto.getSkillOffers();

        validateLastUpdate(recommendationDto);
        validateSkills(skillOffersDto);
        validateSkillInRepository(skillOffersDto);
    }

    private void validateLastUpdate(RecommendationDto recommendationDto) {
        long authorId = recommendationDto.getAuthorId();
        long userId = recommendationDto.getReceiverId();

        Optional<Recommendation> lastRecommendation = recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, userId);

        if (lastRecommendation.isPresent()) {
            LocalDateTime lastUpdate = lastRecommendation.get().getUpdatedAt();
            LocalDateTime now = LocalDateTime.now();

            if (lastUpdate.plusMonths(RECOMMENDATION_PERIOD_IN_MONTH).isAfter(now)) {
                String errorMessage =
                        String.format("User with this ID %d cannot give recommendations yet, since %d months have not passed yet.",
                                userId, RECOMMENDATION_PERIOD_IN_MONTH);
                throw new DataValidationException(errorMessage);
            }
        }
    }

    private void validateSkills(List<SkillOfferDto> skillOfferDtos) {
        if (skillOfferDtos == null || skillOfferDtos.isEmpty()) {
            throw new DataValidationException("Skills null or empty.");
        }
    }

    private void validateSkillInRepository(List<SkillOfferDto> skills) {
        List<Long> skillIds = recommendationService.getUniqueSkillIds(skills);

        for (Long skillId : skillIds) {
            if (!skillRepository.existsById(skillId)) {
                throw new DataValidationException("Skill " + skillId + " doesnt exist in system.");
            }
        }
    }

    public void validateRecommendationForUpdate(RecommendationDto recommendationDto) {
        Long id = recommendationDto.getId();
        if (id == null) {
            throw new DataValidationException("Recommendation ID is null.");
        }
        recommendationRepository.findById(recommendationDto.getId())
                .orElseThrow(() -> new DataValidationException("Update is failed."));
    }
}