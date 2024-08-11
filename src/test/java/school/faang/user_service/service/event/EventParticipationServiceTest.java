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
    private final long wrongUserId = 1L;


    @Test
    public void testRegisterExistingUser() {
        prepareUserList(eventId, userId);
        Assert.assertThrows(RuntimeException.class, () -> eventParticipationService.addParticipant(eventId, userId));
    }

    @Test
    public void testRegister() {
        prepareUserList(eventId, wrongUserId);
        eventParticipationService.addParticipant(eventId, userId);
        Mockito.verify(eventParticipationRepository).register(eventId, userId);
    }

    @Test
    public void testUnregisterNotExistingUser() {
        prepareUserList(eventId, wrongUserId);
        Assert.assertThrows(RuntimeException.class, () -> eventParticipationService.removeParticipant(eventId, userId));
    }

    @Test
    public void testUnregister() {
        prepareUserList(eventId, userId);
        eventParticipationService.removeParticipant(eventId, userId);
        Mockito.verify(eventParticipationRepository).unregister(eventId, userId);
    }

    @Test
    public void testGetParticipant() {
        User user = createUser(userId);
        UserDto userdto = toDto(user);

        Mockito.when(userMapper.toDto(user)).thenReturn(userdto);
        prepareUserList(eventId, userId);

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


    private void prepareUserList(long eventId, long userId) {
        User user = createUser(userId);
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(List.of(user));
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
