package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.ValidationException;
import school.faang.user_service.filter.GoalFilter;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.GoalValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final GoalValidator goalValidator;
    private final List<GoalFilter> goalFilters;
    private final SkillRepository skillRepository;

    @Transactional
    public GoalDto createGoal(Long userId, GoalDto goalDto) throws ValidationException {
        goalValidator.validateGoalTitle(goalDto);
        goalValidator.validateGoalsPerUser(userId);
        goalValidator.validateGoalSkills(goalDto.getSkillIds());

        Goal goalEntity = goalMapper.toEntity(goalDto);
        goalEntity.setParent(goalRepository.getReferenceById(goalDto.getParentId()));
        goalEntity.setSkillsToAchieve(goalDto.getSkillIds().stream()
                .map(skillRepository::getReferenceById)
                .toList());

        Goal createdGoal = goalRepository.save(goalEntity);
        goalDto.getSkillIds().forEach(id -> goalRepository.addSkillToGoal(id, createdGoal.getId()));

        goalRepository.addGoalTuUser(userId, createdGoal.getId());

        return goalMapper.toDto(createdGoal);
    }

    private boolean isToComplete(Long goalId, GoalDto goalDto) {
        return goalRepository.getReferenceById(goalId).getStatus() != GoalStatus.COMPLETED
                && goalDto.getStatus() == GoalStatus.COMPLETED;
    }

    private void updateSkillsToAchieve(Goal goal, List<Long> skillIds) {
        goal.setSkillsToAchieve(skillIds.stream()
                .map(skillRepository::getReferenceById)
                .toList());
        skillIds.forEach(id -> goalRepository.addSkillToGoal(id, goal.getId()));
    }

    @Transactional
    public GoalDto updateGoal(Long goalId, GoalDto goalDto) throws ValidationException {
        goalValidator.validateGoalTitle(goalDto);
        goalValidator.validateGoalSkills(goalDto.getSkillIds());

        Goal goalToUpdate = goalRepository.findById(goalId).orElseThrow(EntityNotFoundException::new);
        goalToUpdate.setTitle(goalDto.getTitle());
        if (!goalDto.getDescription().isEmpty()) {
            goalToUpdate.setDescription(goalDto.getDescription());
        }
        if (!goalDto.getSkillIds().isEmpty()) {
            updateSkillsToAchieve(goalToUpdate, goalDto.getSkillIds());
        }

        if (isToComplete(goalId, goalDto)) {
            goalToUpdate.setStatus(GoalStatus.COMPLETED);
            goalRepository.findUsersByGoalId(goalId).forEach(user -> {
                for (Long skillId : goalDto.getSkillIds()) {
                    skillRepository.assignSkillToUser(user.getId(), skillId);
                }
            });
        }
        goalToUpdate.setUpdatedAt(LocalDateTime.now());

        return goalMapper.toDto(goalRepository.save(goalToUpdate));
    }

    public void deleteGoal(Long goalId) {
        goalRepository.deleteById(goalId);
    }

    public List<GoalDto> findSubtasksByGoalId(long goalId, GoalFilterDto filters) {
        Stream<Goal> goals = goalRepository.findByParent(goalId);
        goalFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(filters, goals));
        return goals.map(goalMapper::toDto).toList();
    }

    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filters) {
        Stream<Goal> goals = goalRepository.findGoalsByUserId(userId);
        goalFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(filters, goals));
        return goals.map(goalMapper::toDto).toList();
    }
}