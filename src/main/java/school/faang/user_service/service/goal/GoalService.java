package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.GoalCompletedEventDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.event.goal.GoalSetEvent;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.publisher.goal.GoalCompletedEventPublisher;
import school.faang.user_service.publisher.goal.GoalSetEventPublisher;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.goal.GoalValidator;
import school.faang.user_service.validator.skill.SkillValidator;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final List<GoalFilter> goalFilters;
    private final GoalValidator goalValidator;
    private final SkillRepository skillRepository;
    private final SkillValidator skillValidator;
    private final UserService userService;
    private final GoalInvitationService goalInvitationService;
    private final GoalCompletedEventPublisher goalCompletedEventPublisher;
    private final GoalSetEventPublisher goalSetEventPublisher;

    @Transactional
    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        User user = userService.getUserById(userId);
        goalValidator.validateUserGoalLimit(userId);

        Goal goal = createGoalEntity(goalDto, user);

        return goalMapper.toGoalDto(goal);
    }

    @Transactional
    public GoalDto updateGoal(long goalId, GoalDto goalDto) {
        Goal existingGoal = getGoalById(goalId);

        Goal updatedGoal = updateGoalEntity(existingGoal, goalDto);

        return goalMapper.toGoalDto(updatedGoal);
    }

    @Transactional
    public void deleteGoal(long goalId) {
        goalRepository.deleteById(goalId);
    }

    @Transactional
    public List<GoalDto> findSubtasksByGoalId(long goalId, GoalFilterDto filterDto) {
        Stream<Goal> goals = goalRepository.findByParent(goalId);

        return filterGoals(goals, filterDto);
    }

    @Transactional
    public List<GoalDto> getGoalsByUser(long userId, GoalFilterDto filterDto) {
        Stream<Goal> goals = goalRepository.findGoalsByUserId(userId);

        return filterGoals(goals, filterDto);
    }

    private Goal getGoalById(long goalId) {
        return goalRepository.findById(goalId)
                .orElseThrow(() -> new EntityNotFoundException("Goal with this id does not exist in the database"));
    }

    private Goal createGoalEntity(GoalDto goalDto, User user) {
        Goal goal = goalRepository.create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId());

        goal.getUsers().add(user);
        setSkillsToGoal(goalDto.getSkillIds(), goal);

        goalRepository.save(goal);
        notifyAboutGoalSet(goal.getId(), user.getId());
        return goal;
    }

    private Goal updateGoalEntity(Goal existingGoal, GoalDto goalDto) {
        goalValidator.validateGoalStatusNotCompleted(existingGoal);

        existingGoal.setTitle(goalDto.getTitle());
        existingGoal.setDescription(goalDto.getDescription());

        GoalStatus status = goalDto.getStatus();
        if (status != null) {
            existingGoal.setStatus(status);
        }

        Long parentId = goalDto.getParentId();
        if (parentId != null) {
            Goal parentGoal = getGoalById(parentId);
            existingGoal.setParent(parentGoal);
        }

        setSkillsToGoal(goalDto.getSkillIds(), existingGoal);

        goalRepository.save(existingGoal);

        if (goalDto.getStatus().equals(GoalStatus.COMPLETED)) {
            notifyUsersAboutCompletedGoal(existingGoal);
        }
        return existingGoal;
    }

    private void setSkillsToGoal(List<Long> skillIds, Goal goal) {
        if (skillIds != null) {
            skillValidator.validateAllSkillsIdsExist(skillIds);
            List<Skill> skills = skillRepository.findAllById(skillIds);
            goal.setSkillsToAchieve(skills);
        }
    }

    private List<GoalDto> filterGoals(Stream<Goal> goals, GoalFilterDto filterDto) {
        return goalFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(goals, (currentGoals, filter) -> filter.apply(currentGoals, filterDto), (s1, s2) -> s1)
                .map(goalMapper::toGoalDto)
                .toList();
    }

    public void deactivateActiveUserGoals(User user) {
        user.getGoals().stream()
                .filter(goal -> goal.getStatus().equals(GoalStatus.ACTIVE))
                .forEach(goal -> {
                    List<GoalInvitation> goalInvitations = goal.getInvitations();

                    goal.getUsers().remove(user);
                    if (goal.getUsers().isEmpty()) {
                        goalInvitationService.deleteGoalInvitations(goalInvitations);
                        goalRepository.deleteById(goal.getId());
                    } else {
                        goalInvitationService.deleteGoalInvitationForUser(goalInvitations, user);
                    }
                });

        user.getGoals().clear();
    }

    private void notifyUsersAboutCompletedGoal(Goal goal) {
        goal.getUsers().parallelStream()
                .forEach(user -> {
                    log.debug("Sending notification to user - {}", user.getUsername());
                    GoalCompletedEventDto event = GoalCompletedEventDto.builder()
                            .goalId(goal.getId())
                            .userId(user.getId())
                            .goalName(goal.getTitle())
                            .build();

                    goalCompletedEventPublisher.publish(event);
                });
    }

    private void notifyAboutGoalSet(Long goalId, Long userId) {
        log.debug("Sending notification about goal set with userId - {0}, and goalId - {1}", userId, goalId);
        GoalSetEvent event = GoalSetEvent.builder()
                .goalId(goalId)
                .userId(userId)
                .build();

        goalSetEventPublisher.publish(event);
    }
}
