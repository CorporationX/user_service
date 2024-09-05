package school.faang.user_service.service.goal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.event.goal.GoalSetEvent;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.publisher.goal.GoalEventPublisher;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.filter.GoalFilter;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.validator.goal.GoalValidator;

import java.util.List;
import java.util.stream.Stream;

@Service
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillService skillService;
    private final GoalValidator goalValidator;
    private final GoalMapper goalMapper;
    private final List<GoalFilter> goalFilters;
    private final GoalEventPublisher goalEventPublisher;

    @Autowired
    public GoalService(GoalRepository goalRepository, SkillService skillService, GoalValidator goalValidator, GoalMapper goalMapper, List<GoalFilter> goalFilters, GoalEventPublisher goalEventPublisher) {
        this.goalRepository = goalRepository;
        this.skillService = skillService;
        this.goalValidator = goalValidator;
        this.goalMapper = goalMapper;
        this.goalFilters = goalFilters;
        this.goalEventPublisher = goalEventPublisher;
    }

    @Transactional
    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        goalValidator.createGoalValidator(userId, goalDto);

        Goal saveGoal = goalRepository.create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentGoalId());

        goalDto.getSkillIds().forEach(skillId -> goalRepository.addSkillToGoal(saveGoal.getId(), skillId));

        GoalSetEvent goalSetEvent = new GoalSetEvent();
        goalSetEvent.setUserId(userId);
        goalSetEvent.setGoalId(goalDto.getId());

        goalEventPublisher.sendEvent(goalSetEvent);
        return goalMapper.toDto(saveGoal);
    }

    @Transactional
    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
        goalValidator.updateGoalValidator(goalId, goalDto);

        Goal goal = goalMapper.toEntity(goalDto);

        Goal savedGoal = goalRepository.save(goal);

        goalRepository.removeSkillsFromGoal(goalId);
        goalDto.getSkillIds().forEach(skillId -> goalRepository.addSkillToGoal(savedGoal.getId(), skillId));

        if (goalDto.getStatus().equals("completed")) {
            goalRepository.findUsersByGoalId(goalId).forEach(user -> goalDto.getSkillIds().forEach(skillId -> skillService.assignSkillToUser(skillId, user.getId())));
        }

        return goalMapper.toDto(savedGoal);
    }

    @Transactional
    public void deleteGoal(long goalId) {
        goalValidator.deleteGoalValidator(goalId);

        goalRepository.removeSkillsFromGoal(goalId);
        goalRepository.deleteById(goalId);
    }

    public List<GoalDto> findSubtasksByGoalId(Long goalId, GoalFilterDto filters) {
        Stream<Goal> subtasks = goalRepository.findByParent(goalId);

        return goalFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(subtasks, filters))
                .map(goalMapper::toDto)
                .toList();
    }

    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filters) {
        Stream<Goal> goals = goalRepository.findGoalsByUserId(userId);

        return goalFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(goals, filters))
                .map(goalMapper::toDto)
                .toList();
    }

}