package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataValidationException;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@AllArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOffersRepository;
    private final RecommendationMapper recommendationMapper;
    public void create(RecommendationDto recommendation) {
        Optional<Recommendation> recommendationByAuthorAndReceiver = recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendation.getAuthorId(), recommendation.getReceiverId());
        if (recommendationByAuthorAndReceiver.isPresent()) {
            validateRecommendation(recommendationMapper.toDto(recommendationByAuthorAndReceiver.get()));
        }

        saveSkillOffers(recommendation);
//        RecommendationDto createdRecommendation = recommendationRepository.create(recommendationDto);
//        return;
    }
    private void validateRecommendation(RecommendationDto recommendation) {
        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime minAllowedDate = LocalDateTime.from(currentDate.minusMonths(6));
        LocalDateTime lastRecommendationDate = recommendation.getCreatedAt();
        if (lastRecommendationDate != null && lastRecommendationDate.isAfter(minAllowedDate)) {
            throw new DataValidationException("The author has recommended this user for 6 months");
        }

        List<SkillOfferDto> skillOffers = recommendation.getSkillOffers();
        for (SkillOfferDto skillOffer : skillOffers) {
            if (!skillOffersRepository.existsById(skillOffer.getId())) {
                throw new DataValidationException(skillOffer.getId() + "doesn't exists");
            }

        }
    }
    private void saveSkillOffers(RecommendationDto recommendation) {
        List<SkillOfferDto> skillOffers = recommendation.getSkillOffers();
        for (SkillOfferDto skillOffer : skillOffers) {
            List<SkillOffer> allOffersOfSkill = skillOffersRepository.findAllOffersOfSkill(skillOffer.getSkillId(), skillOffer.getRecommendationId(), skillOffer.getId());
           if (allOffersOfSkill ){

           }
        }
    }
}

