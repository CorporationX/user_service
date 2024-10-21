package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalCompletedEvent;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.KafkaPublisherService;
import school.faang.user_service.validator.GoalValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {
    private static final int GOALS_PER_USER = 3;

    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final GoalValidator goalValidator;
    private final List<GoalFilter> goalFilters;
    private final SkillRepository skillRepository;
    private final KafkaPublisherService kafkaPublisherService;

    @Override
    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        goalValidator.validateGoalTitle(goalDto);
        validateGoalsPerUser(userId);
        validateGoalSkills(goalDto.getSkillIds());

        Goal goal = goalMapper.toEntity(goalDto);
        goal.setParent(goalRepository.findById(goalDto.getParentId()).orElse(null));
        Goal createdGoal = goalRepository.save(goal);

        goalDto.getSkillIds().forEach(id -> goalRepository.addSkillToGoal(id, createdGoal.getId()));
        goalRepository.addGoalToUser(userId, createdGoal.getId());

        sendAndCollectionEvent(goalDto);

        return goalMapper.toDto(createdGoal);
    }

    @Override
    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
        goalValidator.validateGoalTitle(goalDto);
        validateGoalSkills(goalDto.getSkillIds());

        Goal goalToUpdate = goalRepository.findById(goalId)
                .orElseThrow(() -> new DataValidationException("Goal does not exist"));

        if (isToBeCompleted(goalToUpdate, goalDto)) {
            goalToUpdate.setStatus(goalDto.getStatus());
            goalRepository.findUsersByGoalId(goalToUpdate.getId()).forEach(user -> {
                for (Long skillId : goalDto.getSkillIds()) {
                    skillRepository.assignSkillToUser(user.getId(), skillId);
                }
            });
        }

        updatingFields(goalToUpdate, goalDto);

        goalToUpdate.setUpdatedAt(LocalDateTime.now());
        return goalMapper.toDto(goalRepository.save(goalToUpdate));
    }

    @Override
    public void deleteGoal(Long goalId) {
        goalRepository.deleteById(goalId);
    }

    @Override
    public List<GoalDto> findSubtasksByGoalId(long goalId, GoalFilterDto filters) {
        Stream<Goal> goals = goalRepository.findByParent(goalId);
        goalFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(filters, goals));
        return goals.map(goalMapper::toDto).toList();
    }

    @Override
    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filters) {
        Stream<Goal> goals = goalRepository.findGoalsByUserId(userId);
        goalFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(filters, goals));
        return goals.map(goalMapper::toDto).toList();
    }

    private void sendAndCollectionEvent(GoalDto goalDto) {
        GoalCompletedEvent event = new GoalCompletedEvent();
        event.setGoalId(goalDto.getId());
        event.setUserId(goalDto.getParentId());
        kafkaPublisherService.sendMessage(event);
    }

    private void validateGoalsPerUser(Long userId) throws DataValidationException {
        if (goalRepository.countActiveGoalsPerUser(userId) >= GOALS_PER_USER) {
            throw new DataValidationException("There cannot be more than " + GOALS_PER_USER + " active goals per user");
        }
    }

    private void validateGoalSkills(List<Long> skillIds) throws DataValidationException {
        if (skillRepository.countExisting(skillIds) != skillIds.size()) {
            throw new DataValidationException("Cannot create goal with non-existent skills");
        }
    }

    private boolean isToBeCompleted(Goal goalToUpdate, GoalDto goalDto) {
        return goalToUpdate.getStatus() != GoalStatus.COMPLETED
                && goalDto.getStatus() == GoalStatus.COMPLETED;
    }

    private void updatingFields(Goal goalToUpdate, GoalDto goalDto) {
        goalToUpdate.setTitle(goalDto.getTitle());
        if (goalDto.getDescription() != null) {
            goalToUpdate.setDescription(goalDto.getDescription());
        }
        if (goalDto.getStatus() != null) {
            goalToUpdate.setStatus(goalDto.getStatus());
        }
        if (goalDto.getDeadline() != null) {
            goalToUpdate.setDeadline(goalDto.getDeadline());
        }
        updateSkillsToAchieve(goalToUpdate, goalDto.getSkillIds());
    }

    private void updateSkillsToAchieve(Goal goal, List<Long> skillIds) {
        goal.setSkillsToAchieve(skillIds.stream()
                .map(skillRepository::getReferenceById)
                .toList());
        skillIds.forEach(id -> goalRepository.addSkillToGoal(id, goal.getId()));
    }
}