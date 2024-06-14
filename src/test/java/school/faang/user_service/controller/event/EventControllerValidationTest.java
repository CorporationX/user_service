package school.faang.user_service.controller.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static school.faang.user_service.exception.message.ExceptionMessage.INVALID_EVENT_END_DATE_EXCEPTION;
import static school.faang.user_service.exception.message.ExceptionMessage.INVALID_EVENT_START_DATE_EXCEPTION;
import static school.faang.user_service.exception.message.ExceptionMessage.NULL_EVENT_OWNER_ID_EXCEPTION;
import static school.faang.user_service.exception.message.ExceptionMessage.NULL_OR_BLANK_EVENT_TITLE_EXCEPTION;

class EventControllerValidationTest {
    private final EventControllerValidation eventControllerValidation = new EventControllerValidation();
    private EventDto eventDto;

    @BeforeEach
    void setUp() {
        eventDto = new EventDto();
        eventDto.setTitle("Title");
        eventDto.setStartDate(LocalDateTime.of(3024, 6, 12, 12, 12));
        eventDto.setOwnerId(1L);
        eventDto.setDescription("Description");

        var skillADto = new SkillDto();
        skillADto.setTitle("SQL");
        var skillBDto = new SkillDto();
        skillBDto.setTitle("Java");
        eventDto.setRelatedSkills(List.of(skillADto, skillBDto));
        eventDto.setLocation("Location");
        eventDto.setMaxAttendees(10);
    }

    @Nested
    class PositiveTests {
        @DisplayName("Shouldn't throw exception when event has title, start date, ownerId and if has end date it isn't earlier than start date")
        @Test
        void shouldNotThrowExceptionWhenEventDtoIsValid() {
            assertDoesNotThrow(() -> eventControllerValidation.validateEvent(eventDto));
        }
    }

    @Nested
    class NegativeTests {
        @DisplayName("Should throw exception when event has empty title")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"", "  ", "\t", "\n"})
        void shouldThrowExceptionWhenTitleIsEmpty(String pattern) {
            eventDto.setTitle(pattern);

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> eventControllerValidation.validateEvent(eventDto));

            assertEquals(NULL_OR_BLANK_EVENT_TITLE_EXCEPTION.getMessage(), exception.getMessage());
        }

        @DisplayName("Should throw exception when event has null start date")
        @Test
        void shouldThrowExceptionWhenStartDateIsNull() {
            eventDto.setStartDate(null);

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> eventControllerValidation.validateEvent(eventDto));

            assertEquals(INVALID_EVENT_START_DATE_EXCEPTION.getMessage(), exception.getMessage());
        }

        @DisplayName("Should throw exception when event has past start date")
        @Test
        void shouldThrowExceptionWhenStartDateIsInPast() {
            eventDto.setStartDate(LocalDateTime.of(2023, 6, 12, 12, 12));

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> eventControllerValidation.validateEvent(eventDto));

            assertEquals(INVALID_EVENT_START_DATE_EXCEPTION.getMessage(), exception.getMessage());
        }

        @DisplayName("Should throw exception when dto has null-valued owner id")
        @Test
        void shouldThrowExceptionWhenOwnerIdIsNull() {
            eventDto.setOwnerId(null);

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> eventControllerValidation.validateEvent(eventDto));

            assertEquals(NULL_EVENT_OWNER_ID_EXCEPTION.getMessage(), exception.getMessage());
        }

        @DisplayName("Should throw exception when events end date is earlier than start date")
        @Test
        void shouldThrowExceptionWhenEndDateIsEarlierThanStartDate() {
            eventDto.setEndDate(LocalDateTime.of(2024, 6, 10, 12, 12));

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> eventControllerValidation.validateEvent(eventDto));

            assertEquals(INVALID_EVENT_END_DATE_EXCEPTION.getMessage(), exception.getMessage());
        }
    }
}