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
    private final SkillRepository skilllRepository;
    private final GoalMapper goalMapper;
    private final int MAX_ACTIVE_GOALS = 3;

    // return all user's goals
    public List<Goal> findGoalsByUserId(Long id) {
        if (id == null) throw new IllegalArgumentException("userId can not be Null");
        if (id < 1) throw new IllegalArgumentException("userId can not be less than 1");
        return goalRepository.findGoalsByUserId(id)
                .peek(goal -> goal.setSkillsToAchieve(skilllRepository.findSkillsByGoalId(goal.getId()))).toList();
    }

    // return all user's goals with filter
    public List<Goal> getGoalsByUser(Long userId, GoalFilterDto filter) {
        if (filter == null) return findGoalsByUserId(userId);  // filter == null => return All User Goals
        return findGoalsByUserId(userId).stream()
                .map(goalMapper::toDto)
                .filter(goalDto -> {                                                                          // Description Filter
                    if (filter.getDescription() != null && !filter.getDescription().isBlank()) {
                        return filter.getDescription().equals(goalDto.getDescription());
                    }
                    return true;})
                .filter(goalDto -> {                                                                          // ParentId Filter
                    if (filter.getParentId() != null) {
                        return filter.getParentId().equals(goalDto.getParentId());
                    }
                    return true;})
                .filter(goalDto -> {                                                                          // Title Filter
                    if (filter.getTitle() != null && !filter.getTitle().isBlank()) {
                        return filter.getTitle().equals(goalDto.getTitle());
                    }
                    return true;})
                .filter(goalDto -> {                                                                          // Status Filter
                    if (filter.getStatus() != null) {
                        return filter.getStatus().equals(goalDto.getStatus());
                    }
                    return true;})
                .filter(goalDto -> {                                                                          // SkillIds Filter
                    if (filter.getSkillIds() != null) {
                        if (filter.getSkillIds().size() != goalDto.getSkillIds().size()) {
                            return false;
                        } else {
                            for (int i = 0; i < filter.getSkillIds().size(); ++i) {
                                if (filter.getSkillIds().get(i) != goalDto.getSkillIds().get(i)) {
                                    return false;
                                }
                            }
                            return true;
                        }
                    }
                    return true;})
                .map(goalMapper::toEntity)
                .toList();
    }

    // Create goal
    public void createGoal(Long userId, Goal goal) {
        int activeGoalsCount = goalRepository.countActiveGoalsPerUser(userId);
        if (activeGoalsCount >= MAX_ACTIVE_GOALS ) {
            throw new IllegalArgumentException("Goal cannot be saved because MAX_ACTIVE_GOALS = "
                    + MAX_ACTIVE_GOALS + " and current active goals = "
                    + activeGoalsCount);
        }
        GoalDto goalDto = goalMapper.toDto(goal);
        if (skilllRepository.countExisting(goalDto.getSkillIds()) != goalDto.getSkillIds().size()) {
            throw new IllegalArgumentException("Goal contains non-existent skill");
        }
        goalRepository.create(goal.getTitle(), goal.getDescription(), goal.getParent().getId());
        goalRepository.findGoalsByUserId(userId).toList().add(goal);
    }
}
