package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.mapper.ListOfSkillsCandidateMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.validator.SkillValidator;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final ListOfSkillsCandidateMapper listOfSkillsCandidateMapper;
    private final SkillValidator skillValidator;
    private final long MIN_SKILL_OFFERS = 3;

    public SkillDto create(SkillDto skill) {
        skillValidator.validateSkill(skill);
        Skill skillEntity = skillMapper.toEntity(skill);
        return skillMapper.toDto(skillRepository.save(skillEntity));
    }

    public List<SkillDto> getUserSkills(long userId) {
        return skillRepository.findAllByUserId(userId).stream().
                map(skill -> skillMapper.toDto(skill)).collect(Collectors.toList());
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> skills = skillRepository.findSkillsOfferedToUser(userId);
        return listOfSkillsCandidateMapper.listToSkillCandidateDto(skills);
    }

}
