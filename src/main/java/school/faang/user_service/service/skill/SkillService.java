package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;


    public boolean validateSkill(Skill skill){
        return skillRepository.existsByTitle(skill.getTitle());
    }

    public void saveSkill(Skill skill){
        skillRepository.save(skill);
    }

    public void assignSkillToUser(long userId, long skillId){
        skillRepository.assignSkillToUser(skillId, userId);
    }


}
