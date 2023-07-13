package school.faang.user_service.validator;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecomendationDto;
import school.faang.user_service.dto.SkillOfferDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@AllArgsConstructor
public class RecommendatorValidator {

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOffersRepository;


    public void validateData(RecomendationDto recommendationDto) {
        if (recommendationDto.getId() == null) {
            recommendationRepository
                    .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationDto.getAuthorId(), recommendationDto.getReceiverId())
                    .map(param -> param.getCreatedAt().plusMonths(6).isBefore(LocalDateTime.now())).ifPresent(p -> {
                        throw new DataValidationException("The author has recommended this user for 6 months");
                    });
        }
    }

    public void validateSkill(RecomendationDto recommendation) {
        List<SkillOfferDto> skillOffers = recommendation.getSkillOffers();
        for (SkillOfferDto skillOffer : skillOffers) {
            if (!skillOffersRepository.existsById(skillOffer.getId())) {
                throw new DataValidationException(skillOffer.getId() + "doesn't exists");
            }
        }
    }
}

