package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.ResponseGoalDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.goal.CreateGoalMapper;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final GoalMapper goalMapper;
    private final CreateGoalMapper createGoalMapper;
    private final List<GoalFilter> goalFilters;
    private final int MAX_GOALS_PER_USER = 3;

    @Transactional
    public void deleteGoal(long goalId) {
        if (!goalRepository.existsById(goalId)) {
            throw new IllegalArgumentException("Goal is not found");
        }

        goalRepository.deleteById(goalId);
    }

    @Transactional(readOnly = true)
    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filterDto) {
        Stream<Goal> goals = goalRepository.findGoalsByUserId(userId)
                .peek(goal -> goal.setSkillsToAchieve(skillRepository.findSkillsByGoalId(goal.getId())));

        if (filterDto != null) {
            return filterGoals(goals, filterDto);
        }

        return goalMapper.toDtoList(goals.toList());
    }

    @Transactional(readOnly = true)
    public List<GoalDto> getSubGoalsByFilter(Long parentId, GoalFilterDto filterDto) {
        Stream<Goal> goals = goalRepository.findByParent(parentId)
                .peek(goal -> goal.setSkillsToAchieve(skillRepository.findSkillsByGoalId(goal.getId())));

        if (filterDto != null) {
            return filterGoals(goals, filterDto);
        }

        return goalMapper.toDtoList(goals.toList());
    }

    private List<GoalDto> filterGoals(Stream<Goal> goals, GoalFilterDto filter) {
        Stream<Goal> filteredGoals = goals;

        for (GoalFilter goalFilter : goalFilters) {
            if (goalFilter.isApplicable(filter)) {
                filteredGoals = goalFilter.apply(filteredGoals, filter);
            }
        }

        return goalMapper.toDtoList(filteredGoals.toList());
    }

    @Transactional
    public ResponseGoalDto createGoal(Long userId, CreateGoalDto goalDto) {
        validateGoalToCreate(userId, goalDto);

        return createGoalMapper.toResponseGoalDtoFromGoal(goalRepository.save(createGoalMapper.toGoalFromCreateGoalDto(goalDto)));
    }

    private void validateGoalToCreate(Long userId, CreateGoalDto goalDto) {
        if (goalDto == null) {
            throw new IllegalArgumentException("Goal cannot be null");
        }

        if (goalDto.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title cannot be null");
        }

        if (goalRepository.countActiveGoalsPerUser(userId) >= MAX_GOALS_PER_USER) {
            throw new IllegalArgumentException("Maximum number of goals for this user reached");
        }

        List<SkillDto> skillsToAchieve = goalDto.getSkillsToAchieve();
        skillsToAchieve.forEach(skill -> {
            if (!skillRepository.existsByTitle(skill.getTitle())) {
                throw new IllegalArgumentException("Skill not found");
            }
        });
    }
}
