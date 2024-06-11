package school.faang.user_service.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.mapper.SkillCandidateMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validation.skill.SkillValidator;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class SkillService {
    public final int MIN_SKILL_OFFERS = 3;
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillCandidateMapper skillCandidateMapper;
    private final SkillValidator skillValidate;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    public SkillDto create(SkillDto skillDto) {
        skillValidate.validateSkill(skillDto);
        Skill skillEntity = skillMapper.dtoToSkill(skillDto);
        return skillMapper.skillToDto(skillRepository.save(skillEntity));
    }

    public List<SkillDto> getUserSkills(long userId) {
        List<Skill> skills = skillRepository.findAllByUserId( userId );
        return skillMapper.toSkillDtoList( skills );
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {

        List<Skill> skillsOfferedToUser = skillRepository.findSkillsOfferedToUser(userId);

        Map<Long, Long> skillCountMap = skillsOfferedToUser.stream()
                .collect(Collectors.groupingBy(Skill::getId, Collectors.counting()));

        return skillsOfferedToUser.stream()
                .distinct()
                .map(skillCandidateMapper::toDto)
                .peek((skillCandidateDto -> {
                    long countSkill = skillCountMap.get(skillCandidateDto.getSkillDto().getId());
                    skillCandidateDto.setOffersAmount(countSkill);
                }))
                .toList();
    }

    public SkillDto acquireSkillFromOffers(Long skillId, Long userId) {
        Skill skillUser = skillRepository.findUserSkill(skillId, userId)
                .orElseThrow(() -> new ValidationException("The user already has the skill"));

        List<SkillOffer> skillOffers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);

        if (skillOffers.size() >= MIN_SKILL_OFFERS) {
            skillRepository.assignSkillToUser(skillId, userId);
            addUserSkillGuarantee(skillUser, skillOffers);
            return skillMapper.skillToDto(skillUser);
        } else {
            throw new ValidationException("You need 3 recommendations, currently you have: " + skillOffers.size());
        }
    }

    private void addUserSkillGuarantee(Skill userSkill, List<SkillOffer> allOffersOfSkill) {
            allOffersOfSkill.stream()
                    .map(skillOffer -> UserSkillGuarantee.builder()
                            .user(skillOffer.getRecommendation().getReceiver())
                            .skill(userSkill)
                            .guarantor(skillOffer.getRecommendation().getAuthor())
                            .build())
                    .forEach(userSkillGuaranteeRepository::save);
        }
    }
}


