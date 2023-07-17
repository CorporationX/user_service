package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.mapper.recommendation.SkillOfferMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendationValidator;
import school.faang.user_service.validator.SkillOfferValidator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final RecommendationValidator recommendationValidator;
    private final RecommendationMapper recommendationMapper;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillOfferValidator skillOfferValidator;
    private final SkillOfferMapper skillOfferMapper;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    public RecommendationDto create(RecommendationDto recommendationDto) {
        validate(recommendationDto);

        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        recommendationRepository.save(recommendation);
        processSkillOffers(recommendationDto);

        return recommendationMapper.toDto(recommendation);
    }

    private void processSkillOffers(RecommendationDto recommendationDto) {
        Long userId = recommendationDto.getReceiverId();
        Long guaranteeId = recommendationDto.getAuthorId();

        List<SkillOfferDto> skillOffers = recommendationDto.getSkillOffers();
        List<Skill> userSkillIds = getUserSkills(userId);

        for (SkillOfferDto skillOfferDto : skillOffers) {
            Long skillId = skillOfferDto.getSkillId();

            for (Skill userSkillId : userSkillIds) {
                if (skillId.equals(userSkillId.getId())) {

                } else {
                    saveSkillOffer(skillOfferDto);
                }
            }
        }
    }


    private List<Skill> getUserSkills(long userId) {
        Optional<User> user = userRepository.findById(userId);

        return user.map(User::getSkills)
                .orElse(Collections.emptyList());
    }

    private List<UserSkillGuarantee> getUserGuarantee(long skillId){
        Optional<Skill> skill = skillRepository.findById(skillId);

        return skill.map(Skill:: getGuarantees)
                .orElse(Collections.emptyList());
    }

    private void saveSkillOffer(SkillOfferDto skillOfferDto) {
        skillOfferRepository.save(skillOfferMapper.toEntity(skillOfferDto));
    }


    private void validate(RecommendationDto recommendation) {
        List<SkillOfferDto> skills = recommendation.getSkillOffers();

        recommendationValidator.validateLastUpdate(recommendation);
        skillOfferValidator.validateSkillsListNotEmptyOrNull(skills);
        skillOfferValidator.validateSkillsAreInRepository(skills);
    }
}
