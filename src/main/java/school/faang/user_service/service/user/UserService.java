package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final GoalService goalService;

  public boolean isUserExist(Long userId) {
    return userRepository.existsById(userId);
  }

  private boolean isOnlyWithOneCurrentUser(GoalDto goal, Long userId) {
    List<Long> goalIds = goal.getUserIds();
    return goalIds.size() == 1 && Objects.equals(goalIds.get(0), userId);
  }

  private void stopUserGoals(Long userId) {
    List<Long> userGoalsForDeleting = new ArrayList<>();
    List<Long> userGoalsForUpdating = new ArrayList<>();

    List<GoalDto> allGoals = goalService.getGoalsByUser(userId);

    for (GoalDto goal : allGoals) {
      if (isOnlyWithOneCurrentUser(goal, userId)) {
        userGoalsForDeleting.add(goal.getId());
      } else {
        userGoalsForUpdating.add(goal.getId());
      }
    }

    goalService.deleteAllByIds(userGoalsForDeleting);
    goalService.removeUserFromGoals(userGoalsForUpdating, userId);
  }


  public void deactivateUser(Long userId) {
    stopUserGoals(userId);
  }
}
