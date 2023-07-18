package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.dto.skill.SkillCandidateDto;
import school.faang.user_service.entity.dto.skill.SkillDto;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;

    private final SkillOfferRepository skillOfferRepository;

    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    private final SkillMapper skillMapper;

    private static final long MIN_SKILL_OFFERS = 3;

    public SkillDto create(SkillDto skillDto) {
        if (skillRepository.existsByTitle(skillDto.getTitle())) {
            throw new DataValidationException("Такой скилл уже существует!!!");
        }
        Skill skill = skillRepository.save(skillMapper.toEntity(skillDto));

        return skillMapper.toDto(skill);
    }

    public List<SkillDto> getUserSkills(Long userId) {
        return skillRepository.findAllByUserId(userId)
                .stream()
                .map(skillMapper::toDto)
                .toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(Long userId) {
        return skillRepository.findSkillsOfferedToUser(userId)
                .stream()
                .collect(Collectors.groupingBy(skill -> skill, Collectors.counting()))
                .entrySet()
                .stream()
                .map(entry -> new SkillCandidateDto(skillMapper.toDto(entry.getKey()), entry.getValue()))
                .toList();
    }

    public SkillDto acquireSkillFromOffers(Long skillId, Long userId) {
        Skill userSkill = skillRepository.findUserSkill(skillId, userId).orElse(null);
        if (userSkill == null) {
            List<SkillOffer> allOffersOfSkill = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
            if (allOffersOfSkill.size() >= MIN_SKILL_OFFERS) {
                userSkill = skillRepository.findById(skillId).orElseThrow(() -> new DataValidationException("Такого скилла не существует!!"));
                skillRepository.assignSkillToUser(skillId, userId);
                addUserSkillGuarantee(userSkill, allOffersOfSkill);
                return skillMapper.toDto(userSkill);
            }
        }
        return skillMapper.toDto(userSkill);
    }

    protected void addUserSkillGuarantee(Skill userSkill, List<SkillOffer> allOffersOfSkill) {
        for (SkillOffer skillOffer : allOffersOfSkill) {
            userSkillGuaranteeRepository.save(UserSkillGuarantee.builder()
                    .user(skillOffer.getRecommendation().getReceiver())
                    .skill(userSkill)
                    .guarantor(skillOffer.getRecommendation().getAuthor())
                    .build());
        }
    }
}
