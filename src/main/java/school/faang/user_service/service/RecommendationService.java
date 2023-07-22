package school.faang.user_service.service;

import jakarta.persistence.ManyToOne;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.mapper.SkillOfferMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.utils.validator.ValidatorService;

import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;


@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final ValidatorService validatorService;
    private final RecommendationMapper recommendationMapper;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final SkillOfferMapper skillOfferMapper;
    private final UserSkillGuarantee userSkillGuarantee;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    public RecommendationDto create(RecommendationDto recommendationDto) {
        Recommendation recommendation = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc
                        (recommendationDto.getAuthorId(), recommendationDto.getReceiverId())
                .orElseThrow(() -> new DataValidationException("Recommendation is not found"));

        validatorService.validateTime(recommendation);

        List<SkillOffer> listSkillOffer = recommendationDto.getSkillOffers() //все рекомендации скиллов от одного пользователя
                .stream()
                .map(skillOfferDto -> skillOfferMapper.toEntity(skillOfferDto, this))
                .toList();

        recommendationDto.getSkillOffers()
                .forEach(skillOffer -> {
                    if (!skillRepository.existsById(skillOffer.getSkillId())) {
                        throw new DataValidationException("There is some skills missing");
                    }
                });

        listSkillOffer.forEach(
                skillOffer -> skillOfferRepository
                        .create(skillOffer.getSkill().getId(), skillOffer.getRecommendation().getId())
        );

        Recommendation recommendationGuarantee = recommendationMapper.toEntity(recommendationDto, this);
        User user = recommendationGuarantee.getReceiver();

        listSkillOffer //10 пункт
                .forEach(skillOffer -> {
                    Optional<Skill> optionalSkill = skillRepository.findUserSkill(skillOffer.getSkill().getId(), user.getId());
                    if (optionalSkill.isPresent()) {
                        userSkillGuarantee.setUser(user);
                        userSkillGuarantee.setSkill(skillOffer.getSkill());
                        userSkillGuarantee.setGuarantor(recommendationGuarantee.getAuthor());
                        userSkillGuaranteeRepository.save(userSkillGuarantee);
                    }
                });
        recommendationRepository.save(recommendationGuarantee); //12 пункт
        return recommendationMapper.toDto(recommendationGuarantee);
    }

    public Skill getSkill(long skillId) {
        return skillRepository.findById(skillId)
                .orElseThrow(() -> new DataValidationException("Skill not exist"));
    }

    public User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("User not found"));
    }
}
