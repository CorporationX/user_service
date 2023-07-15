package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendationValidator;
import school.faang.user_service.validator.SkillOfferValidator;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final RecommendationValidator recommendationValidator;
    private final RecommendationMapper recommendationMapper;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final SkillOfferValidator skillOfferValidator;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    public RecommendationDto create(RecommendationDto recommendationDto) {
        validate(recommendationDto);

        long recommendationId = recommendationRepository.create(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent());

        processSkillOffers(recommendationDto);
        Recommendation recommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow();

        return recommendationMapper.toDto(recommendation);
    }

    public void processSkillOffers(RecommendationDto recommendationDto) {
        long recommendationId = recommendationDto.getId();
        long authorId = recommendationDto.getAuthorId();
        long receiverId = recommendationDto.getReceiverId();
        List<SkillOfferDto> skills = recommendationDto.getSkillOffers();

        for (SkillOfferDto skill : skills) {
            long skillId = skill.getSkillId();
            Optional<Skill> userSkill = skillRepository.findUserSkill(skillId, receiverId);

            if (userSkill.isPresent() && guaranteeNotExists(receiverId, skillId, authorId)) {
                saveSkillGuarantee(receiverId, skillId, authorId);
            } else {
                saveSkillOffer(skillId, recommendationId);
            }
        }
    }

    private boolean guaranteeNotExists(long receiverId, long skillId, long authorId) {
        return !userSkillGuaranteeRepository.isGuaranteeExists(receiverId, skillId, authorId);
    }

    private void saveSkillGuarantee(long receiverId, long skillId, long authorId) {
        userSkillGuaranteeRepository.create(receiverId, skillId, authorId);
    }

    private void saveSkillOffer(long skillId, long recommendationId) {
        skillOfferRepository.create(skillId, recommendationId);
    }

    private void validate(RecommendationDto recommendation) {
        List<SkillOfferDto> skills = recommendation.getSkillOffers();

        recommendationValidator.validateLastUpdate(recommendation);
        skillOfferValidator.validateSkillsListNotEmptyOrNull(skills);
        skillOfferValidator.validateSkillsAreInRepository(skills);
    }
}
