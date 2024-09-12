package school.faang.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class RecommendationService {
    @Autowired
    private RecommendationRepository recommendationRepository;
    @Autowired
    private SkillOfferRepository skillOfferRepository;

    public RecommendationDto create(RecommendationDto recommendation) {
        validateRecommendationTiming(recommendation);

        validateSkills(recommendation.getSkillOffers());

        saveSkillOffers(recommendation);

        Long recommendationId = recommendationRepository.create(
                recommendation.getAuthorId(),
                recommendation.getReceiverId(),
                recommendation.getContent()
        );

        RecommendationDto savedRecommendation = new RecommendationDto();
        savedRecommendation.setId(recommendationId);
        savedRecommendation.setAuthorId(recommendation.getAuthorId());
        savedRecommendation.setReceiverId(recommendation.getReceiverId());
        savedRecommendation.setContent(recommendation.getContent());
        savedRecommendation.setSkillOffers(recommendation.getSkillOffers());
        savedRecommendation.setCreatedAt(LocalDateTime.now());

        return savedRecommendation;
    }

    private void validateRecommendationTiming(RecommendationDto recommendationDto) {
        Optional<Recommendation> recentRecommendation = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                        recommendationDto.getAuthorId(),
                        recommendationDto.getReceiverId()
                );

        if (recentRecommendation.isPresent()) {
            LocalDateTime lastRecommendationDate = recentRecommendation.get().getCreatedAt();
            if (lastRecommendationDate != null && lastRecommendationDate.isAfter(LocalDateTime.now().minusMonths(6))) {
                throw new DataValidationException("You cannot give a recommendation to this user more than once within 6 months.");
            }
        }
    }

    private void validateSkills(List<SkillOfferDto> skillOffers) {
        Set<Long> skillIds = new HashSet<>();
        for (SkillOfferDto skillOffer : skillOffers) {
            if (skillOffer.getSkillId() == null) {
                throw new DataValidationException("Skill ID must not be null.");
            }

            if (!skillOfferRepository.existsById(skillOffer.getSkillId())) {
                throw new DataValidationException("Skill with ID " + skillOffer.getSkillId() + " does not exist.");
            }

            if (!skillIds.add(skillOffer.getSkillId())) {
                throw new DataValidationException("Duplicate skill ID detected: " + skillOffer.getSkillId());
            }
        }
    }

    private void saveSkillOffers(RecommendationDto recommendationDto) {
        for (SkillOfferDto skillOffer : recommendationDto.getSkillOffers()) {
            List<SkillOffer> existingOffers = skillOfferRepository.findAllOffersOfSkill(
                    skillOffer.getSkillId(), recommendationDto.getReceiverId()
            );

            if (existingOffers.isEmpty()) {
                skillOfferRepository.create(skillOffer.getSkillId(), recommendationDto.getId());
            } else {
//                TO-DO: create addGuarantor method
            }
        }
    }
}
