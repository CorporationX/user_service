package school.faang.user_service.service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.service.EventParticipationService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

        when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(userList);
//        when(userList.isEmpty()).thenReturn(false);
//        when(eventParticipationRepository.register(1L, 1L)).thenThrow(new IllegalArgumentException("Пользователь уже зарегистрирован на событие"));

        assertThrows(IllegalArgumentException.class,
                () -> eventParticipationService.registerParticipant(1L, 1L),
                "testRegisterParticipantUserNotRegister");
    }

    @Test
    public void testRegisterParticipantUserRegister() {
        when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(new ArrayList<>());
        eventParticipationService.registerParticipant(1L, 1L);
        verify(eventParticipationRepository, times(1)).register(1L,1L);
//        assertEquals
//        when(eventParticipationRepository.register(1L, 1L));
//        eventParticipationService.registerParticipant(1L, 1L);
//        verify(eventParticipationRepository, times(1)).register(1L, 1L);

//        assertThrows(
//                () -> eventParticipationService.registerParticipant(1, 1),
//                );
    }

    @Test
    public void testUnRegisterParticipantUserRegister() {

    }

    @Test
    public void testUnRegisterParticipantUserNotRegister() {

    }

    @Test
    public void testGetParticipant() {

    }

    @Test
    public void testGetParticipantCount() {

    }
}
