package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendations.RecommendationDto;
import school.faang.user_service.dto.recommendations.SkillOfferDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.recommendation.RecommendationValidator;

/**
 * @author Alexander Bulgakov
 */
@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final RecommendationMapper recommendationMapper;
    private final RecommendationValidator recommendationValidator;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    public RecommendationDto create(RecommendationDto recommendationDto) {
        recommendationValidator.checkRecommendation(recommendationDto);

        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        UserSkillGuarantee userSkillGuarantee = new UserSkillGuarantee();
        UserSkillGuarantee guarantor = getUserSkillGuarantee(recommendation.getAuthor().getId());

        for (SkillOffer skillOffer : recommendation.getSkillOffers()) {
            recommendationValidator.existsSkillOffer(skillOffer);
            skillOfferRepository.create(skillOffer.getId(), recommendation.getId());
            recommendationValidator.validateUserHaveSkill(skillOffer, recommendation);

            if (!skillOffer.getSkill().getGuarantees().contains(guarantor)) {
                userSkillGuarantee.setGuarantor(recommendation.getAuthor());
                skillOffer.getSkill().getGuarantees().add(userSkillGuarantee);
            }

            recommendation.addSkillOffer(skillOffer);
        }

        recommendationRepository.create(recommendation.getAuthor().getId(),
                recommendation.getReceiver().getId(), recommendation.getContent());

        return recommendationMapper.toDto(recommendation);
    }

    public UserSkillGuarantee getUserSkillGuarantee(long id) {
        return userSkillGuaranteeRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("..."));
    }

}
