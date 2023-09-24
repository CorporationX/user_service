package school.faang.user_service.service.goal;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.GoalNotFoundException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.filter.goal.dto.GoalFilterDto;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.messaging.GoalCompletedEventPublisher;
import school.faang.user_service.messaging.events.GoalCompletedEvent;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.util.Message;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalService {
    private static final int MAX_ACTIVE_GOALS = 3;
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final GoalMapper goalMapper;
    private final List<GoalFilter> goalFilters;
    private final GoalCompletedEventPublisher goalCompletedEventPublisher;

    @Transactional
    public GoalDto createGoal(GoalDto goal, Long userId) {
        int currentUserGoalNum = goalRepository.countActiveGoalsPerUser(userId);
        boolean allSkillsExist = goal.getSkills().stream()
                .allMatch(skill -> skillRepository.findByTitle(skill.toLowerCase()).isPresent());

        if (!allSkillsExist) {
            throw new IllegalArgumentException(Message.UNEXISTING_SKILLS);
        }
        if (currentUserGoalNum > MAX_ACTIVE_GOALS){
            throw new IllegalArgumentException(Message.TOO_MANY_GOALS);
        }

        goalRepository.save(goalMapper.goalToEntity(goal));

        return goal;
    }

    @Transactional
    public GoalDto updateGoal(GoalDto goalDto, Long userId) {
         Goal goal = goalRepository.findById(goalDto.getId())
               .orElseThrow(() -> new GoalNotFoundException(userId));

        goal.setTitle(goalDto.getTitle());
        goal.setUpdatedAt(LocalDateTime.now());
        goalRepository.save(goal);

        return goalMapper.goalToDto(goal);
    }

    @Transactional
    public void deleteGoal(Long goalId){
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() ->
                        new GoalNotFoundException(goalId));

        goalRepository.delete(goal);
    }

    public List<GoalDto> findSubtasksByGoalId(Long parentGoalId, GoalFilterDto goalFilterDto){
        return applyFilter(goalRepository.findByParent(parentGoalId), goalFilterDto);
    }

    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto goalFilterDto){
        List<Goal> goals = goalRepository.findGoalsByUserId(userId)
                        .peek(goal -> goal.setSkillsToAchieve(skillRepository.findSkillsByGoalId(goal.getId())))
                .toList();

        return applyFilter(goals.stream(), goalFilterDto);
    }

    private List<GoalDto> applyFilter(Stream<Goal> goals, GoalFilterDto goalFilterDto){
        return goalFilters.stream()
                .filter(goalFilter -> goalFilter.isApplicable(goalFilterDto))
                .flatMap(goalFilter -> goalFilter.apply(goals, goalFilterDto))
                .map(goalMapper::goalToDto)
                .toList();
    }

    @Transactional
    public GoalDto completeGoal(Long goalId){
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() ->
                        new GoalNotFoundException(goalId));
        goal.setStatus(GoalStatus.COMPLETED);
        goalRepository.save(goal);
        goalCompletedEventPublisher.publish(new GoalCompletedEvent(goalId));

        return goalMapper.goalToDto(goal);
    }
}