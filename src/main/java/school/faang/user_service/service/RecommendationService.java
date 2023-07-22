package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.dto.recommendation.UserSkillGuaranteeDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.mapper.UserSkillGuaranteeMapper;
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
    private final UserSkillGuaranteeMapper userSkillGuaranteeMapper;
    private final RecommendationMapper recommendationMapper;

    public RecommendationDto create(RecommendationDto recomendation) {
        recommendationValidator.validateData(recomendation);
        recommendationValidator.validateSkill(recomendation);
        Long entityId = recommendationRepository.create(recomendation.getAuthorId(), recomendation.getReceiverId(), recomendation.getContent());
        Recommendation entity = recommendationRepository.findById(entityId).orElseThrow(() -> new DataValidationException("Recommendation not found"));
        skillSave(entity, recomendation.getSkillOffers());
        return recommendationMapper.toDto(entity);
    }

    public void skillSave(Recommendation recommendation, List<SkillOfferDto> list) {
        list.forEach(offer -> {
            long offersIds = skillOffersRepository.create(offer.getSkillId(), recommendation.getId());
            recommendation.addSkillOffer(skillOffersRepository.findById(offersIds).orElseThrow(() -> new DataValidationException("Skill offer not found")));
            guaranteesHaveSkill(recommendation);
        });
    }

    public void guaranteesHaveSkill(Recommendation recommendation) {
        List<SkillDto> usersSkills = findSkills(recommendation.getReceiver().getId());
        UserSkillGuaranteeDto userSkillGuaranteeDto = getUserSkillGuarantee(recommendation);
        for (SkillOffer skillOffer : recommendation.getSkillOffers()) {
            usersSkills.stream()
                    .filter(userSkill -> userSkill.getId().equals(skillOffer.getSkill().getId()))
                    .forEach(skill -> guaranteesHave(skill, recommendation.getAuthor().getId(), userSkillGuaranteeDto));
        }
    }

    public void guaranteesHave(SkillDto skill, Long userId, UserSkillGuaranteeDto userSkillGuaranteeDto) {
        if (skill.getGuaranteeDtoList()
                .stream()
                .noneMatch(guarantee -> guarantee.getGuarantorId() == userId)) ;
        userSkillGuaranteeDto.setSkillId(skill.getId());
        {
            skill.getGuaranteeDtoList().add(userSkillGuaranteeDto);
            userSkillGuaranteeRepository.save(userSkillGuaranteeMapper.toEntity(userSkillGuaranteeDto));
        }
    }

    public List<SkillDto> findSkills(long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        List<SkillDto> skillDtos = new ArrayList<>();
        skills.stream().map(skillMapper::toDto).forEach(skillDtos::add);
        return skillDtos;
    }

    public UserSkillGuaranteeDto getUserSkillGuarantee(Recommendation entity) {
        UserSkillGuaranteeDto userSkillGuaranteeDto = new UserSkillGuaranteeDto();
        userSkillGuaranteeDto.setUserId(entity.getReceiver().getId());
        userSkillGuaranteeDto.setGuarantorId(entity.getAuthor().getId());
        return userSkillGuaranteeDto;
    }

    public RecommendationDto updateRecommendation(RecommendationDto updated, Long id) {
        Recommendation entity = recommendationRepository.findById(id).orElseThrow(() -> new DataValidationException("Recommendation not found"));
        recommendationValidator.validateData(updated);
        recommendationValidator.validateSkill(updated);
        Recommendation updatedEntity = recommendationRepository.update(updated.getAuthorId(), updated.getReceiverId(), updated.getContent());
        skillOffersRepository.deleteAllByRecommendationId(id);
        skillSave(entity, updated.getSkillOffers());
        return recommendationMapper.toDto(updatedEntity);
    }

    public void deleteRecommendation(long id) {
        recommendationRepository.deleteById(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId){
        Page<Recommendation> page = recommendationRepository.findAllByReceiverId(receiverId, Pageable.unpaged());
        return page.stream().map(recommendationMapper::toDto).toList();
    }

    public List<RecommendationDto> getAllUserGivenRecommendations(Long authorId) {
        Page<Recommendation> page = recommendationRepository.findAllByAuthorId(authorId, Pageable.unpaged());
        return page.stream().map(recommendationMapper::toDto).toList();
    }
}