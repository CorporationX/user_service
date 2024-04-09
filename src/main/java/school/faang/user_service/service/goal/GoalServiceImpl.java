package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.GoalOverflowException;
import school.faang.user_service.exception.SkillNotFoundException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillServiceImpl;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {
    private final int MAX_ACTIVE_GOALS = 3;

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final SkillServiceImpl skillService;
    private final GoalMapper goalMapper;
    private final List<GoalFilter> filters;

    @Transactional
    public void deleteGoal(long goalID) {
        goalRepository.deleteById(goalID);
    }

    public void createGoal(long userId, Goal goal) {
        User user = getUser(userId);
        validateGoalCreation(user, goal);
        goalRepository.create(goal.getTitle(), goal.getDescription(), goal.getParent().getId());
        saveGoalSkills(goal);
    }

    @Transactional
    public List<GoalDto> findSubtasksByGoalId(long goalId) {
        Stream<Goal> goalParents = goalRepository.findByParent(goalId);
        return goalParents
                .sorted(Comparator.comparing(Goal::getId))
                .map(goalMapper::toDto)
                .toList();
    }

    @Transactional
    public List<GoalDto> retrieveFilteredSubtasksForGoal(long goalId, GoalFilterDto goalFilterDto) {
        Stream<Goal> goalParents = goalRepository.findByParent(goalId);
        return applyFiltersToGoalsAndSort(goalFilterDto, goalParents);
    }

    @Transactional
    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
        Goal goalToUpdate = goalRepository.findGoalById(goalId)
                .orElseThrow(() -> new EntityNotFoundException("Goal not found with id: " + goalId));

        if (goalToUpdate.getStatus().equals(GoalStatus.COMPLETED)) {
            throw new DataValidationException("The goal was completed earlier");
        }

        if (goalDto.getStatus().equals(GoalStatus.COMPLETED) && !allGoalSkillsActive(goalToUpdate)) {
            throw new DataValidationException("The goal cannot be completed, the goal has inactive skills");
        }

        if (goalDto.getStatus().equals(GoalStatus.COMPLETED)) {
            List<Skill> skills = skillService.findSkillsByGoalId(goalToUpdate.getId());
            updateSkills(goalToUpdate, skills);
        }
        goalMapper.updateFromDto(goalDto, goalToUpdate);
        goalRepository.save(goalToUpdate);
        return goalMapper.toDto(goalToUpdate);
    }

    @Transactional
    public List<GoalDto> getGoalsByUser(long userId, GoalFilterDto goalFilterDto) {
        Stream<Goal> goals = goalRepository.findGoalsByUserId(userId);
        return applyFilterAndMapToDto(goalFilterDto, goals);
    }

    private void updateSkills(Goal goal, List<Skill> skills) {
        skillService.deleteAllSkills(goal.getSkillsToAchieve());
        goal.setSkillsToAchieve(skills);
        goal.getUsers().forEach(user -> user.setSkills(skills));
        goal.setSkillsToAchieve(skills);
    }

    private User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User is not found"));
    }

    private void validateGoalCreation(User user, Goal goal) {
        if (user.getGoals().size() >= MAX_ACTIVE_GOALS) {
            throw new GoalOverflowException("Maximum goal limit exceeded. Only " + MAX_ACTIVE_GOALS + " goals are allowed.");
        }
        if (!allGoalSkillsActive(goal)) {
            throw new SkillNotFoundException("Skill not exist");
        }
    }

    private boolean allGoalSkillsActive(Goal goal) {
        return goal.getSkillsToAchieve().stream()
                .map(Skill::getId)
                .allMatch(skillService::checkActiveSkill);
    }

    private List<GoalDto> applyFiltersToGoalsAndSort(GoalFilterDto goalFilterDto, Stream<Goal> goalParents) {
        return filters.stream()
                .filter(goalFilter -> goalFilter.isApplicable(goalFilterDto))
                .flatMap(goalFilter -> goalFilter.applyFilter(goalParents, goalFilterDto))
                .sorted(Comparator.comparing(Goal::getId))
                .map(goalMapper::toDto)
                .toList();
    }

    private List<GoalDto> applyFilterAndMapToDto(GoalFilterDto goalFilterDto, Stream<Goal> goals) {
        return filters.stream()
                .filter(goalFilter -> goalFilter.isApplicable(goalFilterDto))
                .flatMap(goalFilter -> goalFilter.applyFilter(goals, goalFilterDto))
                .map(goalMapper::toDto)
                .toList();
    }

    private void saveGoalSkills(Goal goal) {
        List<Skill> skills = goal.getSkillsToAchieve().stream()
                .peek(skill -> skill.getGoals().add(goal))
                .toList();
        skillService.saveAll(skills);
    }
}