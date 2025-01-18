package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;

    public Optional<Skill> findSkillById(Long skillId){
        return skillRepository.findById(skillId);
    }

    @Transactional
    public void assignSkillsFromGoalToUsers(Long goalId, List<User> userIds) {
        if (userIds == null) {
            return;
        }
        userIds.forEach(user -> skillRepository.assignSkillToUser(goalId, user.getId()));
    }
}
