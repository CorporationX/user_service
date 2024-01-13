package school.faang.user_service.service.skill;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

@Component
public class SkillService {

    private final SkillRepository skillRepository;

    @Autowired
    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public boolean validateSkill(Skill skill){
        return skillRepository.existsByTitle(skill.getTitle());
    }

    public void saveSkill(Skill skill){
        skillRepository.save(skill);
    }
}
