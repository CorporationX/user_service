package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendationValidator;

import java.util.List;


@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final RecommendationValidator recommendationValidator;

    public void create(RecommendationDto recommendation) {
        validate(recommendation);

        List<SkillOfferDto> skills = recommendation.getSkillOffers();
        long recommendationId = recommendation.getId();
        for (SkillOfferDto skill : skills) {
            skillOfferRepository.create(skill.getSkillId(), recommendationId);
        }
    }

    private void validate(RecommendationDto recommendation) {
        recommendationValidator.validateLastUpdate(recommendation);
    }

}
