package school.faang.user_service.validator;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RecommendationValidator {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOffersRepository;

    public void validateData(RecommendationDto recommendationDto) {
        if (recommendationDto.getId() == null) {
            recommendationRepository
                    .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationDto.getAuthorId(), recommendationDto.getReceiverId())
                    .map(param -> param.getCreatedAt().plusMonths(6).isBefore(LocalDateTime.now())).ifPresent(p -> {
                        throw new DataValidationException("The author has recommended this user for 6 months");
                    });
        }
    }

    public void validateSkill(RecommendationDto recommendation) {
        List<SkillOfferDto> skillOffers = recommendation.getSkillOffers();
        for (SkillOfferDto skillOffer : skillOffers) {
            if (!skillOffersRepository.existsById(skillOffer.getId())) {
                throw new EntityNotFoundException(skillOffer.getId() + "doesn't exists");
            }
        }
    }
}
