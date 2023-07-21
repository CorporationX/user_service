package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.dto.SkillOfferDto;
import school.faang.user_service.dto.UserSkillGuaranteeDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.RecommendationMapper;
import school.faang.user_service.mapper.event.SkillMapper;
import school.faang.user_service.mapper.event.UserSkillGuaranteeDtoMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendatorValidator;

import java.util.List;

@Service
@AllArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final RecommendatorValidator recommendatorValidator;
    private final SkillOfferRepository skillOffersRepository;
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final UserSkillGuaranteeDtoMapper userSkillGuaranteeDtoMapper;
    private final RecommendationMapper recommendationMapper;

    public RecommendationDto giveRecommendation(RecommendationDto recommendationDto) {
        recommendatorValidator.validateData(recommendationDto);
        Long entityId = recommendationRepository.create(recommendationDto.getAuthorId(), recommendationDto.getReceiverId(), recommendationDto.getContent());
        Recommendation entity = recommendationRepository.findById(entityId).orElseThrow(() -> new DataValidationException("Recommendation not found"));
        skillSave(entity, recommendationDto.getSkillOffers());
        return recommendationMapper.toDto(entity);
    }

    public void skillSave(Recommendation entity, List<SkillOfferDto> list) {
        list.forEach(offer -> {
            long offersIds = skillOffersRepository.create(offer.getSkillId(), entity.getId());
            entity.addSkillOffer(skillOffersRepository.findById(offersIds).orElseThrow(() -> new DataValidationException("Skill offer not found")));
            guaranteesHaveSkill(entity);
        });
    }

    public void guaranteesHaveSkill(Recommendation entity) {
        List<SkillDto> usersSkills = findSkills(entity.getReceiver().getId());
        UserSkillGuaranteeDto userSkillGuaranteeDto = getUserSkillGuarantee(entity);
        for (SkillOffer skillOffer : entity.getSkillOffers()) {
            usersSkills.stream()
                    .filter(userSkill -> userSkill.getId().equals(skillOffer.getSkill().getId()))
                    .forEach(skill -> guaranteesHave(skill, entity.getAuthor().getId(), userSkillGuaranteeDto));
        }

    }

    public void guaranteesHave(SkillDto skill, Long userId, UserSkillGuaranteeDto userSkillGuaranteeDto) {
        if (skill.getGuaranteeDtoList()
                .stream()
                .noneMatch(guarantee -> guarantee.getGuarantorId() == userId)) ;
        userSkillGuaranteeDto.setSkillId(skill.getId());
        skill.getGuaranteeDtoList().add(userSkillGuaranteeDto);
        userSkillGuaranteeRepository.save(userSkillGuaranteeDtoMapper.toEntity(userSkillGuaranteeDto));
    }

    public List<SkillDto> findSkills(long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        return skills.stream().map(skillMapper::toDto).toList();
    }

    public UserSkillGuaranteeDto getUserSkillGuarantee(Recommendation entity) {
        UserSkillGuaranteeDto userSkillGuaranteeDto = new UserSkillGuaranteeDto();
        userSkillGuaranteeDto.setUserId(entity.getReceiver().getId());
        userSkillGuaranteeDto.setGuarantorId(entity.getAuthor().getId());
        return userSkillGuaranteeDto;
    }

    public RecommendationDto updateRecommendation(RecommendationDto updated, Long id) {
        Recommendation entity = recommendationRepository.findById(id).orElseThrow(() -> new DataValidationException("Recommendation not found"));
        recommendatorValidator.validateData(updated);
        recommendatorValidator.validateSkill(updated);
        Recommendation updatedEntity = recommendationRepository.update(updated.getAuthorId(), updated.getReceiverId(), updated.getContent());
        skillOffersRepository.deleteAllByRecommendationId(id);
        skillSave(entity, updated.getSkillOffers());
        return recommendationMapper.toDto(updatedEntity);
    }

    public void deleteRecommendation(Long id) {
        recommendationRepository.deleteById(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(Long receiverId) {
        Page<Recommendation> page = recommendationRepository.findAllByReceiverId(receiverId, Pageable.unpaged());
        return page.stream().map(recommendationMapper::toDto).toList();
    }

    public List<RecommendationDto> getAllUserGivenRecommendations(Long authorId) {
        Page<Recommendation> page = recommendationRepository.findAllByAuthorId(authorId, Pageable.unpaged());
        return page.stream().map(recommendationMapper::toDto).toList();
    }
}
