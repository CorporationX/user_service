package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EventNotFoundException;
import school.faang.user_service.exception.UserAlreadyRegisteredException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.exception.UserNotRegisteredForEventException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventParticipationServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventParticipationServiceImplTest {

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @Spy
    private UserMapper userMapper = new UserMapperImpl();

    @InjectMocks
    private EventParticipationServiceImpl eventParticipationService;

    private final long EVENT_ID = 1L;
    private final long USER_ID = 1L;
    private final User USER = User.builder().id(USER_ID).username("test_user").build();
    private final UserDto USER_DTO = UserDto.builder().id(USER_ID).userName("test_user").build();

    @Test
    void testRegisterParticipantWhenUserNotFoundThenThrowsException() {
        when(userRepository.existsById(USER_ID)).thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> eventParticipationService.registerParticipant(EVENT_ID, USER_ID));

        verify(userRepository).existsById(USER_ID);
        verifyNoInteractions(eventRepository, eventParticipationRepository);
    }

    @Test
    void testRegisterParticipantWhenEventNotFoundThenThrowsException() {
        when(userRepository.existsById(USER_ID)).thenReturn(true);
        when(eventRepository.existsById(EVENT_ID)).thenReturn(false);

        assertThrows(EventNotFoundException.class,
                () -> eventParticipationService.registerParticipant(EVENT_ID, USER_ID));

        verify(userRepository).existsById(USER_ID);
        verify(eventRepository).existsById(EVENT_ID);
        verifyNoInteractions(eventParticipationRepository);
    }

    @Test
    void testRegisterParticipantWhenUserAlreadyRegisteredThenThrowsException() {
        when(userRepository.existsById(USER_ID)).thenReturn(true);
        when(eventRepository.existsById(EVENT_ID)).thenReturn(true);
        when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID))
                .thenReturn(List.of(USER));

        assertThrows(UserAlreadyRegisteredException.class,
                () -> eventParticipationService.registerParticipant(EVENT_ID, USER_ID));

        verify(userRepository).existsById(USER_ID);
        verify(eventRepository).existsById(EVENT_ID);
        verify(eventParticipationRepository).findAllParticipantsByEventId(EVENT_ID);
        verify(eventParticipationRepository, never()).register(anyLong(), anyLong());
    }

    @Test
    void testRegisterParticipantWhenSuccessfulRegistration() {
        when(userRepository.existsById(USER_ID)).thenReturn(true);
        when(eventRepository.existsById(EVENT_ID)).thenReturn(true);
        when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID))
                .thenReturn(List.of());

        eventParticipationService.registerParticipant(EVENT_ID, USER_ID);

        verify(userRepository).existsById(USER_ID);
        verify(eventRepository).existsById(EVENT_ID);
        verify(eventParticipationRepository).findAllParticipantsByEventId(EVENT_ID);
        verify(eventParticipationRepository).register(EVENT_ID, USER_ID);
    }

    @Test
    void testUnregisterParticipantWhenUserNotRegisteredThenThrowsException() {
        when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID))
                .thenReturn(List.of());

        assertThrows(UserNotRegisteredForEventException.class,
                () -> eventParticipationService.unregisterParticipant(EVENT_ID, USER_ID));

        verify(eventParticipationRepository).findAllParticipantsByEventId(EVENT_ID);
        verify(eventParticipationRepository, never()).unregister(anyLong(), anyLong());
    }

    @Test
    void testUnregisterParticipantWhenSuccessfulUnregistration() {
        when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID))
                .thenReturn(List.of(USER));

        eventParticipationService.unregisterParticipant(EVENT_ID, USER_ID);

        verify(eventParticipationRepository).findAllParticipantsByEventId(EVENT_ID);
        verify(eventParticipationRepository).unregister(EVENT_ID, USER_ID);
    }

    @Test
    void testGetParticipantsReturnsCorrectDtoList() {
        List<User> participants = List.of(USER);
        when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID))
                .thenReturn(participants);

        List<UserDto> result = eventParticipationService.getParticipants(EVENT_ID);

        assertEquals(1, result.size());
        assertEquals(USER_DTO, result.get(0));
        verify(eventParticipationRepository).findAllParticipantsByEventId(EVENT_ID);
        verify(userMapper).toDtoList(participants);
    }

    @Test
    void testGetParticipantsCountReturnsCorrectCount() {
        int expectedCount = 5;
        when(eventParticipationRepository.countParticipants(EVENT_ID))
                .thenReturn(expectedCount);

        int result = eventParticipationService.getParticipantsCount(EVENT_ID);

        assertEquals(expectedCount, result);
        verify(eventParticipationRepository).countParticipants(EVENT_ID);
    }
}