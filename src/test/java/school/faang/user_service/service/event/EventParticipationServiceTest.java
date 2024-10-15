package school.faang.user_service.service.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.publisher.EventStartEventPublisher;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceTest {

    @InjectMocks
    private EventParticipationServiceImpl eventParticipationService;

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private EventStartEventPublisher eventStartEventPublisher;

    @Mock
    private EventRepository eventRepository;

    private static long eventId;
    private static long userId;
    private static User registeredUser = new User();
    static Event event = new Event();

    @BeforeAll
    static void setup() {
        eventId = 1L;
        userId = 1L;
        registeredUser.setId(1L);
        User user = new User();
        user.setId(1L);
        event.setOwner(user);
    }

    @Test
    void registerParticipant_alreadyRegistered_throwsException() {
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(Collections.singletonList(registeredUser));
        assertThrows(DataValidationException.class,
                () -> eventParticipationService.registerParticipant(eventId, userId));
        verify(eventParticipationRepository, times(0)).register(eventId, userId);
    }

    @Test
    void registerParticipant_newParticipant_repositoryCalled() {
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(Collections.emptyList());
        when(eventRepository.findById(userId)).thenReturn(Optional.of(event));
        eventParticipationService.registerParticipant(eventId, userId);
        verify(eventParticipationRepository, times(1)).register(eventId, userId);
    }

    @Test
    void unregisterParticipant_notRegistered_throwsException() {
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(Collections.emptyList());
        assertThrows(DataValidationException.class,
                () -> eventParticipationService.unregisterParticipant(eventId, userId));
        verify(eventParticipationRepository, times(0)).unregister(eventId, userId);
    }

    @Test
    void unregisterParticipant_registeredParticipant_repositoryCalled() {
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(Collections.singletonList(registeredUser));
        eventParticipationService.unregisterParticipant(eventId, userId);
        verify(eventParticipationRepository, times(1)).unregister(eventId, userId);
    }

    @Test
    void getParticipant_existingEvent_returnsListOfUserDto() {
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(Collections.singletonList(registeredUser));
        List<UserDto> participants = eventParticipationService.getParticipant(eventId);
        assertEquals(1, participants.size());
        assertEquals(userId, participants.get(0).getId());
        verify(eventParticipationRepository, times(1)).findAllParticipantsByEventId(eventId);
        verify(userMapper, times(1)).toDto(registeredUser);
    }

    @Test
    void getParticipantCount_existingEvent_returnsNumberOfParticipants() {
        when(eventParticipationRepository.countParticipants(eventId))
                .thenReturn(1);
        assertEquals(1, eventParticipationService.getParticipantCount(eventId));
        verify(eventParticipationRepository, times(1)).countParticipants(eventId);
    }
}