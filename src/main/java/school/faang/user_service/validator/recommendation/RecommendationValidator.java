package school.faang.user_service.validator.recommendation;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecommendationValidator {
    private final SkillRepository skillRepository;
    private final RecommendationRepository recommendationRepository;

    public void validateLastRecommendationToThisReceiverInterval(User author, User receiver) {

        Optional<LocalDateTime> lastRecommendationDate = author.getRecommendationsGiven().stream()
                .filter(recommendation -> recommendation.getReceiver().getId() == receiver.getId())
                .map(Recommendation::getCreatedAt)
                .max(LocalDateTime::compareTo);

        lastRecommendationDate.ifPresent(lastDate -> {
            LocalDateTime currentDate = LocalDateTime.now();
            LocalDateTime minimumIntervalDate = currentDate.minusMonths(6);

            if (!lastDate.isBefore(minimumIntervalDate)) {
                throw new DataValidationException(
                        "This recipient has already received a recommendation less than 6 months ago. " +
                                "You can write another one after this interval.");
            }
        });
    }

    public void validaIfSkillsFromOfferNotExist(RecommendationDto recommendationDto) {
        recommendationDto.getSkillOffers().forEach(skillOfferDto -> {
            if (skillRepository.findById(skillOfferDto.getSkillId()).isEmpty())
                throw new DataValidationException("Skill with id: " + skillOfferDto.getSkillId() + " does not exist!");
        });
    }

    public void checkIfRecommendationNotExist(long id) {
        recommendationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recommendation with id: " + id + " not found"));
    }

    public boolean checkIsGuarantor(Skill skill, long authorId) {
        return skill.getGuarantees().stream()
                .anyMatch(guarantee -> guarantee.getGuarantor().getId() == authorId);
    }

    public void validateAuthorAndReceiver(RecommendationDto recommendationDto, User author, User receiver) {

        validateLastRecommendationToThisReceiverInterval(
                author,
                receiver);

        validaIfSkillsFromOfferNotExist(recommendationDto);
    }
}
