package school.faang.user_service.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.user.UserNotFoundException;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class DeactivateUserFacadeTest {

  private long userId;

  @Mock
  private UserRepository userRepository;
  @Mock
  private GoalRepository goalRepository;
  @Mock
  private MentorshipRequestRepository mentorshipRequestRepository;
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
  @DisplayName("Проверка деактивации пользователя по его id.")
  void testDeactivateUserForFacade() {
    when(userRepository.save(any(User.class))).thenReturn(createUser());
    when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(createUser()));
    final var userDto = deactivateUserFacade.deactivateUser(userId);
    assertThat(userDto.isActive()).isEqualTo(Boolean.FALSE);
  }

  @Test
  @DisplayName("Проверка выброса исключения при деактивации пользователя, который не был найден.")
  public void testDeactivateUserWithNonExistingUserId() {
    when(userRepository.findById(createUserDto().getId())).thenReturn(Optional.empty());
    assertThrows(UserNotFoundException.class, () -> deactivateUserFacade.deactivateUser(createUserDto().getId()));
  }


  @Test
  @DisplayName("Проверка выброса исключения при удалении отправленных или полученных целей пользователя.")
  public void testDeactivateUserWithDeleteGoalInvitations() {
    when(goalRepository.deleteAllGoalInvitationById(userId)).thenThrow();
    assertThrows(Exception.class, () -> deactivateUserFacade.deactivateUser(userId));
  }

  @Test
  @DisplayName("Проверка выброса исключения при удалении отправленных или полученных заявок на менторство/менти пользователя.")
  public void testDeactivateUserWithDeleteMentorshipRequests() {
    when(mentorshipRequestRepository.deleteAllMentorshipRequestById(userId)).thenThrow();
    assertThrows(Exception.class, () -> deactivateUserFacade.deactivateUser(userId));
  }

}