package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;
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
    private final UserService userService;
    private final SkillService skillService;
    private final GoalMapper goalMapper;


    public void createGoal(Long userId, GoalDto goalDto) {
        goalValidator.validateForCreate(userId, goalDto);
        Goal goal = goalMapper.toEntity(goalDto);
        goal.setUsers(List.of(userService.findById(userId)));
        goal.setSkillsToAchieve(goalDto.getSkillIds().stream().map(skillService::findById).toList());
        goalRepository.save(goal);
    }
}
