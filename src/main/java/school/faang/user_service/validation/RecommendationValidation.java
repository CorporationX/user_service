package school.faang.user_service.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.RecommendationException;
import school.faang.user_service.exception.SkillException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecommendationValidation {
    private final RecommendationRepository recommendationRepository;
    private final SkillRepository skillRepository;
    private final RecommendationMapper recommendationMapper;
    private static final int DATE_LAST_RECOMMENDATION = 6;

    public void validateOfSkills(List<SkillOfferDto> skills) {
        if (skills == null || skills.isEmpty()) {
            throw new SkillException("You didn't specify skills");
        }
        List<Long> skillIds = skills.stream()
                .map(SkillOfferDto::getSkillId)
                .toList();
        if (skills.size() != skillRepository.countExisting(skillIds)) {
            throw new SkillException("Some skills are not found in the systems");
        }

    }
    public void validateOfLatestRecommendation(RecommendationDto recommendation) {
        Optional<Recommendation> lastRecommendation = recommendationRepository.
                findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendation.getAuthorId(),
                        recommendation.getReceiverId()
                );
        if (lastRecommendation.isPresent()) {
            LocalDateTime lastRecommendationDate = lastRecommendation.get().getCreatedAt();

            if (ChronoUnit.MONTHS.between(recommendation.getCreatedAt(),
                    lastRecommendationDate) > DATE_LAST_RECOMMENDATION) {
                    throw new RecommendationException("You have already given a recommendation to this " +
                        "user within the last 6 months");
            }
        }
    }
}
