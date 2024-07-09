package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;
import school.faang.user_service.dto.recomendation.RecommendationDto;
import school.faang.user_service.dto.recomendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendationValidator;

import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final RecommendationValidator recommendationValidator;
    private final RecommendationMapper recommendationMapper;

    @Transactional
    public RecommendationDto create(RecommendationDto recommendationDto) {
        recommendationValidator.validateRecommendationDto(recommendationDto);
        var previousRecommendation = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                        recommendationDto.getAuthorId(), recommendationDto.getReceiverId());
        recommendationValidator.validateRecommendationDate(previousRecommendation);
        long IdRecommendation =
                recommendationRepository.create(recommendationDto.getAuthorId(), recommendationDto.getReceiverId(), recommendationDto.getContent());
        saveSkillOffer(recommendationDto.getSkillOffers(), IdRecommendation);
        Recommendation recommendation = recommendationRepository.findById(IdRecommendation).orElse(null);
        saveGuaranteeUserSkill(recommendationDto);
        return recommendationMapper.toDto(recommendation);
    }

    @Transactional
    public RecommendationDto update(RecommendationDto updated) {
        existsById(updated.getId());
        recommendationValidator.validateRecommendationDto(updated);
        var previousRecommendation = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(updated.getAuthorId(), updated.getReceiverId());
        recommendationValidator.validateRecommendationDate(previousRecommendation);
        recommendationRepository.update(updated.getAuthorId(), updated.getReceiverId(), updated.getContent());
        skillOfferRepository.deleteAllByRecommendationId(updated.getId());
        saveSkillOffer(updated.getSkillOffers(), updated.getReceiverId());
        Recommendation updatedEntity = recommendationRepository.findById(updated.getId()).orElse(null);
        saveGuaranteeUserSkill(updated);
        return recommendationMapper.toDto(updatedEntity);
    }

    @Transactional
    public void delete(Long id) {
        existsById(id);
        recommendationRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        recommendationValidator.validateId(receiverId);
        List<Recommendation> allUserRecommendation = recommendationRepository.findAllByReceiverId(receiverId);
        if (allUserRecommendation != null || !allUserRecommendation.isEmpty()) {
            recommendationValidator.checkRecommendationList(allUserRecommendation, receiverId);
        } else {
            throw new NotFoundException("Recommendation not found");
        }
        return recommendationMapper.recommendationToRecommendationDto(allUserRecommendation);
    }

    @Transactional(readOnly = true)
    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        recommendationValidator.validateId(authorId);
        List<Recommendation> allUserRecommendation = recommendationRepository.findAllByReceiverId(authorId);
        if (allUserRecommendation != null || !allUserRecommendation.isEmpty()) {
            recommendationValidator.checkRecommendationList(allUserRecommendation, authorId);
        } else {
            throw new NotFoundException("Recommendation not found");
        }
        return recommendationMapper.recommendationToRecommendationDto(allUserRecommendation);
    }


    private void existsById(Long id) {
        recommendationValidator.validateId(id);
        if (!recommendationRepository.existsById(id)) {
            String msg = "Entity not found by id:%d";
            log.error(String.format(msg, id));
            throw new IllegalArgumentException(String.format(msg, id));
        }
    }

    private void saveSkillOffer(List<SkillOfferDto> skillOffers, long recommendationId) {
        if (skillOffers != null) {
            skillOffers.forEach(skillOffer -> skillOfferRepository.create(skillOffer.getSkillId(), recommendationId));
        }
    }

    public void saveGuaranteeUserSkill(RecommendationDto recommendationDto) {
        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        List<Skill> userSkills = skillRepository.findAllByUserId(recommendation.getReceiver().getId());
        if (recommendation.getSkillOffers() != null || !recommendation.getSkillOffers().isEmpty()) {
            userSkills.stream()
                    .forEach(skill -> {
                       recommendationDto.getSkillOffers().stream()
                                .forEach(skillOffer -> {
                                    if (skillOffer.getSkillId() == skill.getId() && !skill.getGuarantees().contains(recommendation.getAuthor())) {
                                        UserSkillGuarantee userSkillGuarantee =
                                                UserSkillGuarantee.builder()
                                                        .user(recommendation.getReceiver())
                                                        .guarantor(recommendation.getReceiver())
                                                        .skill(skill).build();
                                        userSkillGuaranteeRepository.save(userSkillGuarantee);
                                    }
                                });
                    });
        }
    }

}
