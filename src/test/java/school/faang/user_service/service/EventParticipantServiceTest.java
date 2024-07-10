package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.Service.event.EventParticipationService;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.eventParticipations.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventParticipantServiceTest {

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private EventParticipationService eventParticipationService;

    @Test
    void registerPatricipant_UserRegistred_ThrowException() {
        long eventId = 1L;
        User firstUser = new User();
        firstUser.setId(1L);
        List<User> userList = List.of(firstUser);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(userList);
        assertThrows(RuntimeException.class , () -> eventParticipationService.registrParticipant(1L,1L));
    }

    @Test
    void registerParticipant_UserRegistredForEvent(){
        long eventId = 1L;
        User firstUser = new User();
        firstUser.setId(1L);
        eventParticipationService.registrParticipant(eventId , firstUser.getId());
        verify(eventParticipationRepository,times(1))
                .register(eventId, firstUser.getId());
    }

    @Test
    void unregisterParticipant_UserNotRegistred_ThrowException(){
        assertThrows(RuntimeException.class , () -> eventParticipationService.unregisterParticipant(1L,1L));
    }

    @Test
    void unregisterParticipant_UserUnRegisterForEvent(){
        long eventId = 1L;
        User firstUser = new User();
        firstUser.setId(1L);
        List<User> userList = List.of(firstUser);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(userList);
        eventParticipationService.unregisterParticipant(eventId, firstUser.getId());
        verify(eventParticipationRepository,Mockito.times(1))
                .unregister(eventId , firstUser.getId());
    }

    @Test
    void getParticipants_ReturnListOfParticipants(){
        long eventId = 1L;
        User firstUser = new User();
        User secondUser = new User();
        firstUser.setId(1L);
        secondUser.setId(2L);
        List<User> userList = List.of(firstUser , secondUser);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(userList);
        List<UserDto> dtoList = userList.stream()
                .map(user -> userMapper.toDto(user))
                .toList();
        assertEquals(dtoList , eventParticipationService.getPaticipant(eventId));
    }

    @Test
    void getParticipantsCount_ReturnParticipantsCount(){
        long eventId = 1L;
        User firstUser = new User();
        User secondUser = new User();
        firstUser.setId(1L);
        secondUser.setId(2L);
        List<User> userList = List.of(firstUser , secondUser);
        List<UserDto> dtoList = userList.stream()
                .map(user -> userMapper.toDto(user))
                .toList();
        when(eventParticipationRepository.countParticipants(eventId)).thenReturn(dtoList.size());
        assertEquals(2,eventParticipationService.getParticipantCount(eventId));
    }
}
