package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EventParticipationServiceTest {

    @InjectMocks
    private EventParticipationService eventParticipationService;

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    public EventParticipationServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterParticipant() {
        long userId = 1L;
        long eventId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("testUser");
        user.setEmail("test@example.com");

        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(Collections.emptyList());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(new UserDto
                (user.getId(), user.getCity(), user.getEmail(),
                        user.getExperience(), user.getUsername(), user.getPremium()));

        UserDto result = eventParticipationService.registerParticipant(eventId, userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("testUser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());

        verify(eventParticipationRepository, times(1)).register(eventId, userId);
    }

    @Test
    public void testUnregisterParticipant() {
        long userId = 1L;
        long eventId = 1L;
        User user = new User();
        user.setId(userId);

        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(Collections.singletonList(user));

        eventParticipationService.unregisterParticipant(eventId, userId);

        verify(eventParticipationRepository, times(1)).unregister(eventId, userId);
    }

    @Test
    public void testUnregisterParticipantNotRegistered() {
        long userId = 1L;
        long eventId = 1L;

        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(Collections.emptyList());

        assertThrows(RuntimeException.class, () -> eventParticipationService.unregisterParticipant(eventId, userId));
    }

    @Test
    void testGetParticipants() {
        long eventId = 1L;
        List<User> participants = Arrays.asList(new User(), new User());
        List<UserDto> participantDto = participants.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());

        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(participants);

        List<UserDto> result = eventParticipationService.getParticipants(eventId);

        assertEquals(participantDto, result);
        verify(eventParticipationRepository).findAllParticipantsByEventId(eventId);
    }

    @Test
    void testGetParticipantsCount() {
        long eventId = 1L;
        int expectedCount = 5;

        when(eventParticipationRepository.countParticipants(eventId)).thenReturn(expectedCount);

        int actualCount = eventParticipationService.getParticipantsCount(eventId);

        assertEquals(expectedCount, actualCount);
        verify(eventParticipationRepository).countParticipants(eventId);
    }
}
