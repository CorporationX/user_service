package school.faang.user_service.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.goal.GoalInvitationService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.mentorship.MentorshipRequestServiceImpl;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.service.user.UserService;

/**
 * Класс реализации логики по деактивации пользователя.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeactivateUserFacade {

  private final UserService userService;
  private final GoalService goalService;
  private final GoalInvitationService goalInvitationService;
  private final MentorshipService mentorshipService;
  private final MentorshipRequestServiceImpl mentorshipRequestService;
  private final UserMapper userMapper;

  /**
   * Метод для деактивации пользователя.
   * @param userId  id пользователя, чей аккаунт деактивируется.
   * @return
   */
  @Transactional
  public UserDto deactivateUser(long userId) {
    final User user = userService.getUserById(userId);
    beforeDeactivateUser(user);
    user.setActive(Boolean.FALSE);
    final User actualUser = userService.saveUser(user);
    afterDeactivateUser(user);
    return userMapper.toUserDto(actualUser);
  }

  /**
   * Метод для выполнения операций над активностями пользователя перед его деактивацией.
   * @param user пользователь, чей аккаунт деактивируется.
   */
  private void beforeDeactivateUser(User user) {
    userService.cancelUserGoals(user);
    userService.cancelUserEvents(user);
    goalInvitationService.deleteGoalInvitations(user.getId());
    mentorshipRequestService.deleteMentorshipRequests(user.getId());
  }

  /**
   * Метод для выполнения операций менторства пользователя после его деактивацией.
   * @param user пользователь, чей аккаунт деактивируется.
   */
  private void afterDeactivateUser(User user) {
    mentorshipService.removeMentorFromMenteeList(user);
    goalService.removeMentorFromHisGoals(user);
  }

}
