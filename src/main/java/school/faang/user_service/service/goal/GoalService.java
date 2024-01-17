package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
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
    private final GoalMapper goalMapper;

    public void createGoal(Long userId, GoalDto goalDto) {
        if (goalValidator.isValidateByActiveGoals(userId) && goalValidator.isValidateByExistingSkills(userId, goalDto)) {
            Goal goal = goalMapper.toEntity(goalDto);
            goalRepository.create(goal.getTitle(), goal.getDescription(), userId);
            goal.getSkillsToAchieve().forEach(s -> goalRepository.addSkillToGoal(s.getId(), goal.getId()));
        }
    }
}
