package school.faang.user_service.validate.eventParticipantValidator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.Validate.EventParticipantValidator;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
public class EventParticipantValidatorTest {

    private static final User USER = User.builder()
            .id(1L)
            .build();

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @InjectMocks
    private EventParticipantValidator eventParticipantValidator;

    @Test
    @DisplayName("Test true check registration at event")
    public void testCheckRegistrationAtEventWithTrue() {
        when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(new ArrayList<>(List.of(USER)));
        assertDoesNotThrow(() -> eventParticipantValidator.checkRegistrationAtEvent(1L, 1L));
    }

    @Test
    @DisplayName("Test false check registration at event")
    public void testCheckRegistrationAtEventWithFalse() {
        when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(new ArrayList<>(List.of(USER)));
        assertThrows(IllegalArgumentException.class, () -> eventParticipantValidator.checkRegistrationAtEvent(1L, 2L));
    }

    @Test
    @DisplayName("Test true check no registrartion at event")
    public void testCheckNoRegistrationAtEventWithTrue() {
        when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(new ArrayList<>(List.of(USER)));
        assertDoesNotThrow(() -> eventParticipantValidator.checkNoRegistrationAtEvent(1L, 2L));
    }

    @Test
    @DisplayName("Test false check no registration at event")
    public void testCheckNoRegistrationAtEventWithFalse() {
        when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(new ArrayList<>(List.of(USER)));
        assertThrows(IllegalArgumentException.class, () -> eventParticipantValidator.checkNoRegistrationAtEvent(1L, 1L));
    }
}
