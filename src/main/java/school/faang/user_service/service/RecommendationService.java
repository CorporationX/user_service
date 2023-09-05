package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.dto.redis.SkillOfferEventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.publisher.RecommendationEventPublisher;
import school.faang.user_service.publisher.SkillOfferedEventPublisher;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendationValidator;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final RecommendationValidator recommendationValidator;
    private final SkillOfferRepository skillOffersRepository;
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final RecommendationMapper recommendationMapper;
    private final SkillOfferedEventPublisher skillOfferedEventPublisher;
    private final RecommendationEventPublisher recommendationEventPublisher;

    public RecommendationDto create(RecommendationDto recommendation) {
        recommendationValidator.validateData(recommendation);
        Long entityId = recommendationRepository.create(recommendation.getAuthorId(), recommendation.getReceiverId(),
                recommendation.getContent());
        Recommendation entity = getRecommendation(entityId);
        recommendationEventPublisher.publish(entity);
        saveSkill(entity, recommendation.getSkillOffers());
        return recommendationMapper.toDto(entity);
    }

    public void saveSkill(Recommendation recommendation, List<SkillOfferDto> list) {
        list.forEach(offer -> {
            if (recommendation.getSkillOffers().isEmpty() ||
                    recommendation.getSkillOffers().stream()
                    .noneMatch(skillOffer -> skillOffer.getSkill().getId() == offer.getSkillId())) {
                long offerId = skillOffersRepository.create(offer.getSkillId(), recommendation.getId());
                recommendation.addSkillOffer(skillOffersRepository.findById(offerId)
                        .orElseThrow(() -> new EntityNotFoundException("Skill offer not found")));

                SkillOfferEventDto event = new SkillOfferEventDto(recommendation.getAuthor().getId(),
                        recommendation.getReceiver().getId(), offerId);
                skillOfferedEventPublisher.publish(event);

                giveGuaranteesHaveSkill(recommendation);
            }
        });
    }

    public void giveGuaranteesHaveSkill(Recommendation recommendation) {
        List<SkillDto> usersSkills = findSkills(recommendation.getReceiver().getId());
        UserSkillGuarantee userSkillGuarantee = getUserSkillGuarantee(recommendation);
        for (SkillOffer skillOffer : recommendation.getSkillOffers()) {
            usersSkills.stream()
                    .filter(userSkill -> userSkill.getId().equals(skillOffer.getSkill().getId()))
                    .forEach(skill -> giveGuarantees(skill, recommendation, userSkillGuarantee));
        }
    }

    public void giveGuarantees(SkillDto skillDto, Recommendation recommendation, UserSkillGuarantee userSkillGuarantee) {
        if (skillDto.getGuaranteeDtoList()
                .stream()
                .noneMatch(guarantee -> guarantee.getGuarantorId() == recommendation.getAuthor().getId())) {
            userSkillGuarantee.setSkill(skillMapper.toEntity(skillDto));
            recommendation.getSkillOffers().stream()
                    .map(SkillOffer::getSkill)
                    .filter(skill -> skill.getId() == skillDto.getId()).forEach(skill ->
                            skill.getGuarantees().add(userSkillGuarantee));
            userSkillGuaranteeRepository.save(userSkillGuarantee);
        }
    }

    public List<SkillDto> findSkills(long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        List<SkillDto> skillDtos = new ArrayList<>();
        skills.stream().map(skillMapper::toDto).forEach(skillDtos::add);
        return skillDtos;
    }

    public UserSkillGuarantee getUserSkillGuarantee(Recommendation entity) {
        UserSkillGuarantee userSkillGuarantee = new UserSkillGuarantee();
        userSkillGuarantee.setUser(entity.getReceiver());
        userSkillGuarantee.setGuarantor(entity.getAuthor());
        return userSkillGuarantee;
    }

    public RecommendationDto updateRecommendation(RecommendationDto updated, Long id) {
        Recommendation entity = getRecommendation(id);
        recommendationValidator.validateData(updated);
        Recommendation updatedEntity = recommendationRepository.update(updated.getAuthorId(), updated.getReceiverId(),
                updated.getContent());
        skillOffersRepository.deleteAllByRecommendationId(id);
        saveSkill(entity, updated.getSkillOffers());
        return recommendationMapper.toDto(updatedEntity);
    }

    private Recommendation getRecommendation(Long id) {
        return recommendationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recommendation not found"));
    }

    public void deleteRecommendation(long id) {
        recommendationRepository.deleteById(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        Page<Recommendation> page = recommendationRepository.findAllByReceiverId(receiverId, Pageable.unpaged());
        return page.stream().map(recommendationMapper::toDto).toList();
    }

    public List<RecommendationDto> getAllUserGivenRecommendations(Long authorId) {
        Page<Recommendation> page = recommendationRepository.findAllByAuthorId(authorId, Pageable.unpaged());
        return page.stream().map(recommendationMapper::toDto).toList();
    }
}