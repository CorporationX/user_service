package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.dto.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
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


    public RecommendationDto create(RecommendationDto recommendationDto) {
        LocalDateTime sixMothsAgo = LocalDateTime.now().minusMonths(6);
        Optional<Recommendation> hasRecent = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc
                        (recommendationDto.getAuthorId(), recommendationDto.getReceiverId());

        if (hasRecent.isPresent() && hasRecent.get().getCreatedAt().isAfter(sixMothsAgo)) {
            throw new DataValidationException("You already gave a recommendation in the last 6 months.");
        }

        Long recommendationId = recommendationRepository.create(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent()
        );

        if (recommendationDto.getSkillOffers() != null) {
            for (SkillOfferDto offer : recommendationDto.getSkillOffers()) {
                Long skillId = offer.getSkillId();

                List<SkillOffer> previousOffers = skillOfferRepository
                        .findAllOffersOfSkill(skillId, recommendationDto.getReceiverId());

                boolean alreadyGuaranteed = previousOffers.stream()
                        .anyMatch(so -> so.getRecommendation().getAuthor().getId()
                                .equals(recommendationDto.getAuthorId()));
                if (!alreadyGuaranteed) {
                    skillOfferRepository.create(skillId, recommendationId);
                }
            }
        }


        recommendationDto.setId(recommendationId);
        recommendationDto.setCreatedAt(LocalDateTime.now());
        return recommendationDto;
    }

    public RecommendationDto update(RecommendationDto recommendationDto) {
        LocalDateTime sixMothsAgo = LocalDateTime.now().minusMonths(6);
        Optional<Recommendation> existing = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc
                        (recommendationDto.getAuthorId(), recommendationDto.getReceiverId());

        if (existing.isEmpty()) {
            throw new DataValidationException("No required recommendations found");
        }

        recommendationRepository.update(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent()
        );

        skillOfferRepository.deleteAllByRecommendationId(recommendationDto.getId());

        if (recommendationDto.getSkillOffers() != null) {
            for (SkillOfferDto offer : recommendationDto.getSkillOffers()) {
                Long skillId = offer.getSkillId();

                List<SkillOffer> offers = skillOfferRepository.findAllOffersOfSkill(
                        skillId, recommendationDto.getReceiverId());

                boolean alreadyGuaranteed = offers.stream()
                        .anyMatch(so -> so.getRecommendation().getAuthor().getId()
                                .equals(recommendationDto.getAuthorId()));

                if (!alreadyGuaranteed) {
                    skillOfferRepository.create(skillId, recommendationDto.getId());
                }
            }
        }
        return recommendationDto;
    }

    public void delete(Long id) {
        recommendationRepository.deleteById(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(Long receiverId) {
        return recommendationRepository.findAllByReceiverId(receiverId );
    }


}
