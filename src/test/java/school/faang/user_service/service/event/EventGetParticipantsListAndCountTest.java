package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.EventParticipantsDto;
import school.faang.user_service.dto.EventUserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EventExistsException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventParticipationServiceTest")
public class EventGetParticipantsListAndCountTest {
    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Mock
    private EventValidator eventValidator;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private EventParticipationServiceImpl eventParticipationService;

    private long eventId;
    private List<User> participants;
    private List<EventUserDto> expectedParticipantsDto;

    @BeforeEach
    void setUp() {
        eventId = 1L;
        User user1 = User.builder()
                .id(1L)
                .username("Tom")
                .email("tom@gmail.com")
                .build();
        User user2 = User.builder()
                .id(1L)
                .username("Bob")
                .email("bob@gmail.com")
                .build();
        participants = List.of(user1, user2);
        expectedParticipantsDto = userMapper.usersToUsersDto(participants);
    }

    @Test
    @DisplayName("testGetParticipants_Success")
    public void testGetParticipants_Success() {
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(participants);
        List<EventUserDto> actualDtos = eventParticipationService.getParticipants(eventId);
        verify(eventValidator, times(1)).validateEvent(eventId);
        verify(eventParticipationRepository, times(1)).findAllParticipantsByEventId(eventId);
        assertEquals(expectedParticipantsDto, actualDtos);
    }

    @Test
    @DisplayName("testGetParticipants_EmptyParticipantsDto")
    public void testGetParticipants_EmptyParticipantsDto() {
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(List.of());
        List<EventUserDto> actualDtos = eventParticipationService.getParticipants(eventId);
        verify(eventValidator, times(1)).validateEvent(eventId);
        verify(eventParticipationRepository, times(1)).findAllParticipantsByEventId(eventId);
        assertEquals(List.of(), actualDtos);
    }

    @Test
    @DisplayName("testGetParticipants_EventNotExists")
    public void testGetParticipants_EventNotExists() {
        when(eventValidator.validateEvent(eventId)).thenThrow(new EventExistsException("Event not exists"));
        EventExistsException exception = assertThrows(EventExistsException.class, () -> {
            eventParticipationService.getParticipants(eventId);
        });
        verify(eventValidator, times(1)).validateEvent(eventId);
        assertEquals("Event not exists", exception.getMessage());
    }

    @Test
    @DisplayName("testGetParticipantsCount_Success")
    public void testGetParticipantsCount_Success() {
        when(eventParticipationRepository.countParticipants(eventId)).thenReturn(participants.size());
        EventParticipantsDto actualCountDto = eventParticipationService.getParticipantsCount(eventId);
        verify(eventValidator, times(1)).validateEvent(eventId);
        verify(eventParticipationRepository, times(1)).countParticipants(eventId);
        assertEquals(new EventParticipantsDto(participants.size()), actualCountDto);
    }

    @Test
    @DisplayName("testGetParticipantsCount_EventNotExists")
    public void testGetParticipantsCount_EventNotExists() {
        when(eventValidator.validateEvent(eventId)).thenThrow(new EventExistsException("Event not exists"));
        EventExistsException exception = assertThrows(EventExistsException.class, () -> {
            eventParticipationService.getParticipantsCount(eventId);
        });
        verify(eventValidator, times(1)).validateEvent(eventId);
        assertEquals("Event not exists", exception.getMessage());
    }
}
