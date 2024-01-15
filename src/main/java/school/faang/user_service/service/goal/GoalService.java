package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.GoalValidator;

/**
 * @author Ilia Chuvatkin
 */

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalValidator goalValidator;
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final GoalMapper goalMapper;

    public void createGoal(Long userId, GoalDto goalDto) {
        Goal goal = goalMapper.toEntity(goalDto);
        goalDto.getSkillIds().forEach(s -> goalRepository.addSkillToGoal(s, goal.getId()));

        if (goalValidator.isValidateByActiveGoals(userId) && goalValidator.isValidateByExistingSkills(userId, goal)) {
            goalRepository.create(goal.getTitle(), goal.getDescription(), userId);
            goal.getSkillsToAchieve().forEach(s -> goalRepository.addSkillToGoal(s.getId(), goal.getId()));
        }
    }

    public void updateGoal(Long goalId, GoalDto goalDto) {
        Goal goalNew = goalMapper.toEntity(goalDto);
        goalDto.getSkillIds().forEach(s -> goalRepository.addSkillToGoal(s, goalNew.getId()));
        Goal goalOld = goalRepository.findById(goalId).orElseThrow();

        if (goalValidator.isValidateByCompleted(goalOld) && goalValidator.isValidateByExistingSkills(goalNew)) {
            goalRepository.save(goalNew);
        }
        if (goalNew.getStatus() == GoalStatus.COMPLETED) {
            goalRepository.findUsersByGoalId(goalNew.getId())
                    .forEach(user -> goalNew.getSkillsToAchieve().stream()
                            .filter((s1) -> !user.getSkills().contains(s1))
                            .forEach(s1 -> skillRepository.assignSkillToUser(s1.getId(), user.getId())));
        }
    }
}
