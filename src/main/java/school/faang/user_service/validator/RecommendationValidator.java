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
                    .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationDto.getAuthorId(),
                            recommendationDto.getReceiverId())
                    .ifPresent(p -> {
                        if (p.getCreatedAt().plusMonths(6).isAfter(LocalDateTime.now()))
                            throw new DataValidationException("The author has recommended this user for 6 months");
                    });
        }
        validateSkill(recommendationDto);
    }

    private void validateSkill(RecommendationDto recommendation) {
        List<SkillOfferDto> skillOffers = recommendation.getSkillOffers();
        for (SkillOfferDto skillOffer : skillOffers) {
            if (!skillOffersRepository.existsById(skillOffer.getId())) {
                throw new EntityNotFoundException(skillOffer.getId() + "doesn't exists");
            }
        }
    }

    public void validateRecommendation(RecommendationDto recommendationDto) {
        if (recommendationDto == null) {
            throw new DataValidationException("RecommendationDto cannot be null");
        }
        if (recommendationDto.getContent() == null || recommendationDto.getContent().isBlank()) {
            throw new DataValidationException("Recommendation content cannot be empty");
        }
    }

    public void validateRecommendationDto(RecommendationDto recommendationDto) {
        if (recommendationDto == null) {
            throw new DataValidationException("RecommendationDto is null");
        }
        if (recommendationDto.getAuthorId() == null) {
            throw new DataValidationException("AuthorId is null");
        }
        if (recommendationDto.getReceiverId() == null) {
            throw new DataValidationException("ReceiverId is null");
        }
        if (recommendationDto.getContent() == null) {
            throw new DataValidationException("Content is null");
        }
    }
}
