package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.component.DeletionDataComponent;
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
  private final DeletionDataComponent deletionDataComponent;

  /**
   * Метод для удаления отправленных или полученных целей пользователя.
   * @param userId id пользователя, чей аккаунт деактивируется.
   */
  public void deleteGoalInvitations(long userId) {
    deletionDataComponent.deleteData(() -> goalRepository.deleteAllGoalInvitationById(userId));
    log.info(MESSAGE_ABOUT_DELETE_GOAL_INVITATIONS);
  }

}
