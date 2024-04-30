package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

@Component
@RequiredArgsConstructor
public class GoalService {
    private static final int MAX_COUNT_ACTIVE_GOALS_PER_USER = 3;

    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
//    private final SkillService skillService;
    private final GoalMapper goalMapper;

    public void createGoal(Long userID, Goal goal) {
        if (goalRepository.countActiveGoalsPerUser(userID) == MAX_COUNT_ACTIVE_GOALS_PER_USER) {
            throw new IllegalArgumentException("Maximum count of active goals per user - 3");
        }

        // проверка - цель содержит только существующие скиллы
//        skillService.getSkills();
    }

    public void updateGoal(Long userID, GoalDto goalDto) {

    }

    /*

Stream<Goal> findGoalsByUserId(long userId);

Goal create(String title, String description, long parent);

int countActiveGoalsPerUser(long userId);

Stream<Goal> findByParent(long goalId);

int countUsersSharingGoal(long goalId);

List<User> findUsersByGoalId(long goalId);

void removeSkillsFromGoal(long goalId);

void addSkillToGoal(long skillId, long goalId);

     */
}
