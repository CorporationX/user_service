package school.faang.user_service.service.skillOffer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recomendation.SkillOfferDto;
import school.faang.user_service.dto.skill.SkillOfferedEventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.SkillOfferMapper;
import school.faang.user_service.publisher.skillOffer.SkillOfferPublisher;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.skill.SkillOfferValidator;

@Service
@RequiredArgsConstructor
public class SkillOfferService {
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferValidator skillOfferValidator;
    private final SkillOfferMapper skillOfferMapper;
    private final SkillOfferPublisher skillOfferPublisher;

    @Transactional
    public SkillOfferDto offerSkillToUser(
        Long senderId,
        Long receiverId,
        Long skillId
    ) {
        skillOfferValidator.validateSkillOffer(senderId, receiverId, skillId);

        var skillOfferedEventDto = createSkillOfferedEventDto(senderId, receiverId, skillId);
        skillOfferPublisher.publish(skillOfferedEventDto);

        var skill = getSkill(skillId);
        var recommendation = getRecommendation(senderId, receiverId);

        var skillOffer = createSkillOffer(skill, recommendation);
        skillOffer = skillOfferRepository.save(skillOffer);
        return skillOfferMapper.toDto(skillOffer);
    }

    private SkillOfferedEventDto createSkillOfferedEventDto(
        Long senderId,
        Long receiverId,
        Long skillId
    ) {
        return SkillOfferedEventDto.builder()
            .senderId(senderId)
            .receiverId(receiverId)
            .skillId(skillId)
            .build();
    }

    private Skill getSkill(Long skillId) {
        return skillRepository.findById(skillId)
            .orElseThrow(() -> new EntityNotFoundException("Skill not found with ID: %d"
                .formatted(skillId)));
    }

    private Recommendation getRecommendation(Long senderId, Long receiverId) {
        return recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(senderId, receiverId)
            .orElseThrow(() -> new EntityNotFoundException("Recommendation not found with senderId: %d and receiverId: %d"
                .formatted(senderId, receiverId)));
    }

    private SkillOffer createSkillOffer(Skill skill, Recommendation recommendation) {
        return SkillOffer.builder()
            .skill(skill)
            .recommendation(recommendation)
            .build();
    }
}
