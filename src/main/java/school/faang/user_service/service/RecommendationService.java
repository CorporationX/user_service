package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataValidationException;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.ValidatorRecommendation;

@RestController
@RequiredArgsConstructor
public class RecommendationService {

    private final ValidatorRecommendation validatorRecommendation;
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final RecommendationMapper recommendationMapper;
    private final UserSkillGuarantee userSkillGuarantee;

    public RecommendationDto create(RecommendationDto recommendationDto) {
        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);

        userSkillGuarantee.setGuarantor(recommendation.getAuthor());

        validatorRecommendation.validationCheckLastGivenRecommendation(recommendationDto);

        validatorRecommendation.validateCheckingDuplicatedSkills(recommendationDto.getSkillOffers());

        for (SkillOffer skillOffer : recommendation.getSkillOffers()) {
            if (checkSkillUserInRepository(skillOffer)) {
                if (checkGarantorAuthorToSkill(recommendationDto)) {
                    throw new DataValidationException("You are already a guarantor for the proposed skill" + skillOffer.getId());
                } else {
                    userSkillGuaranteeRepository.save(userSkillGuarantee);
                }
            } else {
                skillOfferRepository.create(getSkillOffer(skillOffer).getId(), recommendation.getId());
            }
            recommendation.addSkillOffer(skillOffer);
        }
        recommendationRepository.create(recommendation.getAuthor().getId(),
                recommendation.getReceiver().getId(), recommendation.getContent());

        return recommendationMapper.toDto(recommendation);
    }

    private SkillOffer getSkillOffer(SkillOffer skillOffer) {
        return skillOfferRepository.findById(skillOffer.getId())
                .orElseThrow(() -> new DataValidationException("No skill offer with id " + skillOffer.getId() + " found"));
    }

    private boolean checkSkillUserInRepository(SkillOffer skillOffer) {
        return skillRepository.existsById(skillOffer.getId());
    }

    private boolean checkGarantorAuthorToSkill(RecommendationDto recommendationDto) {
        userSkillGuaranteeRepository.findById(recommendationDto.getAuthorId());
        return true;
    }
}
