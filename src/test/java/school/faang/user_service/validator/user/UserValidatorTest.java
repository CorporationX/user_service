package school.faang.user_service.validator.user;

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
class UserValidatorTest {

    @InjectMocks
    private UserValidator userValidator;

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    private static final long ID = 1L;

    @Test
    @DisplayName("Ошибка валидации если id пользователя уже существует")
    void testCheckIfRegisterParticipantThenThrowException() {
        when(eventParticipationRepository.existsById(ID)).thenReturn(true);
        assertThrows(ValidationException.class,
                () -> userValidator.validateUserRegister(ID));
    }

    @Test
    @DisplayName("Ошибка валидации если id пользователя ещё не существует")
    void testCheckIfUnregisterParticipantThenThrowException() {
        when(!eventParticipationRepository.existsById(ID)).thenReturn(false);
        assertThrows(ValidationException.class,
                () -> userValidator.validateUserUnregister(ID));
    }

    @Test
    @DisplayName("Ошибка валидации если вместо id передели null")
    void testUserIdIsNullOrElseThrowValidationException() {
        assertThrows(ValidationException.class,
                () -> userValidator.validateUserId(null),
                "User id can't be null");
    }
}