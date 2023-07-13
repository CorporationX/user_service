package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendationValidator;

import java.util.ArrayList;
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
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    public RecommendationDto create(RecommendationDto recommendationDto) {
        validate(recommendationDto);

        long entityId = recommendationRepository.create(
                recommendationDto.getAuthorId(), recommendationDto.getReceiverId(), recommendationDto.getContent());
        saveSkillOffers(recommendationDto);
        Recommendation recommendation = recommendationRepository.findById(entityId)
                .orElseThrow();

        return recommendationMapper.toDto(recommendation);
    }

    // В случае успешной валидации входных данных в методе create сервиса recommendationService,
    // создается новая рекомендация в базе данных с помощью вызова recommendationRepository.create,
    // сохраняются предлагаемые навыки с помощью метода saveSkillOffers, и возвращается объект RecommendationDto с данными созданной рекомендации.
    //Сохранить предложенные в рекомендации скиллы в репозиторий SkillOfferRepository используя его метод create.
    // Если у пользователя, которому дают рекомендацию, такой скилл уже есть,
    // то добавить автора рекомендации гарантом к скиллу, который он предлагает, если этот автор еще не стоит там гарантом.

    private void saveSkillOffers(RecommendationDto recommendationDto) {
        long recommendationId = recommendationDto.getId();
        long authorId = recommendationDto.getAuthorId();
        long receiverId = recommendationDto.getReceiverId();
        List<SkillOfferDto> skills = recommendationDto.getSkillOffers();

        for (SkillOfferDto skill : skills) {
            long skillId = skill.getSkillId();
            Optional<Skill> userSkill = skillRepository.findUserSkill(skillId, receiverId);

            if (userSkill.isPresent() && !isGuaranteeExists(receiverId, skillId, authorId)) {
                userSkillGuaranteeRepository.create(receiverId, skillId, authorId);
            } else {
                skillOfferRepository.create(skillId, recommendationId);
            }
        }
    }

    private boolean isGuaranteeExists(long receiverId, long skillId, long authorId) {
        return userSkillGuaranteeRepository.isGuaranteeExists(receiverId, skillId, authorId);
    }

    private void validate(RecommendationDto recommendation) {
        recommendationValidator.validateLastUpdate(recommendation);
        recommendationValidator.validateSkills(recommendation);
    }
}
