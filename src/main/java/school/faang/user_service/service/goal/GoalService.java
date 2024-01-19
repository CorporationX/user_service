package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.validator.GoalValidator;

import java.util.List;

/**
 * @author Ilia Chuvatkin
 */

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalValidator goalValidator;
    private final GoalRepository goalRepository;
    private final SkillService skillService;
    private final GoalMapper goalMapper;

    public void createGoal(Long userId, GoalDto goalDto) {
        validate(userId,goalDto);
        Goal goal = goalMapper.toEntity(goalDto);
        goalRepository.create(goal.getTitle(), goal.getDescription(), userId);
        goal.getSkillsToAchieve().forEach(s -> goalRepository.addSkillToGoal(s.getId(), goal.getId()));
    }

    private void validate(Long userId, GoalDto goalDto) {
        List<Long> userSkillsIds = skillService.getUserSkills(userId).stream().map(SkillDto::getId).toList();
        long countActiveGoals = goalRepository.countActiveGoalsPerUser(userId);
        goalValidator.validateExistingSkills(userSkillsIds, goalDto);
        goalValidator.validateActiveGoals(countActiveGoals);
    }
}
