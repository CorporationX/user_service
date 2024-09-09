package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.Skill.SkillCandidateDto;
import school.faang.user_service.dto.Skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.mapper.SkillCandidateMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.validator.candidate.Skill.SkillCandidateValidator;
import school.faang.user_service.validator.skill.SkillValidator;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
@Component
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillCandidateMapper skillCandidateMapper;
    private final SkillValidator skillValidator;
    private final SkillCandidateValidator skillCandidateValidator;


    public List<SkillDto> getUserSkills(long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        return skillMapper.toDtoList(skills);
    }

    public SkillDto create(SkillDto skill) throws IllegalArgumentException {
        skillValidator.validateSkill(skill);
        Skill skillEntity = skillMapper.toSkill(skill);
        return skillMapper.toDto(skillRepository.save(skillEntity));
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> skillCandidate = skillRepository.findSkillsOfferedToUser(userId);
        return skillCandidateMapper.toListDto(skillCandidate);
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) throws IllegalArgumentException {
        AtomicReference<SkillDto> dto = new AtomicReference<>();
        skillRepository.findUserSkill(skillId,userId).ifPresentOrElse(skill -> dto.set(skillMapper.toDto(skill)), () -> {
            throw new IllegalArgumentException("Skill not found");
        });
        skillValidator.validateSkill(dto.get());
        skillCandidateValidator.validateSkillOfferSize(skillId, userId);
        return dto.get();

    }
}
