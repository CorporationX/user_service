package school.faang.user_service.service.skill;

import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillDto;

import java.util.List;

public interface SkillService {
    SkillDto create(CreateSkillDto skillDto);
    List<SkillDto> getAssignedSkills(Long userId);
}
