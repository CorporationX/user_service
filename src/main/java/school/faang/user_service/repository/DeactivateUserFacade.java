package school.faang.user_service.repository;

import static school.faang.user_service.exception.ExceptionMessages.DELETION_ERROR_MESSAGE;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.AopInvocationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.user.UserNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

/**
 * Класс реализации всей логики по деактивации пользователя.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeactivateUserFacade {

  private static final String PREMIUM_STATUS_ACTION
      = "Имеется премиум подписка, которая действует до %s года.";
  private static final String MESSAGE_ABOUT_CANCEL_USER_GOALS
      = "Цели пользователя были отменены.";
  private static final String MESSAGE_ABOUT_CANCEL_USER_EVENTS
      = "Назначенные события пользователя были отменены.";
  private static final String MESSAGE_ABOUT_DELETE_GOAL_INVITATIONS
      = "Все приглашения для целей были удалены.";
  private static final String MESSAGE_ABOUT_DELETE_MENTORSHIP_REQUESTS
      = "Все запросы на менторство/менти были удалены.";
  private static final String MESSAGE_ABOUT_REMOVE_MENTOR_FROM_HIS_GOALS
      = "Пользователь был удален из списка своих менти.";
  private static final String MESSAGE_ABOUT_REMOVE_MENTOR_FROM_MENTEE_LIST
      = "Пользователь был удален из целей, которые он создал.";

  private final UserRepository userRepository;
  private final GoalRepository goalRepository;
  private final MentorshipRequestRepository mentorshipRequestRepository;
  private final UserMapper userMapper;

  /**
   * Метод для деактивации пользователя.
   * @param userId  id пользователя, чей аккаунт деактивируется.
   * @return
   */
  @Transactional
  public UserDto deactivateUser(long userId) {
    final User user = getUser(userId);
    beforeDeactivateUser(user);
    user.setActive(Boolean.FALSE);
    final User actualUser = userRepository.save(user);
    afterDeactivateUser(user);
    return userMapper.userDtoFromUser(actualUser);
  }

  /**
   * Метод для получения деактивируемого пользователя.
   * @param userId id пользователя, чей аккаунт деактивируется.
   * @return
   */
  private User getUser(long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(String.valueOf(userId)));
  }

  /**
   * Метод для выполнения операций над активностями пользователя перед его деактивацией.
   * @param user пользователь, чей аккаунт деактивируется.
   */
  private void beforeDeactivateUser(User user) {
    cancelUserGoals(user);
    cancelUserEvents(user);
    deleteGoalInvitations(user.getId());
    deleteMentorshipRequests(user.getId());
  }

  /**
   * Метод для выполнения операций менторства пользователя после его деактивацией.
   * @param user пользователь, чей аккаунт деактивируется.
   */
  private void afterDeactivateUser(User user) {
    removeMentorFromMenteeList(user);
    removeMentorFromHisGoals(user);
  }

  /**
   * Метод для отмены целей при условии, что её не выполняют другие пользователи.
   * @param user пользователь, чей аккаунт деактивируется.
   */
  private void cancelUserGoals(User user) {
    Optional.ofNullable(user.getGoals())
            .ifPresent(goal -> {
              goal.forEach(currentGoal -> {
                final Long id = user.getId();
                boolean result = currentGoal.getUsers().stream()
                    .anyMatch(currentUser -> !id.equals(currentUser.getId()));
                if (Boolean.FALSE.equals(result)) {
                  currentGoal.setStatus(GoalStatus.CANCELED);
                }
              });
              log.info(MESSAGE_ABOUT_CANCEL_USER_GOALS);
            });
  }

  /**
   * Метод для отмены всех запланированных пользователем событий.
   * @param user пользователь, чей аккаунт деактивируется.
   */
  private void cancelUserEvents(User user) {
    Optional.ofNullable(user.getOwnedEvents())
        .ifPresent(event -> {
          event.forEach(currentEvent -> {
            if (!EventStatus.CANCELED.equals(currentEvent.getStatus())) {
              currentEvent.setStatus(EventStatus.CANCELED);
            }
          });
          log.info(MESSAGE_ABOUT_CANCEL_USER_EVENTS);
        });
  }

  /**
   * Метод для удаления отправленных или полученных целей пользователя.
   * @param userId id пользователя, чей аккаунт деактивируется.
   */
  private void deleteGoalInvitations(long userId) {
    deleteData(() -> goalRepository.deleteAllGoalInvitationById(userId));
    log.info(MESSAGE_ABOUT_DELETE_GOAL_INVITATIONS);
  }

  /**
   * Метод для удаления отправленных или полученных заявок на менторство/менти пользователя.
   * @param userId  id пользователя, чей аккаунт деактивируется.
   */
  private void deleteMentorshipRequests(long userId) {
    deleteData(() -> mentorshipRequestRepository.deleteAllMentorshipRequestById(userId));
    log.info(MESSAGE_ABOUT_DELETE_MENTORSHIP_REQUESTS);
  }

  /**
   * Метод для удаления пользователя из целей, которые он поставил и являлся ментором.
   * @param user пользователь, чей аккаунт деактивируется.
   */
  private void removeMentorFromHisGoals(User user) {
    Optional.ofNullable(user.getSetGoals())
        .ifPresent(goal -> {
          goal.forEach(currentGoal -> currentGoal.setMentor(null));
          log.info(MESSAGE_ABOUT_REMOVE_MENTOR_FROM_HIS_GOALS);
        });
  }

  /**
   * Метод для удаления пользователя из списка менти, где он был ментором.
   * @param user пользователь, чей аккаунт деактивируется.
   */
  private void removeMentorFromMenteeList(User user) {
    Optional.ofNullable(user.getMentees())
        .ifPresent(mentee -> {
          mentee.forEach(currentMentee -> currentMentee.getMentors().remove(user));
          log.info(MESSAGE_ABOUT_REMOVE_MENTOR_FROM_MENTEE_LIST);
        });
  }

  /**
   * Метод для удаления данных из разных репозиториев.
   * @param deletion функциональный интерфейс, который содержит метод удаления.
   */
  private void deleteData(Runnable deletion) {
    try {
      deletion.run();
    } catch (AopInvocationException e) {
      log.error(DELETION_ERROR_MESSAGE, e);
    }
  }

}
