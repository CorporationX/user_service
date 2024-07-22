package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendationValidation;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final RecommendationValidation recommendationValidation;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final RecommendationMapper recommendationMapper;

    public RecommendationDto create(RecommendationDto recommendationDto) {
        recommendationValidation.recommendationIsNotEmpty(recommendationDto);
        recommendationValidation.validationHalfOfYear(recommendationDto);
        recommendationValidation.skillValid(recommendationDto.skillOffers());

        Recommendation recommendation = recommendationMapper.recommendationEntity(recommendationDto);
        saveSkillsOffers(recommendation);
        recommendationRepository.save(recommendation);

        return recommendationMapper.recommendationDto(recommendation);
    }

        public RecommendationDto update(RecommendationDto recommendationDto){
            recommendationValidation.recommendationIsNotEmpty(recommendationDto);
            recommendationValidation.validationHalfOfYear(recommendationDto);
            recommendationValidation.skillValid(recommendationDto.skillOffers());

            Recommendation recommendation = recommendationMapper.recommendationEntity(recommendationDto);

            skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());
            saveSkillsOffers(recommendation);

            recommendationRepository.save(recommendation);

        return recommendationMapper.recommendationDto(recommendation);
        }

    private void saveSkillsOffers(Recommendation recommendation) {
        Long receiverId = recommendation.getReceiver().getId();

        User user = userRepository.findById(recommendation.getAuthor().getId())
                .orElseThrow(() -> new DataValidationException("Author not found"));
        User guarantee = userRepository.findById(recommendation.getReceiver().getId())
                .orElseThrow(() -> new DataValidationException("Receiver not found"));

        List<SkillOffer> skillOffers = recommendation.getSkillOffers();
        List<Skill> userSkills = userRepository.findById(receiverId)
                .orElseThrow(() -> new DataValidationException("User not found"))
                .getSkills();

        if (userSkills == null) {
            userSkills = new ArrayList<>();
        }

        for (SkillOffer skillOffer : skillOffers) {
            if (userSkills.contains(skillOffer.getSkill()) &&
                    !userSkillGuaranteeRepository.existsById(recommendation.getAuthor().getId())) {
                addGuarantee(user, guarantee, skillOffer.getSkill().getId());
            } else {
                skillOfferRepository.save(skillOffer);
            }
        }
    }

    private void addGuarantee(User user, User guarantee, long id) {
        Skill skill = skillRepository.findById(id).orElseThrow(
                () -> new DataValidationException("Skill not found")
        );
        UserSkillGuarantee skillGuarantee = UserSkillGuarantee.builder()
                .skill(skill)
                .user(user)
                .guarantor(guarantee)
                .build();

        userSkillGuaranteeRepository.save(skillGuarantee);
    }

    public void delete(long id) {
       recommendationRepository.findById(id)
               .ifPresentOrElse(recommendation -> recommendationRepository.deleteById(id),
                       () -> { throw new DataValidationException("Recommendation not found");} );
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        Page<Recommendation> recommendations = recommendationRepository.findAllByReceiverId(receiverId, Pageable.unpaged());
        return recommendations.get().map(recommendationMapper::recommendationDto).toList();
    }

    public List<RecommendationDto> getAllGivenRecommendations(Long authorId){
        Page<Recommendation> recommendations = recommendationRepository.findAllByAuthorId(authorId,Pageable.unpaged());
        return recommendations.get().map(recommendationMapper::recommendationDto).toList();
    }
}
