package school.faang.user_service.service.skill;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;

import java.util.List;

@Component
public interface SkillService {

    boolean checkActiveSkill(Long skillId);

    void saveAll(List<Skill> skills);

    void deleteAllSkills(List<Skill> skills);
    boolean exist(long skillId);
}
