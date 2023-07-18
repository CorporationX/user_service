package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserSkillGuaranteeDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.UserSkillGuaranteeMapper;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
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
    private final UserRepository userRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final UserSkillGuaranteeMapper userSkillGuaranteeMapper;

    @Transactional
    public RecommendationDto create(RecommendationDto recommendationDto) {
        validate(recommendationDto);

        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        recommendationRepository.save(recommendation);
        processSkillOffers(recommendation);

        return recommendationMapper.toDto(recommendation);
    }

    @Transactional
    public RecommendationDto update(RecommendationDto recommendationDto) {
        recommendationValidator.validateRecommendationExist(recommendationDto);
        validate(recommendationDto);

        delete(recommendationDto.getId());
        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        recommendationRepository.save(recommendation);
        processSkillOffers(recommendation);

        return recommendationMapper.toDto(recommendation);
    }

    @Transactional
    public void delete(long recommendationId) {
        recommendationRepository.deleteById(recommendationId);
    }


    private void processSkillOffers(Recommendation recommendation) {
        long userId = recommendation.getReceiver().getId();
        long authorId = recommendation.getAuthor().getId();
        List<SkillOffer> skillOffers = recommendation.getSkillOffers();
        List<Skill> userSkills = getUserSkills(userId);

        for (SkillOffer skillOffer : skillOffers) {
            long skillId = skillOffer.getSkill().getId();

            if (userSkills.contains(skillOffer.getSkill()) && guaranteeNotExists(userId, skillId, authorId)) {
                saveUserSkillGuarantee(userId, skillId, authorId);
            } else {
                skillOfferRepository.save(skillOffer);
            }
        }
    }

    private List<Skill> getUserSkills(long userId) {
        Optional<User> user = userRepository.findById(userId);

        return user.map(User::getSkills)
                .orElse(Collections.emptyList());
    }

    private void saveUserSkillGuarantee(long userId, long skillId, long guarantorId) {
        UserSkillGuaranteeDto guaranteeDto = userSkillGuaranteeMapper.toDto(userId, skillId, guarantorId);
        UserSkillGuarantee guarantee = userSkillGuaranteeMapper.toEntity(guaranteeDto);
        userSkillGuaranteeRepository.save(guarantee);
    }

    private boolean guaranteeNotExists(long userId, long skillId, long guarantorId) {
        return !userSkillGuaranteeRepository.existsByUserIdAndSkillIdAndGuarantorId(userId, skillId, guarantorId);
    }

    private void validate(RecommendationDto recommendation) {
        List<SkillOfferDto> skills = recommendation.getSkillOffers();

        recommendationValidator.validateLastUpdate(recommendation);
        skillOfferValidator.validateSkillsListNotEmptyOrNull(skills);
        skillOfferValidator.validateSkillsAreInRepository(skills);
    }
}
