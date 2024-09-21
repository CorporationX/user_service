package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class EventParticipationServiceTest {

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @InjectMocks
    private EventParticipationService eventParticipationService;

    private User registeredUser;
    private User unRegisteredUser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        registeredUser = User.builder().id(1L).username("registeredUser").email("registered@example.com").build();
        unRegisteredUser = User.builder().id(2L).username("unregisteredUser").email("unregistered@example.com").build();
    }

    @Test
    public void testManageParticipation_UserAlreadyRegistered_ShouldThrowException() {
        when(eventParticipationRepository.findAllParticipantsByEventId(1L))
                .thenReturn(Arrays.asList(registeredUser));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventParticipationService.manageParticipation(1L, 1L, true);
        });

        assertEquals("User already registered", exception.getMessage());
    }

    @Test
    public void testManageParticipation_UserNotRegistered_ShouldThrowException() {
        when(eventParticipationRepository.findAllParticipantsByEventId(1L))
                .thenReturn(Collections.emptyList());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventParticipationService.manageParticipation(1L, 1L, false);
        });

        assertEquals("User is not registered", exception.getMessage());
    }

//    @Disabled("не проходит, чинить")
//    @Test
//    public void testManageParticipation_UserSuccessfullyRegistered() {
//        when(eventParticipationRepository.findAllParticipantsByEventId(1L))
//                .thenReturn(Collections.emptyList());
//
//        eventParticipationService.manageParticipation(1L, 1L, true);
//
//        verify(eventParticipationRepository, times(1)).register(1L, 1L);
//    }

//    @Disabled("не проходит, чинить")
//    @Test
//    public void testManageParticipation_UserSuccessfullyUnregistered() {
//        when(eventParticipationRepository.findAllParticipantsByEventId(1L))
//                .thenReturn(Arrays.asList(registeredUser));
//
//        eventParticipationService.manageParticipation(1L, 1L, false);
//
//        verify(eventParticipationRepository, times(1)).unregister(1L, 1L);
//    }
}