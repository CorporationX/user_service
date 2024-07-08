package school.faang.user_service.service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.service.EventParticipationService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventParticipationTest {

    @Mock
    private EventParticipationRepository eventParticipationRepository;
    @Spy
    private UserMapper userMapper;
    @InjectMocks
    private EventParticipationService eventParticipationService;

    @Test
    @DisplayName("testRegisterParticipantUserNotRegister")
    public void testRegisterParticipantUserNotRegister() {
        List<User> userList = new ArrayList<>();
        User firstUser = new User();
        firstUser.setId(1L);
        User secondUser = new User();
        secondUser.setId(2L);
        userList.add(firstUser);
        userList.add(secondUser);

        when(eventParticipationRepository.findParticipantById(1L, 1L)).thenReturn(userList);
        assertThrows(IllegalArgumentException.class,
                () -> eventParticipationService.registerParticipant(1L, 1L),
                "testRegisterParticipantUserNotRegister");
    }

    @Test
    public void testRegisterParticipantUserRegister() {
        when(eventParticipationRepository.findParticipantById(1L, 1L)).thenReturn(new ArrayList<>());
        eventParticipationService.registerParticipant(1L, 1L);
        verify(eventParticipationRepository, times(1)).register(1L, 1L);
    }

    @Test
    public void testUnRegisterParticipantUserRegister() {
        when(eventParticipationRepository.findParticipantById(1L, 1L)).thenReturn(new ArrayList<>());
        assertThrows(IllegalArgumentException.class,
                () -> eventParticipationService.unRegisterParticipant(1L, 1L),
                "testUnRegisterParticipantUserUnRegister");
    }

    @Test
    public void testUnRegisterParticipantUserNotRegister() {
        List<User> userList = new ArrayList<>();
        User firstUser = new User();
        firstUser.setId(1L);
        User secondUser = new User();
        secondUser.setId(2L);
        userList.add(firstUser);
        userList.add(secondUser);

        when(eventParticipationRepository.findParticipantById(1L, 1L)).thenReturn(userList);
        eventParticipationService.unRegisterParticipant(1L, 1L);
        verify(eventParticipationRepository, times(1)).unregister(1L, 1L);
    }

    @Test
    public void testGetParticipant() {
        List<UserDto> userDtoList = new ArrayList<>();
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("JohnDoe");
        userDto.setEmail("johndor@example.com");
        userDtoList.add(userDto);

        when(userMapper.toDtoList(Mockito.anyList())).thenReturn(userDtoList);

        List<UserDto> result = eventParticipationService.getParticipant(1L);
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    public void testGetParticipantCount() {
        //Почему не работает?
        Integer count = eventParticipationService.getParticipantCount(1L);
        assertEquals(1, count);

//        verify(eventParticipationRepository, times(1)).countParticipants(1L);
    }
}