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
class EventParticipationValidatorTest {

    private static final long ID = 1L;
    @InjectMocks
    private EventParticipationValidator eventParticipationValidator;
    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Test
    @DisplayName("Ошибка валидации если id пользователя уже существует")
    void whenCheckIfRegisterParticipantThenThrowException() {
        when(eventParticipationRepository.existsById(ID)).thenReturn(true);
        assertThrows(ValidationException.class,
                () -> eventParticipationValidator.validateUserRegister(ID));
    }

    @Test
    @DisplayName("Ошибка валидации если id пользователя ещё не существует")
    void whenCheckIfUnregisterParticipantThenThrowException() {
        when(!eventParticipationRepository.existsById(ID)).thenReturn(false);
        assertThrows(ValidationException.class,
                () -> eventParticipationValidator.validateUserUnregister(ID));
    }
}