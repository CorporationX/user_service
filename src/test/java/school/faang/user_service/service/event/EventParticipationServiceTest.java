package school.faang.user_service.service.event;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.validator.event.EventParticipationValidator;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceTest {
    @Mock
    private EventParticipationRepository eventParticipationRepository;
    @Mock
    private EventParticipationValidator eventParticipationValidator;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private EventParticipationService eventParticipationService;
    private final long eventId = 0L;
    private final long userId = 0L;


    @Test
    public void testRegisterExistingUser() {
        Mockito.doThrow(IllegalArgumentException.class).when(eventParticipationValidator).validateCanUserRegister(eventId,userId);
        Assert.assertThrows(IllegalArgumentException.class, () -> eventParticipationService.addParticipant(eventId, userId));
    }

    @Test
    public void testRegister() {
        eventParticipationService.addParticipant(eventId, userId);
        Mockito.verify(eventParticipationRepository).register(eventId, userId);
    }

    @Test
    public void testUnregisterNotExistingUser() {
        Mockito.doThrow(IllegalArgumentException.class).when(eventParticipationValidator).validateCanUserUnregister(eventId,userId);
        Assert.assertThrows(IllegalArgumentException.class, () -> eventParticipationService.removeParticipant(eventId, userId));
    }

    @Test
    public void testUnregister() {
        eventParticipationService.removeParticipant(eventId, userId);
        Mockito.verify(eventParticipationRepository).unregister(eventId, userId);
    }

    @Test
    public void testGetParticipant() {
        User user = createUser(userId);
        UserDto userdto = toDto(user);

        Mockito.when(userMapper.toDto(user)).thenReturn(userdto);
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(List.of(user));
        List<UserDto> expected = List.of(userdto);
        List<UserDto> result = eventParticipationService.getParticipant(eventId);

        Assertions.assertEquals(result, expected);
        Mockito.verify(eventParticipationRepository).findAllParticipantsByEventId(eventId);
    }

    @Test
    public void testGetParticipantsCount() {
        int result = 11;
        Mockito.when(eventParticipationRepository.countParticipants(eventId)).thenReturn(result);
        Assertions.assertEquals(eventParticipationService.getParticipantsCount(eventId), result);
    }

    private User createUser(long id) {
        return User.builder()
                .id(id)
                .email("email")
                .username("userName")
                .build();
    }

    private UserDto toDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        return userDto;
    }
}
