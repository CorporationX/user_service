package school.faang.user_service.service.event;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceTest {

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private EventParticipationService eventParticipationService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(1L)
                .username("testUser")
                .email("test@example.com")
                .city("Test City")
                .experience(1)
                .premium(null)
                .build();

        userDto = UserDto.builder()
                .id(user.getId())
                .city(user.getCity())
                .email(user.getEmail())
                .experience(user.getExperience())
                .username(user.getUsername())
                .premium(user.getPremium())
                .build();
    }

    @Test
    public void testRegisterParticipant_Success() {
        long userId = 1L;
        long eventId = 1L;

        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(Collections.emptyList());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = eventParticipationService.registerParticipant(eventId, userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("testUser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());

        verify(eventParticipationRepository, times(1)).register(eventId, userId);
    }

    @Test
    public void testRegisterParticipant_UserAlreadyRegistered() {
        long userId = 1L;
        long eventId = 1L;

        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(Collections.singletonList(user));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                eventParticipationService.registerParticipant(eventId, userId));

        assertEquals("User is already registered for the event", exception.getMessage());
    }

    @Test
    public void testRegisterParticipant_UserNotFound() {
        long userId = 1L;
        long eventId = 1L;

        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(Collections.emptyList());
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                eventParticipationService.registerParticipant(eventId, userId));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void testUnregisterParticipant_Success() {
        long userId = 1L;
        long eventId = 1L;

        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(Collections.singletonList(user));

        eventParticipationService.unregisterParticipant(eventId, userId);

        verify(eventParticipationRepository, times(1)).unregister(eventId, userId);
    }

    @Test
    public void testUnregisterParticipant_UserNotRegistered() {
        long userId = 1L;
        long eventId = 1L;

        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> eventParticipationService.unregisterParticipant(eventId, userId));

        assertEquals("User is not registered for the event", exception.getMessage());
    }

    @Test
    public void testGetParticipants() {
        long eventId = 1L;

        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(Collections.singletonList(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        List<UserDto> participants = eventParticipationService.getParticipants(eventId);

        assertNotNull(participants);
        assertEquals(1, participants.size());
        assertEquals(userDto, participants.get(0));
    }

    @Test
    public void testGetParticipantsCount() {
        long eventId = 1L;

        when(eventParticipationRepository.countParticipants(eventId)).thenReturn(5);

        int count = eventParticipationService.getParticipantsCount(eventId);

        assertEquals(5, count);
    }
}

