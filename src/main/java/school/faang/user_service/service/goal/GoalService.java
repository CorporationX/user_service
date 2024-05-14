package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.GoalValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final SkillService skillService;
    private final GoalMapper goalMapper;
    private final GoalValidator goalValidator;
    private final UserService userService;
    private final List<GoalFilter> goalFilters;

    @Transactional
    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        goalValidator.validateBeforeCreate(userId, goalDto);

        Goal goal = goalMapper.toEntity(goalDto);

        setParent(goal, goalDto);
        setSkillToAchieve(goal, goalDto);

        User user = userService.getById(userId);
        goal.setUsers(new ArrayList<>());
        goal.getUsers().add(user);
        goal.setStatus(GoalStatus.ACTIVE);

        goalRepository.save(goal);
        return goalMapper.toDto(goal);
    }


    @Transactional
    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
        Goal goalToUpdate = getGoalById(goalId);
        goalValidator.validateBeforeUpdate(goalToUpdate, goalDto);
        setFieldsFromDto(goalToUpdate, goalDto);

        if (goalToUpdate.getStatus().equals(GoalStatus.COMPLETED)) {
            assignSkillToUser(goalToUpdate);
        }

        return goalMapper.toDto(goalToUpdate);
    }

    @Transactional(readOnly = true)
    public List<GoalDto> findSubtasksByGoalId(long goalId, GoalFilterDto filter) {
        List<GoalDto> goals = goalRepository.findByParent(goalId)
                .map(goalMapper::toDto)
                .toList();
        applyGoalFilter(goals, filter);

        return goals;
    }

    @Transactional(readOnly = true)
    public List<GoalDto> getGoalsByUserId(Long userId, GoalFilterDto filter) {
        List<GoalDto> goals = goalRepository.findGoalsByUserId(userId)
                .map(goalMapper::toDto)
                .toList();
        applyGoalFilter(goals, filter);

        return goals;
    }

    @Transactional
    public void deleteGoal(long goalId) {
        goalRepository.deleteById(goalId);
    }

    private void applyGoalFilter(List<GoalDto> goals, GoalFilterDto filter) {
        goalFilters.stream()
                .filter(goalFilter -> goalFilter.isApplicable(filter))
                .forEach(goalFilter -> goalFilter.apply(goals, filter));
    }

    private void assignSkillToUser(Goal goalToUpdate) {
        goalToUpdate.getUsers().forEach(
                (user) -> goalToUpdate.getSkillsToAchieve().forEach(
                        (skill) -> {
                            if (!user.getSkills().contains(skill)) {
                                skillService.assignSkillToUser(skill.getId(), user.getId());
                            }
                        }));
    }

    private void setParent(Goal goal, GoalDto goalDto) {
        if (Objects.nonNull(goalDto.getParentId())) {
            goalRepository.findById(goalDto.getParentId())
                    .ifPresent(goal::setParent);
        }
    }

    private void setSkillToAchieve(Goal goal, GoalDto goalDto) {
        if (Objects.nonNull(goalDto.getSkillIds())) {
            List<Skill> skills = goalDto.getSkillIds().stream()
                    .map(skillService::getSkillById)
                    .toList();
            goal.setSkillsToAchieve(skills);
        }
    }

    private void setFieldsFromDto(Goal goalToUpdate, GoalDto goalDto) {
        goalMapper.updateGoal(goalToUpdate, goalDto);
        setParent(goalToUpdate, goalDto);
        setSkillToAchieve(goalToUpdate, goalDto);
    }

    private Goal getGoalById(Long goalId) {
        return goalRepository.findById(goalId)
                .orElseThrow(() -> new EntityNotFoundException("Цель с id " + goalId + " + не найдена"));
    }
}