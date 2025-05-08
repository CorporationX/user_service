package school.faang.user_service.service;

import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;

@Service
public class RecommendationService {

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private SkillOfferRepository skillOfferRepository;

    public RecommendationDto create(RecommendationDto recommendationDto) throws DataValidationException {
        validateRecommendation(recommendationDto);

        Recommendation recommendation = new Recommendation();
        recommendation.setAuthorId(recommendationDto.getAuthorId());
        recommendation.setReceiverId(recommendationDto.getReceiverId());
        recommendation.setContent(recommendationDto.getContent());
        recommendation.setCreatedAt(LocalDateTime.now());
        recommendationRepository.create(recommendation);

        for (SkillOfferDto skillOfferDto : recommendationDto.getSkillOffers()) {
            SkillOffer skillOffer = new SkillOffer();
            skillOffer.setSkillId(skillOfferDto.getSkillId());
            skillOffer.setRecommendationId(recommendation.getId());

            skillOfferRepository.create(skillOffer);
        }
        return recommendationDto;
    }

    public RecommendationDto update(RecommendationDto recommendationDto) throws DataValidationException {
        validateRecommendation(recommendationDto);

        Recommendation recommendation = recommendationRepository.findById(recommendationDto.getId());
        recommendation.setContent(recommendationDto.getContent());
        recommendationRepository.update(recommendation);

        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());

        for (SkillOfferDto skillOfferDto : recommendationDto.getSkillOffers()) {
            SkillOffer skillOffer = new SkillOffer();
            skillOffer.setSkillId(skillOfferDto.getSkillId());
            skillOffer.setRecommendationId(recommendation.getId());


            skillOfferRepository.create(skillOffer);
        }

        return new RecommendationDto();
    }
    public void delete(Long id) {
        recommendationRepository.deleteById(id);
        skillOfferRepository.deleteAllByRecommendationId(id);
    }

    private void validateRecommendation(RecommendationDto recommendationDto) throws DataValidationException {
    }
}
