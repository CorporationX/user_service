package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class EventValidatorTest {
    @InjectMocks
    private EventValidator eventValidator;

    @Test
    void shouldValidateTitleEvent() {
        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () ->
        {
            eventValidator.validate(null, null, null);
        });
        assertEquals("Не заполнено название", dataValidationException.getMessage());
    }

    @Test
    void shouldValidateStartDateEvent() {
        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () ->
        {
            eventValidator.validate("test", null, null);
        });
        assertEquals("Не заполнена дата", dataValidationException.getMessage());
    }

    @Test
    void shouldValidateOwnerIdEvent() {
        LocalDateTime now = LocalDateTime.now();
        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () ->
        {
            eventValidator.validate("test", now, null);
        });
        assertEquals("Не заполнен пользователь", dataValidationException.getMessage());
    }

    @Test
    void shouldValidateSkill() {
        List<String> skillListDto = new ArrayList<>();
        List<Skill> skillListUser = new ArrayList<>();
        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () ->
        {
            eventValidator.validate(skillListDto, skillListUser);
        });
        assertEquals("Нет подходящих скилов", dataValidationException.getMessage());
    }

}