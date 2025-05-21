package school.faang.user_service.controller.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.utils.EventControllerUtils;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EventControllerUtilsTest {
    @InjectMocks
    private EventControllerUtils utils;

    @BeforeEach
    public void setUp() {
        utils = new EventControllerUtils();
    }

    @Test
    public void testValidEventDtoDates_shouldPass() {
        EventDto event = new EventDto();
        event.setStartDate(LocalDateTime.of(2025, 1, 1, 0, 0));
        event.setEndDate(LocalDateTime.of(2025, 1, 5, 0, 0));

        assertDoesNotThrow(() -> utils.isValidDateRange(event));
    }

    @Test
    public void testInvalidEventDtoDates_shouldThrowException() {
        EventDto event = new EventDto();
        event.setStartDate(LocalDateTime.of(2025, 1, 10, 0, 0));
        event.setEndDate(LocalDateTime.of(2025, 1, 5, 0 ,0));

        assertThatThrownBy(() -> utils.isValidDateRange(event))
                .isInstanceOf(DataValidationException.class)
                .hasMessage("The end date must be after the start date");
    }

    @Test
    public void testNullEndDateInEventDto_shouldPass() {
        EventDto event = new EventDto();
        event.setStartDate(LocalDateTime.of(2025, 1, 1, 0, 0));
        event.setEndDate(null);

        assertDoesNotThrow(() -> utils.isValidDateRange(event));
    }

    @Test
    public void testValidEventFilterDtoDates_shouldPass() {
        EventFilterDto filter = new EventFilterDto();
        filter.setStartDate(LocalDateTime.of(2025, 3, 1, 0, 0));
        filter.setEndDate(LocalDateTime.of(2025, 3, 10, 0, 0));

        assertDoesNotThrow(() -> utils.isValidDateRange(filter));
    }

    @Test
    public void testInvalidEventFilterDtoDates_shouldThrowException() {
        EventFilterDto filter = new EventFilterDto();
        filter.setStartDate(LocalDateTime.of(2025, 3, 15, 0, 0));
        filter.setEndDate(LocalDateTime.of(2025, 3, 10, 0, 0));

        assertThatThrownBy(() -> utils.isValidDateRange(filter))
                .isInstanceOf(DataValidationException.class)
                .hasMessage("The end date must be after the start date");
    }

    @Test
    public void testOneNullDateInEventFilterDto_shouldPass() {
        EventFilterDto filter = new EventFilterDto();
        filter.setStartDate(LocalDateTime.of(2025, 3, 1, 0, 0));
        filter.setEndDate(null);

        assertDoesNotThrow(() -> utils.isValidDateRange(filter));
    }

}