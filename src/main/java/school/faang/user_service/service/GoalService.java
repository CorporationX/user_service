package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.GoalValidator;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final UserService userService;
    private final GoalRepository goalRepository;
    private final SkillService skillService;
    private final SkillRepository skillRepository;
    private final GoalMapper goalMapper;
    private final List<GoalFilter> filters;
    private final GoalValidator validator;

    @Transactional(readOnly = true)
    public GoalDto getGoalById(Long goalId) {
        return goalMapper.toDto(getGoal(goalId));
    }

    @Transactional(readOnly = true)
    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filter) {
        List<GoalDto> goals = goalRepository.findGoalsByUserId(userId)
                .map(goalMapper::toDto)
                .collect(Collectors.toList());

        filterGoals(goals, filter);
        return goals;
    }

    @Transactional(readOnly = true)
    public List<GoalDto> getGoalsByParent(Long parentId, GoalFilterDto filter) {
        List<GoalDto> goals = goalRepository.findByParent(parentId)
                .map(goalMapper::toDto)
                .collect(Collectors.toList());

        filterGoals(goals, filter);
        return goals;
    }

    @Transactional
    public GoalDto create(Long userId, GoalDto goalDto) {
        validator.validateToCreate(userId, goalDto);

        Goal goal = goalMapper.toEntity(goalDto);
        User user = userService.getUser(userId);

        user.getGoals().add(goal);
        setParent(goal, goalDto);
        setSkills(goal, goalDto);
        goal.setStatus(GoalStatus.ACTIVE);

        goal = goalRepository.save(goal);
        return goalMapper.toDto(goal);
    }

    @Transactional
    public GoalDto update(Long goalId, GoalDto goalDto) {
        Goal goal = getGoal(goalId);
        validator.validateToUpdate(goal, goalDto);
        updateFields(goal, goalDto);

        return goalMapper.toDto(goal);
    }

    @Transactional
    public void delete(Long goalId) {
        goalRepository.deleteById(goalId);
    }

    private void setParent(Goal goal, GoalDto goalDto) {
        if (Objects.nonNull(goalDto.getParentId())) {
            goalRepository.findById(goalDto.getParentId())
                    .ifPresent(goal::setParent);
        }
    }

    private void setSkills(Goal goal, GoalDto goalDto) {
        if (Objects.nonNull(goalDto.getSkillIds())) {
            List<Skill> skills = goalDto.getSkillIds().stream()
                    .map(skillService::getSKill)
                    .toList();
            goal.setSkillsToAchieve(skills);
        }
    }

    private void updateFields(Goal goal, GoalDto goalDto) {
        goal.setTitle(goalDto.getTitle());
        setParent(goal, goalDto);
        setSkills(goal, goalDto);
        if (Objects.nonNull(goalDto.getDescription())) {
            goal.setDescription(goalDto.getDescription());
        }
        if (Objects.nonNull(goalDto.getStatus()) && goalDto.getStatus().equals(GoalStatus.COMPLETED)) {
            goal.setStatus(goalDto.getStatus());
            assignSkillsToUsers(goal);
        }
    }

    private void assignSkillsToUsers(Goal goal) {
        goal.getSkillsToAchieve().forEach(skill -> goal.getUsers().forEach(user -> {
            if (!user.getSkills().contains(skill)) {
                skillRepository.assignSkillToUser(skill.getId(), user.getId());
            }
        }));
    }

    private void filterGoals(List<GoalDto> goals, GoalFilterDto filterDto) {
        filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .forEach(filter -> filter.apply(goals, filterDto));
    }

    private Goal getGoal(Long goalId) {
        return goalRepository.findById(goalId)
                .orElseThrow(() -> new EntityNotFoundException("Goal with id " + goalId + " does not exist"));
    }
}
