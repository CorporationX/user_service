package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceTest {
    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private EventParticipationService eventParticipationService;

    @Test
    public void registerParticipantTest() {
        Mockito.when(eventParticipationRepository.existsById(1L)).thenReturn(true);
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(Collections.emptyList());
        eventParticipationService.registerParticipant(1L, 10L);
        Mockito.verify(eventParticipationRepository).register(1L, 10L);
    }

    @Test
    public void registerParticipantThrowExceptionTest() {
        User user = User.builder().id(1L).username("name").build();
        Mockito.when(eventParticipationRepository.existsById(1L)).thenReturn(true);
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(List.of(user));
        assertThrows(DataValidationException.class,
                () -> eventParticipationService.registerParticipant(1L, 1L));
    }

    @Test
    public void unregisterParticipantTest() {
        User user = User.builder().id(1L).username("name").build();
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(List.of(user));
        eventParticipationService.unregisterParticipant(1L, 1L);
        Mockito.verify(eventParticipationRepository).unregister(1L, 1L);
    }

    @Test
    public void unregisterParticipantUserNotRegisteredThrowsUserNotRegisteredAtEventExceptionTest() {
        assertThrows(DataValidationException.class,
                () -> eventParticipationService.unregisterParticipant(1L, 2L));
    }

    @Test
    public void getParticipantTest() {
        User user = User.builder().id(1L).username("name").build();
        UserDto userDto = new UserDto(1L, "name", "email");
        Mockito.when(eventParticipationRepository.existsById(1L)).thenReturn(true);
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(List.of(user));
        Mockito.when(userMapper.toDto(user)).thenReturn(userDto);
        assertEquals(userDto, eventParticipationService.getListOfParticipant(1L).get(0));
    }

    @Test
    public void getParticipantThrowExceptionTest() {
        assertThrows(DataValidationException.class,
                () -> eventParticipationService.getListOfParticipant(1L));
    }

    @Test
    public void getCountRegisteredParticipantTest() {
        Mockito.when(eventParticipationRepository.existsById(1L)).thenReturn(true);
        Mockito.when(eventParticipationRepository.countParticipants(1L)).thenReturn(1);
        assertEquals(1, eventParticipationService.getCountRegisteredParticipant(1L));
        Mockito.verify(eventParticipationRepository).countParticipants(1L);
    }

    @Test
    public void getParticipantsCountThrowsException() {
        assertThrows(DataValidationException.class,
                () -> eventParticipationService.getCountRegisteredParticipant(1L));
    }
}