package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final GoalMapper goalMapper;
    private final int MAX_ACTIVE_GOALS = 3;

    public List<Goal> findGoalsByUserId(Long id) {
        if (id == null) throw new IllegalArgumentException("userId can not be Null");
        if (id < 1) throw new IllegalArgumentException("userId can not be less than 1");
        return goalRepository.findGoalsByUserId(id)
                .peek(goal -> goal.setSkillsToAchieve(skillRepository.findSkillsByGoalId(goal.getId()))).toList();
    }

    public List<Goal> getGoalsByUser(Long userId, GoalFilterDto filter) {
        if (filter == null) {
            return findGoalsByUserId(userId);
        }
        return findGoalsByUserId(userId).stream()
                .map(goalMapper::toDto)
                .filter(goalDto -> {
                    if (filter.getDescription() != null && !filter.getDescription().isBlank()) {
                        return filter.getDescription().equals(goalDto.getDescription());
                    }
                    return true;})
                .filter(goalDto -> {
                    if (filter.getParentId() != null) {
                        return filter.getParentId().equals(goalDto.getParentId());
                    }
                    return true;})
                .filter(goalDto -> {
                    if (filter.getTitle() != null && !filter.getTitle().isBlank()) {
                        return filter.getTitle().equals(goalDto.getTitle());
                    }
                    return true;})
                .filter(goalDto -> {
                    if (filter.getStatus() != null) {
                        return filter.getStatus().equals(goalDto.getStatus());
                    }
                    return true;})
                .filter(goalDto -> {
                    if (filter.getSkillIds() != null) {
                        for (int i = 0; i < filter.getSkillIds().size(); ++i) {
                            if (goalDto.getSkillIds().contains(filter.getSkillIds().get(i))) {
                                return true;
                            }
                        }
                    }
                    return true;})
                .map(goalMapper::toEntity)
                .toList();
    }

    public void createGoal(Long userId, Goal goal) {
        int activeGoalsCount = goalRepository.countActiveGoalsPerUser(userId);
        if (activeGoalsCount >= MAX_ACTIVE_GOALS ) {
            throw new IllegalArgumentException("Goal cannot be saved because MAX_ACTIVE_GOALS = "
                    + MAX_ACTIVE_GOALS + " and current active goals = "
                    + activeGoalsCount);
        }
        GoalDto goalDto = goalMapper.toDto(goal);
        if (skillRepository.countExisting(goalDto.getSkillIds()) != goalDto.getSkillIds().size()) {
            throw new IllegalArgumentException("Goal contains non-existent skill");
        }
        goalRepository.create(goal.getTitle(), goal.getDescription(), goal.getParent().getId());
    }
}
