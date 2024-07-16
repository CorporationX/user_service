package school.faang.user_service.service;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
public record SkillService(SkillRepository skillRepository,
                           SkillMapper skillMapper,
                           SkillOfferRepository skillOfferRepository,
                           UserSkillGuaranteeRepository skillGuaranteeRepository) {
    private static final int MIN_SKILL_OFFERS = 3;

    public SkillDto create(SkillDto skillDto) {
        if (skillRepository.existsByTitle(skillDto.getTitle())) {
            throw new DataValidationException("Skill with this title already exist");
        }
        Skill skill = skillMapper.toEntity(skillDto);
        Skill savedSkill = skillRepository.save(skill);
        return skillMapper.toDto(savedSkill);
    }

    public List<SkillDto> getUserSkills(long userId) {
        return skillRepository.findAllByUserId(userId)
                .stream()
                .map(skillMapper::toDto)
                .toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return skillRepository.findSkillsOfferedToUser(userId)
                .stream()
                .collect(Collectors.groupingBy(skillMapper::toDto, Collectors.counting()))
                .entrySet()
                .stream()
                .map(entry -> new SkillCandidateDto(entry.getKey(), entry.getValue()))
                .toList();
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        Skill userSkill = skillRepository.findUserSkill(skillId, userId).orElse(null);
        if (userSkill == null) {
            List<SkillOffer> offers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
            if (offers.size() >= MIN_SKILL_OFFERS) {
                userSkill = skillRepository.findById(skillId).orElseThrow(() ->
                        new DataValidationException("No such skill!"));
                skillRepository.assignSkillToUser(skillId, skillId);
                addSkillGuaranty(userSkill, offers);
                return skillMapper.toDto(userSkill);
            }
        }
        return skillMapper.toDto(userSkill);
    }

    private void addSkillGuaranty(Skill userSkill, List<SkillOffer> offers) {
        offers.forEach(offer -> skillGuaranteeRepository.save(UserSkillGuarantee.builder()
                .user(offer.getRecommendation().getReceiver())
                .skill(userSkill)
                .guarantor(offer.getRecommendation().getAuthor())
                .build()));
    }
}
