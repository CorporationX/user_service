package school.faang.user_service.controller.event;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class EventParticipationControllerTest {
    @InjectMocks
    private EventParticipationController eventParticipationController;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventParticipationService eventParticipationService;

    @Test
    void test_register_participant_should_throw_exception() {
        Assertions.assertThrows(NullPointerException.class, () -> eventParticipationController.registerParticipant(1L, 1L));
    }

    @Test
    void test_register_participant_should_success_register() {
        User user = new User();
        Event event = new Event();
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        Mockito.doNothing().when(eventParticipationService).registerParticipant(event.getId(), user.getId());
        Assertions.assertDoesNotThrow(() -> eventParticipationController.registerParticipant(event.getId(), user.getId()));
    }
}