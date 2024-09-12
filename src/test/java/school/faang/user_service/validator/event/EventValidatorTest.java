package school.faang.user_service.validator.event;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.event.EventParticipationRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventValidatorTest {

    private final long ID = 1L;

    @InjectMocks
    private EventValidator eventValidator;

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Test
    @DisplayName("Ошибка если пользователь уже зарегистрирован")
    void testCheckIfRegisterParticipantThenThrowException() {
        when(eventParticipationRepository.existsById(ID)).thenReturn(true);
        assertThrows(ValidationException.class,
                () -> eventValidator.checkIfUserRegisterOnEvent(ID));
    }

    @Test
    @DisplayName("Ошибка если пользователь ещё не зарегистрирован")
    void testCheckIfUnregisterParticipantThenThrowException() {
        when(!eventParticipationRepository.existsById(1L)).thenReturn(false);
        assertThrows(ValidationException.class,
                () -> eventValidator.checkIfUserUnregisterOnEvent(ID));
    }

    @Test
    @DisplayName("Ошибка если передали null")
    void testEventIdIsNullOrElseThrowValidationException() {
        assertThrows(ValidationException.class,
                () -> eventValidator.eventIdIsNotNullOrElseThrowValidationException(null),
                "Event id can't be null");
    }
}