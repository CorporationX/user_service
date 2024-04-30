package school.faang.user_service.service.skill;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Component
public class SkillServiceImpl implements SkillService{
    private SkillRepository skillRepository;
    @Override
    public boolean checkActiveSkill(Long skillId) {
        return skillRepository.existsById(skillId);
    } //TODO

    @Override
    public void saveAll(List<Skill> skills) {
        skillRepository.saveAll(skills);
    } //TODO

    @Override
    public void deleteAllSkills(List<Skill> skills) { //TODO
        skillRepository.deleteAll(skills);
    }

    @Override
    public boolean exist(long skillId) {
        return true; //TODO
    }
}
