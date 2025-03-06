package school.faang.user_service.service.goal;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalMapper;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.goal_filter.GoalFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GoalService {

    private static final int MAX_ACTIVE_GOALS = 3;
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final List<GoalFilter> goalFilters;

    private final GoalMapper goalMapper = Mappers.getMapper(GoalMapper.class);

    @Autowired
    public GoalService(GoalRepository goalRepository, SkillRepository skillRepository, List<GoalFilter> goalFilters) {
        this.goalRepository = goalRepository;
        this.skillRepository = skillRepository;
        this.goalFilters = goalFilters;
    }

    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        validateGoals(userId);
        validateSkills(goalDto.getSkillsIds());
        List<Skill> skills = goalMapper.mapSkillsToEntities(goalDto.getSkillsIds());

        Goal savedGoal = new Goal();
        savedGoal.setTitle(goalDto.getTitle());
        savedGoal.setDescription(goalDto.getDescription());
        savedGoal.setId(goalDto.getParentId());
        savedGoal = goalRepository.save(savedGoal);
        savedGoal.getSkillsToAchieve().addAll(skills);

        return goalMapper.toGoalDto(savedGoal);
    }

    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
        validateSkills(goalDto.getSkillsIds());
        List<Long> skillsIds = goalDto.getSkillsIds();
        Goal existingGoal = goalRepository.findById(goalId).orElseThrow(() -> new DataValidationException("Goal not found"));
        if (existingGoal.getStatus() == GoalStatus.COMPLETED) {
            throw new DataValidationException("Cannot update a completed goal");
        }

        validateSkills(skillsIds);
        validateGoals(goalId);
        existingGoal.setTitle(goalDto.getTitle());
        existingGoal.setDescription(goalDto.getDescription());
        existingGoal.setStatus(goalDto.getStatus());

        if (existingGoal.getSkillsToAchieve() == null) {
            existingGoal.setSkillsToAchieve(new ArrayList<>());
        }

        existingGoal.getSkillsToAchieve().clear();
        existingGoal.getSkillsToAchieve().addAll(skillRepository.findAllByUserId(goalId));
        goalRepository.save(existingGoal);

        return goalMapper.toGoalDto(existingGoal);
    }

    public void deleteGoal(long goalId) {

        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> new DataValidationException(("Goal not found")));

        if (goal.getSkillsToAchieve() == null) {
            goal.setSkillsToAchieve(new ArrayList<>());
        }

        goal.getSkillsToAchieve().clear();
        goalRepository.save(goal);
    }

    public List<GoalDto> findSubtasksByGoalId(long goalId, GoalFilterDto filterDto) {

        Stream<Goal> subtasksStream = goalRepository.findByParent(goalId);
        if (filterDto == null) {
            for (GoalFilter goalFilter : goalFilters) {
                if (goalFilter.isApplicable(filterDto)) {
                    subtasksStream = goalFilter.apply(subtasksStream, filterDto);
                }
            }
        }

        List<Goal> subtask = subtasksStream.toList();
        if (subtask.isEmpty()) {
            throw new DataValidationException("Подзадачи для данной цели не найдены");
        }

        return subtask.stream()
                .map(this::convertGoalToDo)
                .collect(Collectors.toList());
    }

    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto goalFilterDto) {
        Stream<Goal> goalStream = goalRepository.findGoalsByUserId(userId);

        if (goalFilterDto.getId() != null) {
            goalStream = goalStream.filter(goal -> goal.getId().equals(goalFilterDto.getId()));
        }

        if (goalFilterDto.getUser() != null) {
            goalStream = goalStream.filter(goal -> goal.getUsers().equals(goalFilterDto.getUser()));
        }

        return goalStream.map(goalMapper::toGoalDto).collect(Collectors.toList());
    }

    private void validateGoals(Long userId) {
        int activeGoals = goalRepository.countActiveGoalsPerUser(userId);
        if (activeGoals >= MAX_ACTIVE_GOALS) {
            throw new DataValidationException("User has reached the maximum goals");
        }
    }

    private void validateSkills(List<Long> skillIds) {
        long existingSkillsCount = skillRepository.countExisting(skillIds);
        if (existingSkillsCount < skillIds.size()) {
            throw new DataValidationException("One or more skills don't exist");
        }
    }

    private GoalDto convertGoalToDo(Goal goal) {
        GoalDto goalDto = new GoalDto();
        goalDto.setId(goal.getId());
        goalDto.setTitle(goal.getTitle());
        goalDto.setDescription(goal.getDescription());
        goalDto.setStatus(goal.getStatus());

        return goalDto;
    }
}









