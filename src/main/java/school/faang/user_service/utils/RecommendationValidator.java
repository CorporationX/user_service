package school.faang.user_service.utils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecommendationValidator {
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    @Transactional
    public void validate(Recommendation recommendation) {
        if (recommendation == null) {
            throw new DataValidationException("Recommendation cannot be empty");
        }

        if (StringUtils.isBlank(recommendation.getContent())) {
            throw new DataValidationException("Recommendation content cannot be empty");
        }

        if (Optional.ofNullable(recommendation.getAuthor()).map(User::getId).orElse(null) == null) {
            throw new DataValidationException("Author cannot be empty");
        }

        if (Optional.ofNullable(recommendation.getReceiver()).map(User::getId).orElse(null) == null) {
            throw new DataValidationException("Receiver id cannot be empty");
        }

        if (recommendation.getSkillOffers() == null) {
            throw new DataValidationException("Skill offers cannot be empty");
        }

        if (recommendation.getCreatedAt() == null) {
            throw new DataValidationException("Created date cannot be empty");
        }

        if (!userRepository.existsById(recommendation.getReceiver().getId())) {
            throw new DataValidationException("Receiver not found");
        }

        Optional<User> author = userRepository.findById(recommendation.getAuthor().getId());

        if (author.isEmpty()) {
            throw new DataValidationException("Author not found");
        }

        List<Recommendation> recommendationsToReceiver = author.get().getRecommendationsGiven().stream()
                .filter(recommendationGiven -> recommendationGiven.getReceiver().getId() == recommendation.getReceiver().getId())
                .sorted(Comparator.comparing(Recommendation::getCreatedAt).reversed()).toList();

        if (!recommendationsToReceiver.isEmpty()) {
            Recommendation lastRecommendation = recommendationsToReceiver.get(0);
            LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);

            if (lastRecommendation.getCreatedAt().isAfter(sixMonthsAgo)) {
                throw new DataValidationException("The last recommendation was less than 6 months ago");
            }
        }

        List<SkillOffer> skillOffers = recommendation.getSkillOffers();

        if (!skillOffers.isEmpty()) {
            List<Long> skillIds = skillOffers.stream()
                    .map(SkillOffer::getSkill)
                    .map(Skill::getId)
                    .distinct()
                    .toList();

            List<Boolean> notExistedSkills = skillIds.stream()
                    .map(skillRepository::existsById)
                    .filter(hasSkill -> !hasSkill)
                    .toList();

            if (!notExistedSkills.isEmpty()) {
                throw new DataValidationException("Skill not found");
            }
        }
    }
}
