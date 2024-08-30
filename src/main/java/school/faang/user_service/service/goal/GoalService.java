package school.faang.user_service.service.goal;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;

/**
 * Класс-сервис, который отвечает за бизнес-логику управления целями.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GoalService {

  private static final String MESSAGE_ABOUT_REMOVE_MENTOR_FROM_HIS_GOALS
      = "Пользователь был удален из списка своих менти.";

  /**
   * Метод для удаления пользователя из целей, которые он поставил и являлся ментором.
   * @param user пользователь, чей аккаунт деактивируется.
   */
  public void removeMentorFromHisGoals(User user) {
    Optional.ofNullable(user.getSetGoals())
        .ifPresent(goal -> {
          goal.forEach(currentGoal -> currentGoal.setMentor(null));
          log.info(MESSAGE_ABOUT_REMOVE_MENTOR_FROM_HIS_GOALS);
        });
  }

}
