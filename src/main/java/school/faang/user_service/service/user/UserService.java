package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.ExceptionMessages;
import school.faang.user_service.exception.user.UserNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

/**
 * Класс-сервис, который отвечает за бизнес-логику управления пользователем.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

  private static final String MESSAGE_ABOUT_CANCEL_USER_GOALS
      = "Цели пользователя были отменены.";
  private static final String MESSAGE_ABOUT_CANCEL_USER_EVENTS
      = "Назначенные события пользователя были отменены.";

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  /**
   * Метод для получения пользователя.
   * @param userId id пользователя.
   * @return
   */
  public User getUserById(long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> {
          log.error(String.format(ExceptionMessages.USER_IS_NULL, userId));
          return new UserNotFoundException(userId);
        });
  }

  /**
   * Метод для отмены целей при условии, что её не выполняют другие пользователи.
   * @param user пользователь, чей аккаунт деактивируется.
   */
  public void cancelUserGoals(User user) {
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
  public void cancelUserEvents(User user) {
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
   * Метод для сохранения пользователя в БД.
   * @param user
   * @return Пользователь, который был сохранен вБД.
   */
  public User saveUser(User user) {
    return userRepository.save(user);
  }

  public List<Long> getUserSkillsId(long id) {
    return userRepository.findById(id).orElseThrow(()-> {
      log.error("нет пользователя по id");
      return new EntityNotFoundException("нет пользователя по id");
    }).getSkills().stream().map(Skill::getId).toList();
  }

  public UserDto getUser(long id){
    return userMapper.toUserDto(userRepository.findById(id).orElseThrow());
  }

}
