package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.service.SkillService;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class SkillController implements SkillService {

    public SkillDto create(Skill skill) throws IllegalArgumentException {
        return create(skill);
    }

    public List<SkillDto> getUserSkills(long userId) {
        return getUserSkills(userId);
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return getOfferedSkills(userId);
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) throws IllegalArgumentException {
        return acquireSkillFromOffers(skillId, userId);
    }
}

