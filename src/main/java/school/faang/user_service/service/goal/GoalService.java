package school.faang.user_service.service.goal;

import com.amazonaws.services.kms.model.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.filter.GoalFilters;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.GoalServiceValidate;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillService skillService;
    private final GoalMapper goalMapper;
    private final List<GoalFilters> goalFilters;
    private final GoalServiceValidate goalServiceValidate;

    @Transactional
    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        List<String> allGoalTitles = goalRepository.findAllGoalTitles();
        int countActiveUser = goalRepository.countActiveGoalsPerUser(userId);

        goalServiceValidate.checkLimitCountUser(countActiveUser);
        goalServiceValidate.checkDuplicateTitleGoal(goalDto, allGoalTitles);

        goalRepository.create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId());
        skillService.create(goalMapper.toGoal(goalDto).getSkillsToAchieve(), userId);
        return goalDto;
    }

    @Transactional
    public GoalDto updateGoal(long goalId, GoalDto goalDto) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new NotFoundException("Goal with this id does not exist in the database"));
        Goal updateGoal = goalMapper.toGoal(goalDto);

        goalServiceValidate.checkStatusGoal(goal);
        goalServiceValidate.existByTitle(updateGoal.getSkillsToAchieve());

        goal.setStatus(updateGoal.getStatus());
        goal.setTitle(updateGoal.getTitle());
        goal.setSkillsToAchieve(updateGoal.getSkillsToAchieve());
        goal.setDescription(updateGoal.getDescription());

        goalRepository.save(goal);
        skillService.addSkillToUsers(goalRepository.findUsersByGoalId(goalId), goalId);
        return goalMapper.toGoalDto(goal);
    }

    @Transactional
    public void deleteGoal(long goalId) {
        Stream<Goal> goal = goalRepository.findByParent(goalId);
        goalServiceValidate.checkExistenceGoal(goal);

        goalRepository.deleteById(goalId);
    }

    @Transactional
    public List<GoalDto> getSubtasksByGoalId(long goalId, GoalFilterDto filterGoals) {
        Stream<Goal> goal = goalRepository.findByParent(goalId);
        return filters(goal, filterGoals);
    }

    @Transactional
    public List<GoalDto> getGoalsByUser(long userId, GoalFilterDto filterGoals) {
        Stream<Goal> goal = goalRepository.findGoalsByUserId(userId);
        return filters(goal, filterGoals);
    }

    private List<GoalDto> filters(Stream<Goal> goal, GoalFilterDto filterGoals) {
        goalFilters.stream()
                .filter(filter -> filter.isApplicable(filterGoals))
                .forEach(filter -> filter.apply(goal, filterGoals));

        return goal
                .map(goalMapper::toGoalDto)
                .toList();
    }
}