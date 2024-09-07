package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.dto.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendationDtoValidator;
import school.faang.user_service.validator.SkillInDbValidator;
import school.faang.user_service.validator.UserInDbValidator;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final SkillRepository skillRepository;

    private final RecommendationDtoValidator recommendationDtoValidator;
    private final UserInDbValidator userInDbValidator;
    private final SkillInDbValidator skillInDbValidator;

    private final RecommendationMapper recommendationMapper;

    public RecommendationDto create(RecommendationDto recommendation) {
        validateRecommendationDto(recommendation);
        List<Skill> skills = skillInDbValidator.getSkillsFromDb(recommendation);
        User userReceiver = userInDbValidator.checkIfUserInDbIsEmpty(recommendation.getReceiverId());
        User userAuthor = userInDbValidator.checkIfUserInDbIsEmpty(recommendation.getAuthorId());

        Long recommendationId = recommendationRepository
                .create(userAuthor.getId(), userReceiver.getId(), recommendation.getContent());

        Recommendation existedRecommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new DataValidationException("Такой рекомендации нет в БД"));

        saveSkillOffersInDb(existedRecommendation, userReceiver, userAuthor, skills);

        return recommendationMapper.toDto(existedRecommendation);
    }

    public RecommendationDto update(RecommendationDto recommendation) {
        validateRecommendationDto(recommendation);
        User userReceiver = userInDbValidator.checkIfUserInDbIsEmpty(recommendation.getReceiverId());
        User userAuthor = userInDbValidator.checkIfUserInDbIsEmpty(recommendation.getAuthorId());
        List<Skill> skills = skillInDbValidator.getSkillsFromDb(recommendation);

        recommendationRepository.update(userAuthor.getId(), userReceiver.getId(), recommendation.getContent());

        Recommendation existedRecommendation = recommendationRepository.findById(recommendation.getId())
                .orElseThrow(() -> new DataValidationException("Такой рекомендации нет в БД"));

        skillOfferRepository.deleteAllByRecommendationId(existedRecommendation.getId());

        saveSkillOffersInDb(existedRecommendation, userReceiver, userAuthor, skills);

        return recommendationMapper.toDto(existedRecommendation);
    }

    public void delete(long id) {
        recommendationRepository.deleteById(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long recieverId) {
        return recommendationMapper.recommendationsToRecommendationsDtos(
                 recommendationRepository.findAllByReceiverId(recieverId, Pageable.unpaged()).getContent());
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        return recommendationMapper.recommendationsToRecommendationsDtos(
                recommendationRepository.findAllByAuthorId(authorId, Pageable.unpaged()).getContent());
    }

    private void saveSkillOffersInDb(Recommendation recommendation, User userReceiver, User userAuthor, List<Skill> skills) {
        for (Skill skill : skills) {
            Long skillOfferId = skillOfferRepository.create(skill.getId(), recommendation.getId());

            if (userReceiver.getSkills().contains(skill)) {
                UserSkillGuarantee userSkillGuarantee = new UserSkillGuarantee();
                userSkillGuarantee.setUser(userReceiver);
                userSkillGuarantee.setSkill(skill);
                userSkillGuarantee.setGuarantor(userAuthor);
                userSkillGuaranteeRepository.save(userSkillGuarantee);

                Optional<Skill> existedSkill = skillRepository.findById(skill.getId());
                existedSkill.get().getGuarantees().add(userSkillGuarantee);
                skillRepository.save(existedSkill.get());
            }

            recommendation.getSkillOffers().add(skillOfferRepository.findById(skillOfferId).get());
        }
    }

    private void validateRecommendationDto(RecommendationDto recommendation) {
        recommendationDtoValidator.checkIfRecommendationContentIsBlank(recommendation);
        recommendationDtoValidator.checkIfRecommendationCreatedTimeIsShort(recommendation);
    }
}
