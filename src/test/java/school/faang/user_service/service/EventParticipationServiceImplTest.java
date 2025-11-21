package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.EventParticipationDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventParticipationServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceImplTest {
    @Mock
    private EventParticipationRepository eventParticipationRepository;
    @Mock
    private EventRepository eventRepository;
    @Spy
    private UserMapperImpl userMapper;
    @InjectMocks
    private EventParticipationServiceImpl service;
    private User user;
    private Event event;
    private long eventId;
    private long userId;

    @BeforeEach()
    public void setUp() {
        eventId = 10L;
        userId = 1L;
        user = new User();
        user.setId(userId);
        event = new Event();
        lenient().when(eventRepository.getByIdOrThrow(eventId)).thenReturn(event);
    }

    @Test
    public void checkRegisterParticipantSuccess() {
        event.setAttendees(List.of());
        EventParticipationDto expectedDto = new EventParticipationDto(eventId, userId);
        when(eventParticipationRepository.register(eventId, userId)).thenReturn(expectedDto);

        EventParticipationDto result = service.registerParticipant(eventId, userId);

        assertEquals(expectedDto, result);
        verify(eventParticipationRepository).register(eventId, userId);
    }

    @Test
    public void checkRegisterParticipantFailed() {
        event.setAttendees(List.of(user));

        assertThrows(ForbiddenException.class, () -> service.registerParticipant(eventId, userId));
        verify(eventParticipationRepository, never()).register(anyLong(), anyLong());
    }

    @Test
    public void checkUnregisterParticipantSuccess() {
        event.setAttendees(List.of(user));
        service.unregisterParticipant(eventId, userId);

        verify(eventParticipationRepository).unregister(eventId, userId);
    }

    @Test
    public void checkUnregisterParticipantFailed() {
        event.setAttendees(List.of());
        service.unregisterParticipant(eventId, userId);

        verify(eventParticipationRepository, never()).unregister(anyLong(), anyLong());
    }

    @Test
    public void checkCountParticipantsByEventIdSuccess() {
        when(eventParticipationRepository.countParticipants(eventId)).thenReturn(5);

        CountResponse countResponse = service.countParticipantsByEventId(eventId);

        verify(eventParticipationRepository).countParticipants(eventId);
        assertEquals(5, countResponse.count());
    }

    @Test
    public void checkCountParticipantsByEventIdFailed() {
        event.setAttendees(List.of());
        CountResponse response = service.countParticipantsByEventId(eventId);

        assertEquals(0, response.count());
    }

    @Test
    public void checkGetAllParticipantsByEventIdSuccess() {
        UserDto userDto = new UserDto(
                1L,
                "testUser",
                "test@test.com",
                "1234567890",
                "About me..."
        );
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(List.of(user));
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        List<UserDto> result = service.getAllParticipantsByEventId(eventId);

        verify(eventParticipationRepository).findAllParticipantsByEventId(eventId);
        assertEquals(1, result.size());
        assertSame(userDto, result.get(0));
    }

    @Test
    public void checkGetAllParticipantsByEventIdFailed() {
        event.setAttendees(List.of());
        List<UserDto> allParticipantsByEventId = service.getAllParticipantsByEventId(eventId);

        assertTrue(allParticipantsByEventId.isEmpty());
    }
}