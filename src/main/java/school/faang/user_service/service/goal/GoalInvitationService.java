package school.faang.user_service.service.goal;

import static school.faang.user_service.exception.ExceptionMessages.DELETION_ERROR_MESSAGE;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.AopInvocationException;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.goal.GoalRepository;

/**
 * Класс-сервис, который отвечает за бизнес-логику управления приглашения для цели.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GoalInvitationService {

  private static final String MESSAGE_ABOUT_DELETE_GOAL_INVITATIONS
      = "Все приглашения для целей были удалены.";

  private final GoalRepository goalRepository;

  /**
   * Метод для удаления отправленных или полученных целей пользователя.
   * @param userId id пользователя, чей аккаунт деактивируется.
   */
  public void deleteGoalInvitations(long userId) {
    try {
      goalRepository.deleteAllGoalInvitationById(userId);
      log.info(MESSAGE_ABOUT_DELETE_GOAL_INVITATIONS);
    } catch (AopInvocationException e) {
      log.error(DELETION_ERROR_MESSAGE, e);
    }
  }

}
