package school.faang.user_service.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.exeption.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendationValidator;
import school.faang.user_service.validator.SkillValidator;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final RecommendationValidator recommendationValidator;
    private final SkillValidator skillValidator;


    @Transactional
    public RecommendationDto create(RecommendationDto recommendation) {
        validate(recommendation);
        long newRecommendationId = recommendationRepository.create(
                recommendation.getAuthorId(),
                recommendation.getReceiverId(),
                recommendation.getContent()
        );
        recommendation.setId(newRecommendationId);
        saveSkillOffer(recommendation);
        return recommendation;
    }

    private void validate(RecommendationDto recommendation) {
        recommendationValidator.validateRecommendation(recommendation);
        recommendationValidator.validateRecommendationTerm(recommendation);
        skillValidator.validateSkillOffersDto(recommendation);
    }

    @Transactional
    public void saveSkillOffer(RecommendationDto recommendation) {

        User user = userRepository.findById(recommendation.getReceiverId())
                .orElseThrow(() -> new DataValidationException("Receiver not found"));

        User userGuarantee = userRepository.findById(recommendation.getAuthorId())
                .orElseThrow(() -> new DataValidationException("Author not found"));

        for (SkillOfferDto skillOfferDto : recommendation.getSkillOffers()) {
            if (skillRepository.existsByTitleAndUsersContains(skillOfferDto.getSkill().getTitle(), user)) {
                Skill skill = skillRepository.findById(skillOfferDto.getSkill().getId())
                        .orElseThrow(() -> new DataValidationException("Skill not found"));
                UserSkillGuarantee userSkillGuarantee =
                        UserSkillGuarantee
                                .builder()
                                .user(user)
                                .skill(skill)
                                .guarantor(userGuarantee)
                                .build();

                if (!skill.getGuarantees().contains(userSkillGuarantee)) {
                    skill.getGuarantees().add(userSkillGuarantee);
                    skillRepository.save(skill);
                }
            }
            skillOfferRepository.create(skillOfferDto.getId(), skillOfferDto.getRecommendation().getId());
        }
    }
}
