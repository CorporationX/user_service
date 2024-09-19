package school.faang.user_service.service.goal;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;

    @Transactional
    public boolean existsByTitle(List<Skill> skills) {
        if (skills == null || skills.isEmpty()) {
            return false;
        } else {
            return skills.stream()
                    .map(Skill::getTitle)
                    .allMatch(skillRepository::existsByTitle);
        }
    }

    @Transactional
    public void create(List<Skill> skills, Long userId) {
        if (skills != null && !skills.isEmpty()) {
            skills.stream()
                    .map(Skill::getId)
                    .forEach(skillId ->
                            skillRepository.assignSkillToUser(skillId, userId));
        }
    }

    @Transactional
    public void addSkillToUsers(List<User> users, Long goalId) {
        if (users != null && !users.isEmpty() && goalId != null) {
            users.forEach(user ->
                    user.getSkills().addAll(skillRepository.findSkillsByGoalId(goalId)));
        }
    }
}