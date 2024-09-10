package school.faang.user_service.EventOrganization.controller.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.EventOrganization.dto.event.EventDto;
import school.faang.user_service.EventOrganization.exception.DataValidationException;
import school.faang.user_service.EventOrganization.service.event.EventService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    @Mock
    EventService eventService;

    @InjectMocks
    private EventController eventController;

    @BeforeEach
    void setUp() {
        //       EventDto eventDto = new EventDto();
    }

    //positive test

    @Test
    void testValidationArgs() {
        //Arrange
        EventDto eventDto = new EventDto();
        eventDto.setId(1L);
        eventDto.setTitle("fsadfnjks");
        eventDto.setStartDate(LocalDateTime.now());
        //Act & Assert
        eventController.create(eventDto);
        verify(eventService, times(1)).create(eventDto);
    }

    //negative test

    @Test
    void testInvalidationArgs() {
        //Arrange
        EventDto eventDto = new EventDto();
        eventDto.setId(null);
        eventDto.setTitle("");
        eventDto.setStartDate(null);
        //Act & Assert
        assertThrows(DataValidationException.class, () -> eventController.create(eventDto));
    }
}