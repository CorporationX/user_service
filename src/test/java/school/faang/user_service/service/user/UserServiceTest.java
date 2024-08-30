package school.faang.user_service.service.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @Mock
  private UserRepository userRepository;
  @InjectMocks
  private UserService userService;
  private long id = 1L;

  @Test
  void testGetUserByIdException() {
    when(userRepository.findById(anyLong()))
        .thenThrow(new RuntimeException("ошибка"));

    RuntimeException exception = assertThrows(RuntimeException.class, () ->
        userService.getUserById(id));

    assertEquals("ошибка", exception.getMessage());
  }

  @Test
  void testGetUserByIdValid() {
    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.ofNullable(User.builder().id(1L).build()));

    userService.getUserById(id);
  }

  @Test
  void testGetUserSkillsIdException() {
    when(userRepository.findById(anyLong()))
        .thenThrow(new RuntimeException("ошибка"));

    RuntimeException exception = assertThrows(RuntimeException.class, () ->
        userService.getUserSkillsId(id));

    assertEquals("ошибка", exception.getMessage());
  }

  @Test
  void testGetUserSkillsIdValid() {
    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.ofNullable(User.builder()
            .id(1L)
            .skills(List.of(Skill.builder().id(1L).build()))
            .build()));

    userService.getUserSkillsId(id);
  }

}
