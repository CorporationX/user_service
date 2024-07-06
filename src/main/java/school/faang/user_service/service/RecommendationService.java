package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;
import school.faang.user_service.dto.recomendation.RecommendationDto;
import school.faang.user_service.dto.recomendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.UserRepository;
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
    private final UserRepository userRepository;


    public RecommendationDto create(RecommendationDto recommendationDto) {
        recommendationValidator.validateRecommendationDto(recommendationDto);
        var previousRecommendation = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationDto.getAuthorId(), recommendationDto.getReceiverId());
        recommendationValidator.validateRecommendationDate(previousRecommendation);
        long IdRecommendation =
                recommendationRepository.create(recommendationDto.getAuthorId(), recommendationDto.getReceiverId(), recommendationDto.getContent());
        saveSkillOffer(recommendationDto.getSkillOffers(), IdRecommendation);
        Recommendation recommendation = recommendationRepository.findById(IdRecommendation).orElse(null);
        saveGuaranteeUserSkill(recommendationDto.getSkillOffers(), recommendation.getReceiver(), recommendation.getAuthor());
        return recommendationMapper.toDto(recommendation);
    }

    @Transactional(readOnly = true)
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
        saveGuaranteeUserSkill(updated.getSkillOffers(), updatedEntity.getReceiver(), updatedEntity.getAuthor());
        return recommendationMapper.toDto(updatedEntity);
    }

    public void delete(Long id) {
        existsById(id);
        recommendationRepository.deleteById(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId, Pageable pageable) {
        recommendationValidator.validateId(receiverId);
        List<Recommendation> allUserRecommendation = recommendationRepository
                .findAllByReceiverId(receiverId, pageable)
                .stream()
                .toList();
        checkRecommendationList(allUserRecommendation, receiverId);
        return recommendationMapper.recommendationToRecommendationDto(allUserRecommendation);
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId, Pageable pageable) {
        recommendationValidator.validateId(authorId);
        List<Recommendation> allUserRecommendation = recommendationRepository
                .findAllByAuthorId(authorId, pageable)
                .stream()
                .toList();
        checkRecommendationList(allUserRecommendation, authorId);
        return recommendationMapper.recommendationToRecommendationDto(allUserRecommendation);
    }

    private void checkRecommendationList(List<Recommendation> recommendations, long userId) {
        if (recommendations.isEmpty()) {
            String msg = "User id: %d recommendation not found!";
            log.error(String.format(msg, userId));
            throw new NotFoundException(String.format(msg, userId));
        }
    }

    private void existsById(Long id) {
        recommendationValidator.validateId(id);
        if (!recommendationRepository.existsById(id)) {
            String msg = "Entity not found by id:%d";
            log.error(String.format(msg, id));
            throw new IllegalArgumentException(String.format(msg, id));
        }
    }

    public void saveSkillOffer(List<SkillOfferDto> skillOffers, long recommendationId) {
        if (skillOffers == null || skillOffers.size() == 0) {
            skillOffers.forEach(skillOffer -> skillOfferRepository.create(skillOffer.getSkillId(), recommendationId));
        }
    }

    public void saveGuaranteeUserSkill(List<SkillOfferDto> skillOfferDto, User user, User guarantor) {
        UserSkillGuarantee userSkillGuarantee = new UserSkillGuarantee();
        List<Skill> userSkills = skillRepository.findAllByUserId(user.getId());
        if (skillOfferDto == null || skillOfferDto.size() == 0) {
            userSkills.stream().forEach(skill -> {
                skillOfferDto.stream().forEach(skillOffer -> {
                            if (skillOffer.getSkillId() == skill.getId() && !skill.getGuarantees().contains(guarantor)) {
                                userSkillGuarantee.builder().user(user).guarantor(user).skill(skill);
                                userSkillGuaranteeRepository.save(userSkillGuarantee);
                            }
                        }
                );
            });
        }
    }

}
