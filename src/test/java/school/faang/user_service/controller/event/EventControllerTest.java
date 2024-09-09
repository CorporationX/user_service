package school.faang.user_service.controller.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validator.event.EventValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {
    @InjectMocks
    private EventController eventController;

    @Mock
    private EventService eventService;

    @Mock
    private EventValidator eventValidator;

    private EventDto eventDto;

    @BeforeEach
    void setUp() {
        eventDto = EventDto.builder().build();
    }

    @Test
    void create_shouldReturnEventDto() {
        // Arrange
        when(eventService.create(eventDto)).thenReturn(eventDto);

        // Act
        EventDto result = eventController.create(eventDto);

        // Assert
        verify(eventService, times(1)).create(eventDto);
        assertEquals(eventDto, result);
    }

    // TODO: create getEvent_shouldReturnEventDto test
}
