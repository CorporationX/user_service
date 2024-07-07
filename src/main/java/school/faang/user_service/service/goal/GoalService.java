package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.entity.filter.GoalFilters;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.entity.Skill;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillService skillService;
    private final GoalMapper goalMapper;
    private final List<GoalFilters> goalFilters;
    private final int MAX_NUMBERS_GOAL_USER = 3;

    public List<String> findAllGoalTitles() {
        return goalRepository.findAllGoalTitles();
    }

    public void createGoal(Long userId, Goal goal) {
        List<Skill> skills = goal.getSkillsToAchieve();

        if (goalRepository.countActiveGoalsPerUser(userId) >= MAX_NUMBERS_GOAL_USER) {
            log.error("This user" + userId + "has exceeded goal limit");
        } else if (existsByTitle(skills)) {
            goalRepository.create(goal.getTitle(), goal.getDescription(), goal.getParent().getId());
            skillService.create(skills, userId);
        }
    }

    public void updateGoal(Long goalId, GoalDto goalDto) {
        String status = goalRepository.findByParent(goalId)
                .map(Goal::getStatus)
                .toString();

        Goal goal = goalMapper.toGoal(goalDto);

        if (status.equals("COMPLETED")) {
            log.error("Goal has already been achieved");
        } else if (existsByTitle(goal.getSkillsToAchieve())) {
            skillService.addSkillToUsers(goalRepository.findUsersByGoalId(goalId), goalId);
        }
    }

    private boolean existsByTitle(List<Skill> skills) {
        if (!skillService.existsByTitle(skills))
            log.error("There is no skill with this name");

        return true;
    }

    public void deleteGoal(Long goalId) {
        Optional<Goal> goal = goalRepository.findByParent(goalId).findFirst();
        if (goal.isEmpty()) {
            goalRepository.deleteByGoalId(goalId);
        } else {
            log.error("A goal with this ID does not exist");
        }
    }

    public List<GoalDto> getSubtasksByGoalId(long goalId, GoalFilterDto filters) {
        Stream<Goal> goal = goalRepository.findByParent(goalId);
        return filters(goal, filters);
    }

    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filters) {
        Stream<Goal> goal = findGoalsByUserId(userId);
        return filters(goal, filters);
    }

    private Stream<Goal> findGoalsByUserId(Long userId) {
        return goalRepository.findGoalsByUserId(userId);
    }

    private List<GoalDto> filters(Stream<Goal> goal, GoalFilterDto filters) {
        goalFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(goal, filters));

        return goal
                .map(goalMapper::toGoalDto)
                .toList();
    }
}