package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataValidationException;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ValidatorRecommendation {
    public static final int RECOMMENDATION_PERIOD_IN_MONTH = 6;
    private final RecommendationRepository recommendationRepository;

    public void validateContent(RecommendationDto recommendation) {
        if (recommendation.getContent() == null || recommendation.getContent().isEmpty()) {
            throw new DataValidationException("Enter the text of the recommendation.");
        }
    }

    public void validationCheckLastGivenRecommendation(RecommendationDto recommendationDto) {
        recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationDto.getAuthorId(), recommendationDto.getReceiverId()
                ).filter(recommendationDate -> recommendationDate.getCreatedAt().plusMonths(RECOMMENDATION_PERIOD_IN_MONTH).isAfter(LocalDateTime.now()))
                .ifPresent(message -> {
                    throw new DataValidationException("The recommendation can be given no earlier than " + RECOMMENDATION_PERIOD_IN_MONTH + " months after the previous one");
                });
    }

    public void validateCheckingDuplicatedSkills(List<SkillOfferDto> skillOfferDtos){
        Set<Long> uniqueSkillId = new HashSet<>();
        for (SkillOfferDto skillOfferDto : skillOfferDtos){
            if (!uniqueSkillId.add(skillOfferDto.getId())){
                throw new DataValidationException("You can't give the same skill twice");
            }
        }

    }
}
