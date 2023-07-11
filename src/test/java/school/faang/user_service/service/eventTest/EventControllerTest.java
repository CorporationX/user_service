package school.faang.user_service.service.eventTest;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.controller.event.EventController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class EventControllerTest {
    @Mock
    private EventService eventService;
    @InjectMocks
    private EventController eventController;
    EventDto eventDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        eventDto = new EventDto(0L, "0", LocalDateTime.now(), LocalDateTime.now(), 0L, "0", new ArrayList<>(), "location", -1);
    }

    @Test
    void testValidDto() {
        eventController.create(eventDto);
        verify(eventService, times(1)).create(eventDto);
    }

    @Test
    void testNullTitleIsInvalid() {
        eventDto.setTitle(null);
        Assert.assertThrows(
                DataValidationException.class,
                () -> eventController.create(eventDto)
        );
    }

    @Test
    void testBlankTitleIsInvalid() {
        eventDto.setTitle("  ");
        Assert.assertThrows(
                DataValidationException.class,
                () -> eventController.create(eventDto)
        );
    }

    @Test
    void testNullStartDateIsInvalid() {
        eventDto.setStartDate(null);
        Assert.assertThrows(
                DataValidationException.class,
                () -> eventController.create(eventDto)
        );
    }

    @Test
    void testNullOwnedIdIsInvalid() {
        eventDto.setOwnerId(null);
        Assert.assertThrows(
                DataValidationException.class,
                () -> eventController.create(eventDto)
        );
    }

    @Test
    void testNegativeOwnedIdIsInvalid() {
        eventDto.setOwnerId(-1L);
        Assert.assertThrows(
                DataValidationException.class,
                () -> eventController.create(eventDto)
        );
    }
}
