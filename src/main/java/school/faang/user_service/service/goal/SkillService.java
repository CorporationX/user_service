package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        return skills.stream()
                .map(Skill::getTitle)
                .allMatch(skillRepository::existsByTitle);
    }

    @Transactional
    public void create(List<Skill> skills, Long userId) {
        skills.stream()
                .map(Skill::getId)
                .forEach(skillId ->
                        skillRepository.assignSkillToUser(skillId, userId));
    }

    @Transactional
    public void addSkillToUsers(List<User> users, Long goalId) {
        users.forEach(user ->
                user.getSkills()
                        .addAll(skillRepository.findSkillsByGoalId(goalId)));
    }
}