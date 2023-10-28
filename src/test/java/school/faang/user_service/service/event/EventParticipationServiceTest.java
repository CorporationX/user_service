package school.faang.user_service.service.event;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.PreferredContact;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class EventParticipationServiceTest {
    @Mock
    private EventParticipationRepository eventParticipationRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private EventParticipationService eventParticipationService;

    @Test
    void registerParticipantAlreadyRegisteredThrowsException() {
        User user = User.builder().id(1L).username("test").build();
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(List.of(user));
        Assertions.assertThrows(DataValidationException.class, () -> {
            eventParticipationService.registerParticipant(1L, 1L);
        });
    }

    @Test
    void registerParticipant() {
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(List.of());
        eventParticipationService.registerParticipant(1L, 1L);
        Mockito.verify(eventParticipationRepository).register(1L, 1L);
    }

    @Test
    void unregisterParticipantThrowsException() {
        User user = User.builder().id(1L).username("test").build();
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(List.of(user));
        Assertions.assertThrows(DataValidationException.class, () -> {
            eventParticipationService.unregisterParticipant(1L, 2L);
        });
    }

    @Test
    void unregisterParticipant() {
        User user = User.builder().id(1L).username("test").build();
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(List.of(user));
        eventParticipationService.unregisterParticipant(1L, 1L);
        Mockito.verify(eventParticipationRepository).unregister(1L, 1L);
    }

    @Test
    void getParticipantsThrowsException() {
        Mockito.when(eventRepository.existsById(1L)).thenReturn(false);
        Assertions.assertThrows(DataValidationException.class, () -> {
            eventParticipationService.getParticipants(1L);
        });
    }

    @Test
    void getParticipants() {
        User user = User.builder().id(1L).username("test").build();
        UserDto userDto = UserDto.builder()
                .id(1L)
                .username("test")
                .email("test")
                .preferredContact(PreferredContact.EMAIL)
                .build();
        Mockito.when(eventRepository.existsById(1L)).thenReturn(true);
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(List.of(user));
        Mockito.when(userMapper.toDto(user)).thenReturn(userDto);
        Assertions.assertEquals(userDto, eventParticipationService.getParticipants(1L).get(0));
    }

    @Test
    void getParticipantsCountThrowsException() {
        Mockito.when(eventRepository.existsById(1L)).thenReturn(false);
        Assertions.assertThrows(DataValidationException.class, () -> {
            eventParticipationService.getParticipantsCount(1L);
        });
    }

    @Test
    void getParticipantsCount() {
        Mockito.when(eventRepository.existsById(1L)).thenReturn(true);
        Mockito.when(eventParticipationRepository.countParticipants(1L)).thenReturn(1);
        Assertions.assertEquals(1, eventParticipationService.getParticipantsCount(1L));
    }
}