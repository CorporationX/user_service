package school.faang.user_service.controller.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.testData.TestData;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static school.faang.user_service.exception.message.ExceptionMessage.INVALID_EVENT_DATES_EXCEPTION;
import static school.faang.user_service.exception.message.ExceptionMessage.NULL_EVENT_ID_EXCEPTION;

class EventControllerValidationTest {
    private final EventControllerValidation eventControllerValidation = new EventControllerValidation();
    private EventDto eventDto;

    @BeforeEach
    void setUp() {
        eventDto = new TestData().getEventDto();
    }

    @Nested
    class PositiveTests {
        @DisplayName("Shouldn't throw exception when event's end date it isn't earlier than start date")
        @Test
        void shouldNotThrowExceptionWhenEventDtoDatesAreValid() {
            assertDoesNotThrow(() -> eventControllerValidation.validateEventDates(eventDto));
        }

        @DisplayName("Shouldn't throw exception when event has id")
        @Test
        void shouldNotThrowExceptionWhenEventDtoHasId() {
            assertDoesNotThrow(() -> eventControllerValidation.validateEventId(eventDto));
        }
    }

    @Nested
    class NegativeTests {
        @DisplayName("Should throw exception when dto has null-valued id")
        @Test
        void shouldThrowExceptionWhenOwnerIdIsNull() {
            eventDto.setId(null);

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> eventControllerValidation.validateEventId(eventDto));

            assertEquals(NULL_EVENT_ID_EXCEPTION.getMessage(), exception.getMessage());
        }

        @DisplayName("Should throw exception when event end date is earlier than start date")
        @Test
        void shouldThrowExceptionWhenEndDateIsEarlierThanStartDate() {
            eventDto.setEndDate(LocalDateTime.now().minus(1, ChronoUnit.DAYS));

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> eventControllerValidation.validateEventDates(eventDto));

            assertEquals(INVALID_EVENT_DATES_EXCEPTION.getMessage(), exception.getMessage());
        }
    }
}