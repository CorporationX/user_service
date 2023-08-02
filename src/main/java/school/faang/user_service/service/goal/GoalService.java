package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;

import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.goal.GoalMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validation.GoalValidator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class GoalService {
    private final GoalRepository goalRepository;
    private final GoalMapperImpl goalMapper;
    private final List<GoalFilter> goalFilters;
    private final GoalValidator goalValidator;

    @Autowired
    public GoalService(GoalRepository goalRepository, GoalMapperImpl goalMapper, List<GoalFilter> goalFilters, GoalValidator goalValidator) {
        this.goalRepository = goalRepository;
        this.goalMapper = goalMapper;
        this.goalFilters = Optional.ofNullable(goalFilters).orElse(List.of());
        this.goalValidator = goalValidator;
    }

    public List<GoalDto> getGoalsByUser(@NotNull Long userId, GoalFilterDto filters) {
        Stream<Goal> goals = goalRepository.findAll().stream();

        return goalFilters.stream()
                .filter(goalFilter -> goalFilter.isApplicable(filters))
                .flatMap(goalFilter -> goalFilter.applyFilter(goals, filters))
                .map(goalMapper::toDto)
                .toList();
    }


    public List<GoalDto> findSubtasksByGoalId(long goalId, GoalFilterDto filter) {
        Stream<Goal> subtasks = goalRepository.findByParent(goalId);

        if (filter != null) {
            for (GoalFilter goalFilter : goalFilters) {
                if (goalFilter.isApplicable(filter)) {
                    subtasks = goalFilter.applyFilter(subtasks, filter);
                }
            }
        }

        return subtasks
                .map(goalMapper::toDto)
                .toList();
    }

    @Transactional
    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        goalValidator.validateGoal(userId, goalDto);
        Goal goal = goalMapper.toEntity(goalDto);
        goalRepository.create(goal.getTitle(), goal.getDescription(), goal.getParent().getId());
        saveGoal(goal);
        return goalMapper.toDto(goal);
    }

    private void saveGoal(Goal goal) {
        List<Skill> skillsToAchieve = goal.getSkillsToAchieve();
        goalRepository.save(goal);
    }

    public List<GoalDto> getGoalsByUser(@NotNull Long userId) {
        Stream<Goal> goals = goalRepository.findGoalsByUserId(userId);

        return goals.map(goalMapper::toDto).toList();
    }

    public void deleteGoal ( long goalId){
        goalRepository.deleteById(goalId);
    }

    public void deleteAllByIds (List < Long > ids) {
        goalRepository.deleteAllById(ids);
    }

    public GoalDto get (Long id){
        Goal goal = goalRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Couldn't find a goal with id: " + id));

        return goalMapper.toDto(goal);
    }

    public GoalDto update (GoalDto goal){
        GoalDto existingGoal = get(goal.getId());

        goalMapper.update(existingGoal, goal);
        goalRepository.save(goalMapper.toEntity(existingGoal));

        return existingGoal;
    }

    public int removeUserFromGoals (List < Long > goalIds, Long userId){
        List<Goal> goals = goalRepository.findAllById(goalIds);

        goals.forEach(goal -> {
            List<User> currentUsers = goal.getUsers();
            goal.setUsers(currentUsers.stream().filter(user -> user.getId() != userId).toList());
        });

        return goals.size();

    }
}
