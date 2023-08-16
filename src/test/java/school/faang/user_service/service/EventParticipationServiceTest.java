package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
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
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(Collections.emptyList());
        eventParticipationService.registerParticipant(1L, 1L);
        Mockito.verify(eventParticipationRepository).register(1L, 1L);
    }

    @Test
    public void registerParticipantThrowExceptionTest() {
        User user = User.builder().id(1L).username("name").build();
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(List.of(user));
        assertThrows(DataValidationException.class,
                () -> eventParticipationService.registerParticipant(1L, 1L));
    }

    @Test
    public void unregisterParticipantTest() {
        Mockito.when(eventParticipationRepository.existsById(1L)).thenReturn(true);
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(Collections.emptyList());
        eventParticipationService.unregisterParticipant(1L, 1L);
        Mockito.verify(eventParticipationRepository).unregister(1L, 1L);
    }

    @Test
    public void unregisterParticipantThrowExceptionTest() {
        User user = User.builder().id(1L).username("name").build();
        Mockito.when(eventParticipationRepository.existsById(1L)).thenReturn(true);
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(List.of(user));
        Assertions.assertThrows(DataValidationException.class,
                () -> eventParticipationService.unregisterParticipant(1L, 2L));
    }

    @Test
    public void getParticipantTest() {
        User user = User.builder().id(1L).username("test").build();
        UserDto userDto = new UserDto(1L, "test", "test");
        Mockito.when(eventParticipationRepository.existsById(1L)).thenReturn(true);
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(List.of(user));
        Mockito.when(userMapper.toDto(user)).thenReturn(userDto);
        Assertions.assertEquals(userDto, eventParticipationService.getListOfParticipant(1L).get(0));
    }

    @Test
    public void getParticipantThrowExceptionTest() {
        assertThrows(DataValidationException.class,
                () -> eventParticipationService.getListOfParticipant(1L));
    }
}