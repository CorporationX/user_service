package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Component
@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService{
    private final SkillRepository skillRepository;
    @Override
    public boolean checkActiveSkill(Long skillId) {
        return skillRepository.existsById(skillId);
    }

    @Override
    public void saveAll(List<Skill> skills) {
        skillRepository.saveAll(skills);
    }

    @Override
    public void deleteAllSkills(List<Skill> skills) {
        skillRepository.deleteAll(skills);
    }

    @Override
    public boolean exist(long skillId) {
        return skillRepository.existsById(skillId);
    }
}
