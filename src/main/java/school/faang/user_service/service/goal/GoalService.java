package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper = GoalMapper.INSTANCE;

    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filterDto) {
        List<Goal> goals = goalRepository.findGoalsByUserId(userId)
                .filter(goal -> {
                    if (filterDto.getGoalStatus() == null) {
                        return true;
                    }
                    return goal.getStatus().equals(filterDto.getGoalStatus());
                })
                .filter(goal -> {
                    if (filterDto.getSkillId() == null) {
                        return true;
                    }
                    return goalContainSkillId(filterDto, goal);
                })
                .filter(goal -> {
                    if (filterDto.getTitle() == null) {
                        return true;
                    }
                    return goal.getTitle().equals(filterDto.getTitle());
                })
                .toList();

        return goalMapper.toDtoList(goals);
    }

    private boolean goalContainSkillId(GoalFilterDto filterDto, Goal goal) {
        return goal.getSkillsToAchieve().stream().map(Skill::getId).toList().contains(filterDto.getSkillId());
    }
}
