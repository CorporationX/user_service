package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserEventCountResDto;
import school.faang.user_service.dto.UserResDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.event.EventParticipationService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceTest {
    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @InjectMocks
    private EventParticipationService eventParticipationService;

    @Test
    public void testRegisterParticipant_NullEventId_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> eventParticipationService.registerParticipant(null, 1L));
    }

    @Test
    public void testRegisterParticipant_NullUserId_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> eventParticipationService.registerParticipant(1L, null));
    }

    @Test
    public void testRegisterParticipant_UserAlreadyRegistered_ThrowsException() {
        when(eventParticipationRepository.findAllParticipantsByEventId(1L))
                .thenReturn(List.of(User.builder().id(1L).username("testUser").email("test@example.com").build()));

        assertThrows(IllegalArgumentException.class, () -> eventParticipationService.registerParticipant(1L, 1L));

        verify(eventParticipationRepository, never()).register(anyLong(), anyLong());
    }

    @Test
    public void testRegisterParticipant_SuccessfulRegistration() {
        when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(List.of());

        eventParticipationService.registerParticipant(1L, 1L);

        verify(eventParticipationRepository).register(1L, 1L);
    }

    @Test
    public void testUnregisterParticipant_NullEventId_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> eventParticipationService.unregisterParticipant(null, 1L));
    }

    @Test
    public void testUnregisterParticipant_NullUserId_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> eventParticipationService.unregisterParticipant(1L, null));
    }

    @Test
    public void testUnregisterParticipant_UserNotRegistered_ThrowsException() {
        when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class, () -> eventParticipationService.unregisterParticipant(1L, 1L));

        verify(eventParticipationRepository, never()).unregister(anyLong(), anyLong());
    }

    @Test
    public void testUnregisterParticipant_SuccessfulUnregistration() {
        when(eventParticipationRepository.findAllParticipantsByEventId(1L))
                .thenReturn(List.of(User.builder().id(1L).username("testUser").email("test@example.com").build()));

        eventParticipationService.unregisterParticipant(1L, 1L);

        verify(eventParticipationRepository).unregister(1L, 1L);
    }

    @Test
    public void testGetParticipant_NullEventId_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> eventParticipationService.getParticipant(null));
    }

    @Test
    public void testGetParticipant_Success() {
        when(eventParticipationRepository.findAllParticipantsByEventId(1L))
                .thenReturn(List.of(User.builder().id(1L).username("testUser").email("test@example.com").build()));

        List<UserResDto> participants = eventParticipationService.getParticipant(1L);

        assertEquals(1, participants.size());
        assertEquals("testUser", participants.get(0).getUsername());
        verify(eventParticipationRepository).findAllParticipantsByEventId(1L);
    }

    @Test
    public void testGetParticipantCount_NullEventId_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> eventParticipationService.getParticipantCount(null));
    }

    @Test
    public void testGetParticipantCount_Success() {
        when(eventParticipationRepository.countParticipants(1L)).thenReturn(5);

        UserEventCountResDto result = eventParticipationService.getParticipantCount(1L);

        assertEquals(5, result.getCount());
        verify(eventParticipationRepository).countParticipants(1L);
    }
}