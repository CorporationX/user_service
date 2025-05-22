package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.dto.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
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
    private final RecommendationMapper recommendationMapper;
    private final int MIN_RANGE_FOR_RECOMMENDATION = 6; // in months

    public RecommendationDto create(RecommendationDto recommendationDto) {
        LocalDateTime sixMothsAgo = LocalDateTime.now().minusMonths(MIN_RANGE_FOR_RECOMMENDATION);
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
        LocalDateTime sixMothsAgo = LocalDateTime.now().minusMonths(MIN_RANGE_FOR_RECOMMENDATION);
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

    public boolean delete(Long id) {
        if (recommendationRepository.existsById(id)) {
            recommendationRepository.deleteById(id);
            return true;
        }
        return false;
    }


    public Page<RecommendationDto> getAllUserRecommendations(Long receiverId, Pageable pageable) {
        return recommendationRepository.findAllByReceiverId(receiverId, pageable)
                .map(recommendationMapper::toDto);
    }

    public Page<RecommendationDto> getAllGivenRecommendations(long authorId, Pageable pageable) {
        return recommendationRepository.findAllByAuthorId(authorId, pageable)
                .map(recommendationMapper::toDto);
    }
}
