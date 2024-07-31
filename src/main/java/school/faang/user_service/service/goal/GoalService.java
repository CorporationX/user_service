package school.faang.user_service.service.goal;

import com.amazonaws.services.kms.model.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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


    // Поправить валидацию(передавать меньше значений и проверить мб как то можно упроситить)
    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        List<String> allGoalTitles = goalRepository.findAllGoalTitles();
        int countActiveUser = goalRepository.countActiveGoalsPerUser(userId);



        goalServiceValidate.validateCreateGoal(userId, goalDto, countActiveUser, allGoalTitles);

        goalRepository.create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId());
        skillService.create(goalDto.getSkillsToAchieve(), userId);
        return
    }


    // Надо брать goal из базы в нем менять поле которые пришли в dto и кидать обратно в базу

    /**
     Также проверить, что цель содержит только существующие скиллы.
     Для этого нужно использовать SkillService и SkillRepository с их методами.
     */
    public GoalDto updateGoal(long goalId, GoalDto goalDto) {
    Goal goal = goalRepository.findById(goalId)
            .orElseThrow(() -> new NotFoundException("Goal with this id does not exist in the database"));

    goalServiceValidate.validUpdate(goal);






//        String status = goalRepository.findByParent(goalId)
//                .map(Goal::getStatus)
//                .toString();
//        Goal goal = goalMapper.toGoal(goalDto);
//        goalServiceValidate.validateUpdateGoal(goal, status);

        skillService.addSkillToUsers(goalRepository.findUsersByGoalId(goalId), goalId);
        return
    }


    // надо что-то вернуть
    public void deleteGoal(long goalId) {
        Stream<Goal> goal = goalRepository.findByParent(goalId);
        goalServiceValidate.validateDeleteGoal(goal);

        goalRepository.deleteByGoalId(goalId);
    }

    public List<GoalDto> getSubtasksByGoalId(long goalId, GoalFilterDto filterGoals) {
        Stream<Goal> goal = goalRepository.findByParent(goalId);
        return filters(goal, filterGoals);
    }

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