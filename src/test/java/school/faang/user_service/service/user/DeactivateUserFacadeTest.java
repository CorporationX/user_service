package school.faang.user_service.service.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.user.UserNotFoundException;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.service.goal.GoalInvitationService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.mentorship.MentorshipRequestServiceImpl;
import school.faang.user_service.service.mentorship.MentorshipService;

@ExtendWith(MockitoExtension.class)
class DeactivateUserFacadeTest {

  private long userId;

  @Mock
  private UserService userService;

  @Mock
  private GoalService goalService;

  @Mock
  private GoalInvitationService goalInvitationService;

  @Mock
  private MentorshipService mentorshipService;

  @Mock
  private MentorshipRequestServiceImpl mentorshipRequestService;

  @Spy
  private UserMapperImpl userMapper;

  @InjectMocks
  private DeactivateUserFacade deactivateUserFacade;

  @BeforeEach
  void setUp() {
    userId = createUser().getId();
  }

  private User createUser() {
    return User.builder()
        .id(2L)
        .username("JaneSmith")
        .email("janesmith@example.com")
        .phone("0987654321")
        .aboutMe("About Jane Smith")
        .city("London")
        .active(false)
        .premium(null)
        .build();
  }

  private UserDto createUserDto() {
    return UserDto.builder()
        .id(2L)
        .username("JaneSmith")
        .email("janesmith@example.com")
        .phone("0987654321")
        .aboutMe("About Jane Smith")
        .city("London")
        .active(false)
        .premium("")
        .build();
  }

  @Test
  @DisplayName("Проверка выброса исключения при деактивации пользователя, который не был найден.")
  public void testDeactivateUserWithNonExistingUserId() {
    when(userService.getUserById(userId)).thenThrow(new UserNotFoundException(userId));
    assertThrows(UserNotFoundException.class, () -> deactivateUserFacade.deactivateUser(createUserDto().getId()));
  }

  @Test
  @DisplayName("Проверка деактивации пользователя по его id.")
  void testDeactivateUserForFacade() {
    when(userService.saveUser(any(User.class))).thenReturn(createUser());
    when(userService.getUserById(userId)).thenReturn(createUser());
    final var userDto = deactivateUserFacade.deactivateUser(userId);
    assertThat(userDto.isActive()).isEqualTo(Boolean.FALSE);
  }

}