package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.Exceptions;
import school.faang.user_service.validation.Validator;

import java.time.LocalDateTime;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class ValidationTest {

    @InjectMocks
    private Validator validator;

    @Spy
    private Exceptions exception;

    @Test
    public void testCheckFieldIsNull() {
        Long eventNull = null;

        Assert.assertThrows(DataValidationException.class, () -> validator.checkFieldIsNull(eventNull == null));
    }

    @Test
    public void testCheckLongFieldIsZero() {
        Long eventZero = 0L;

        Assert.assertFalse(validator.checkLongFieldIsNullAndZero(eventZero));
    }

    @Test
    public void testCheckStringIsEmpty() {
        String eventIsEmpty = "    ";

        Assert.assertTrue(validator.checkStringIsNullAndEmpty(eventIsEmpty));
    }

    @Test
    public void testCheckLocalDateTimeIsNotEmpty() {
        LocalDateTime eventDate = LocalDateTime.now();

        Assert.assertFalse(validator.checkLocalDateTimeIsNull(eventDate));
    }


    @Test
    public void testCheckEventDto() {
        EventDto eventDto = EventDto.builder().
                title("1").
                startDate(LocalDateTime.now()).
                ownerId(2L).
                build();

        Assertions.assertDoesNotThrow(() -> validator.checkEventDto(eventDto));
    }
}
