package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.SkillRepository;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;

    public List<Skill> findSkillsByGoalId(long goalId) {
        return skillRepository.findSkillsByGoalId(goalId);
    }

    public boolean validateSkill(Skill skill) {
        return skillRepository.existsByTitle(skill.getTitle());
    }


    public void assignSkillToUser(long userId, long skillId) {
        skillRepository.assignSkillToUser(skillId, userId);
    }


    public boolean existsById(long id) {
        return skillRepository.existsById(id);
    }


    public Skill findById(long id) {
        return skillRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Навык не найден"));
    }
}
