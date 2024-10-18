package school.faang.user_service.service.impl.goal;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.model.dto.goal.GoalDto;
import school.faang.user_service.model.dto.goal.GoalFilterDto;
import school.faang.user_service.model.dto.goal.GoalNotificationDto;
import school.faang.user_service.model.entity.Skill;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.entity.goal.Goal;
import school.faang.user_service.model.enums.GoalStatus;
import school.faang.user_service.model.event.GoalCompletedEvent;
import school.faang.user_service.publisher.GoalCompletedEventPublisher;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.GoalService;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.validator.goal.GoalValidator;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {
    private final GoalRepository goalRepository;
    private final SkillService skillService;
    private final GoalValidator goalValidator;
    private final List<GoalFilter> filters;
    private final GoalMapper goalMapper;
    private final UserContext userContext;
    private final GoalCompletedEventPublisher goalCompletedEventPublisher;

    @Override
    @Transactional
    public GoalDto createGoal(long userId, GoalDto goalDto) {
        goalValidator.validateCreationGoal(userId);

        List<Skill> skills = skillService.getSkillsByIds(goalDto.skillIds());

        Goal saveGoal = goalRepository.create(goalDto.title(),
                goalDto.description(),
                goalDto.parentId());
        saveGoal.setSkillsToAchieve(skills);
        goalRepository.save(saveGoal);
        return goalMapper.toDto(saveGoal);
    }

    @Override
    @Transactional
    public GoalDto updateGoal(long goalId, GoalDto goalDto) {
        Goal goal = goalValidator.validateUpdate(goalId);
        List<Skill> skills = skillService.getSkillsByIds(goalDto.skillIds());
        assignNewSkillToGoal(goal, skills, goalDto);

        if (goal.getStatus() == GoalStatus.COMPLETED) {
            assignGoalSkillsToUsers(goalId, skills);
            sendEvent(goalId);
        }
        return goalMapper.toDto(goal);
    }

    private void assignGoalSkillsToUsers(long goalId, List<Skill> skills) {
        List<User> users = goalRepository.findUsersByGoalId(goalId);
        users.forEach(user -> skills
                .forEach(skill -> skillService.assignSkillToUser(skill.getId(), user.getId())));
    }

    private void assignNewSkillToGoal(Goal goal, List<Skill> newSkills, GoalDto goalDto) {
        goalRepository.deleteSkillsByGoalId(goal.getId());
        goal.setSkillsToAchieve(newSkills);
        goal.setStatus(goalDto.status());
        goalRepository.save(goal);
    }

    @Override
    public void deleteGoal(long goalId) {
        goalRepository.deleteById(goalId);
    }

    @Override
    public List<GoalDto> findSubtasksByGoalId(long goalId, GoalFilterDto filterDto) {
        List<Goal> goals = goalRepository.findByParent(goalId).toList();
        return goalMapper.toDto(getGoalAfterFilters(goals, filterDto));
    }

    @Override
    public List<GoalDto> getGoalsByUser(long userId, GoalFilterDto filterDto) {
        List<Goal> goals = goalRepository.findGoalsByUserId(userId).toList();
        return goalMapper.toDto(getGoalAfterFilters(goals, filterDto));
    }

    private List<Goal> getGoalAfterFilters(List<Goal> goals, GoalFilterDto filterDto) {
        return filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(goals.stream(), (stream, filter) ->
                        filter.apply(stream, filterDto), (s1, s2) -> s1).toList();
    }

    @Override
    @Transactional
    public void removeGoals(List<Goal> goals) {
        goalRepository.deleteAll(goals);
    }

    @Override
    public GoalNotificationDto getGoal(long goalId) {
        Goal goal = goalRepository.findById(goalId).orElseThrow(() ->
                new EntityNotFoundException("Goal with id %d doesn't exist".formatted(goalId)));

        return goalMapper.toGoalNotificationDto(goal);
    }

    private void sendEvent(long goalId) {
        GoalCompletedEvent event = GoalCompletedEvent.builder()
                .goalId(goalId)
                .userId(userContext.getUserId())
                .completedAt(LocalDateTime.now())
                .build();

        goalCompletedEventPublisher.publish(event);
    }
}
