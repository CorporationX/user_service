package school.faang.user_service.service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.service.EventParticipationService;


@ExtendWith(MockitoExtension.class)
public class EventParticipationTest {

  @Mock
  private EventParticipationRepository eventParticipationRepository;
  @Spy
  private UserMapper userMapper;
  @InjectMocks
  private EventParticipationService eventParticipationService;

  @Test
  @DisplayName("testRegisterParticipantUserNotRegister")
  public void testRegisterParticipantUserNotRegister() {
    User firstUser = new User();
    firstUser.setId(1L);
    User secondUser = new User();
    secondUser.setId(2L);
    List<User> userList = List.of(firstUser, secondUser);

    when(eventParticipationRepository.findParticipantById(1L, 1L)).thenReturn(userList);
    assertThrows(IllegalArgumentException.class,
        () -> eventParticipationService.registerParticipant(1L, 1L),
        "testRegisterParticipantUserNotRegister");
  }

  @Test
  public void testRegisterParticipantUserRegister() {
    when(eventParticipationRepository.findParticipantById(1L, 1L)).thenReturn(new ArrayList<>());
    eventParticipationService.registerParticipant(1L, 1L);
    verify(eventParticipationRepository, times(1)).register(1L, 1L);
  }

  @Test
  public void testUnRegisterParticipantUserRegister() {
    when(eventParticipationRepository.findParticipantById(1L, 1L)).thenReturn(new ArrayList<>());
    assertThrows(IllegalArgumentException.class,
        () -> eventParticipationService.unRegisterParticipant(1L, 1L),
        "testUnRegisterParticipantUserUnRegister");
  }

  @Test
  public void testUnRegisterParticipantUserNotRegister() {
    User firstUser = new User();
    firstUser.setId(1L);
    User secondUser = new User();
    secondUser.setId(2L);
    List<User> userList = List.of(firstUser, secondUser);

    when(eventParticipationRepository.findParticipantById(1L, 1L)).thenReturn(userList);
    eventParticipationService.unRegisterParticipant(1L, 1L);
    verify(eventParticipationRepository, times(1)).unregister(1L, 1L);
  }

  @Test
  public void testGetParticipant() {
    UserDto userDto = UserDto.builder()
        .id(1L)
        .username("JohnDoe")
        .email("johndor@example.com")
        .phone("1234567890")
        .aboutMe("About John Doe")
        .city("New York")
        .active(true)
        .build();

    List<UserDto> userDtoList = List.of(userDto);

    when(userMapper.toDtoList(Mockito.anyList())).thenReturn(userDtoList);

    List<UserDto> result = eventParticipationService.getParticipant(1L);
    assertEquals(1L, result.get(0).getId());
  }

  @Test
  public void testGetParticipantCount() {
    when(eventParticipationService.getParticipantCount(1L)).thenReturn(1);
    Integer count = eventParticipationService.getParticipantCount(1L);
    assertEquals(1, count);
  }
}