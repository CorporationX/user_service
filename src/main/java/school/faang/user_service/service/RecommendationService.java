package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendationDtoValidator;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final SkillRepository skillRepository;
    private final RecommendationDtoValidator recommendationDtoValidator;
    private final UserRepository userRepository;
    private final RecommendationMapper recommendationMapper;

    public RecommendationDto create(RecommendationDto recommendation) {
        validateRecommendationDto(recommendation);
        List<Skill> skills = getSkillsFromDb(recommendation);
        User userReceiver = checkIfUserIsExist(recommendation.getReceiverId());
        User userAuthor = checkIfUserIsExist(recommendation.getAuthorId());

        Long recommendationId = recommendationRepository
                .create(userAuthor.getId(), userReceiver.getId(), recommendation.getContent());

        Recommendation existedRecommendation = recommendationRepository.findById(recommendationId).orElseThrow(
                () -> new DataValidationException("Recommendation with id - " + recommendation.getId() + " does not exist"));

        saveSkillOffersInDb(existedRecommendation, userReceiver, userAuthor, skills);

        return recommendationMapper.toDto(existedRecommendation);
    }

    public RecommendationDto update(RecommendationDto recommendation) {
        validateRecommendationDto(recommendation);
        User userReceiver = checkIfUserIsExist(recommendation.getReceiverId());
        User userAuthor = checkIfUserIsExist(recommendation.getAuthorId());
        List<Skill> skills = getSkillsFromDb(recommendation);

        recommendationRepository.update(userAuthor.getId(), userReceiver.getId(), recommendation.getContent());

        Recommendation existedRecommendation = recommendationRepository.findById(recommendation.getId()).orElseThrow(
                () -> new DataValidationException("Recommendation with id - " + recommendation.getId() + " does not exist"));

        skillOfferRepository.deleteAllByRecommendationId(existedRecommendation.getId());

        saveSkillOffersInDb(existedRecommendation, userReceiver, userAuthor, skills);

        return recommendationMapper.toDto(existedRecommendation);
    }

    public void delete(long id) {
        recommendationRepository.deleteById(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long recieverId) {
        return recommendationMapper.toDtos(
                 recommendationRepository.findAllByReceiverId(recieverId, Pageable.unpaged()).getContent());
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        return recommendationMapper.toDtos(
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

                Skill existedSkill = skillRepository.findById(skill.getId()).orElseThrow(
                        () -> new DataValidationException("Skill with id - " + skill.getId() + " does not exist"));
                UserSkillGuarantee existedUserSkillGuarantee = userSkillGuaranteeRepository
                        .findByReceiverIdAndSkillIdAndAuthorId(userReceiver.getId(), skill.getId(), userAuthor.getId())
                        .orElseThrow(() -> new DataValidationException("UserSkillGuarantee does not exist"));

                existedSkill.getGuarantees().add(existedUserSkillGuarantee);
                skillRepository.save(existedSkill);
            }

            SkillOffer existedSkillOffer = skillOfferRepository.findById(skillOfferId).orElseThrow(
                    () -> new DataValidationException("SkillOffer with id - " + skillOfferId + " does not exist"));
            recommendation.getSkillOffers().add(existedSkillOffer);
        }
    }

    private void validateRecommendationDto(RecommendationDto recommendation) {
        recommendationDtoValidator.validateIfRecommendationContentIsBlank(recommendation);
        recommendationDtoValidator.validateIfRecommendationCreatedTimeIsShort(recommendation);
    }

    private List<Skill> getSkillsFromDb(RecommendationDto recommendation) {
        List<SkillOfferDto> skillOfferDtos = recommendation.getSkillOffers();

        List<Skill> skills = new ArrayList<>();
        for (SkillOfferDto skillOfferDto : skillOfferDtos) {
            Skill skillOffer = skillRepository.findById(skillOfferDto.getSkillId()).orElseThrow(
                    () -> new DataValidationException("Skill with id - " + skillOfferDto.getSkillId() + " does not exist"));

            skills.add(skillOffer);
        }

        return skills;
    }

    private User checkIfUserIsExist(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("User with id - " + userId + " does not exist"));
    }
}
