package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.dto.recommendation.UserSkillGuaranteeDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
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
        Recommendation entity = recommendationRepository.findById(entityId).orElseThrow(() -> new EntityNotFoundException("Recommendation not found"));
        skillSave(entity, recomendation.getSkillOffers());
        return recommendationMapper.toDto(entity);
    }

    public void skillSave(Recommendation recommendation, List<SkillOfferDto> list) {
        list.forEach(offer -> {
            long offersIds = skillOffersRepository.create(offer.getSkillId(), recommendation.getId());
            recommendation.addSkillOffer(skillOffersRepository.findById(offersIds).orElseThrow(() -> new EntityNotFoundException("Skill offer not found")));
            guaranteesHaveSkill(recommendation);
        });
    }

    public void guaranteesHaveSkill(Recommendation recommendation) {
        List<SkillDto> usersSkills = findSkills(recommendation.getReceiver().getId());
        UserSkillGuaranteeDto userSkillGuaranteeDto = getUserSkillGuarantee(recommendation);
        for (SkillOffer skillOffer : recommendation.getSkillOffers()) {
            usersSkills.stream()
                    .filter(userSkill -> userSkill.getId().equals(skillOffer.getSkill().getId()))
                    .forEach(skill -> guaranteesHave(skill, recommendation, userSkillGuaranteeDto));
        }
    }

    public void guaranteesHave(SkillDto skill, Recommendation recommendation, UserSkillGuaranteeDto userSkillGuaranteeDto) {
        if (skill.getGuaranteeDtoList()
                .stream()
                .noneMatch(guarantee -> guarantee.getGuarantorId() == recommendation.getAuthor().getId())) {
            userSkillGuaranteeDto.setSkillId(skill.getId());
            recommendation.getSkillOffers().stream()
                    .map(SkillOffer::getSkill)
                    .filter(p -> p.getId() == skill.getId())
                    .forEach((p) -> {
                        p.getGuarantees().add(userSkillGuaranteeMapper.toEntity(userSkillGuaranteeDto));
                    });
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
}