package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;

    public void create(RecommendationDto recommendation) {



    }

    private void recommendationAuthorLastUpdateValidator(RecommendationDto recommendation) {
        long authorId = recommendation.getAuthorId();
        long userId = recommendation.getReceiverId();

        Optional<Recommendation> lastRecommendation = recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, userId);
        if (lastRecommendation.isPresent()) {
            LocalDateTime lastUpdate = lastRecommendation.get().getUpdatedAt();
            LocalDateTime currentDate = LocalDateTime.now();
            if (lastUpdate.plusMonths(6).isAfter(currentDate)) {
                throw new DataValidationException("You've already recommended this pearson");
            }
        }
    }

    private void existingSkillsCheckValidator(RecommendationDto recommendation) {
        long userId = recommendation.getReceiverId();

        List<SkillOfferDto> skillsOffersDto = recommendation.getSkillOffers();
        boolean skillDontMatch = skillsOffersDto.stream()
                .anyMatch(s -> !skillRepository.existsByTitle(s.getSkill().getTitle()));

        if (skillDontMatch) {
            throw new DataValidationException("Skills don't match");
        }
    }
}
