package school.faang.user_service.controller.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.event.EventController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.util.ArrayList;

import static java.time.LocalDateTime.*;
import static org.junit.Assert.assertThrows;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {
  EventDto eventDto;
  @Mock
  private EventService eventService;
  @InjectMocks
  private EventController eventController;

  @BeforeEach
  public void init() {
    eventDto = new EventDto(1L, "Hiring", now(), now(),
        1L, "Hiring event", new ArrayList<>(), "USA", 5);
  }

  @Test
  public void testTitleThrowDataValidationException() {
    eventDto.setTitle(null);
    assertThrows(DataValidationException.class, () -> {
      eventController.create(eventDto);
    });
  }

  @Test
  public void testTitleLengthThrowDataValidationException() {
    eventDto.setTitle("Hi");
    assertThrows(DataValidationException.class, () -> {
      eventController.create(eventDto);
    });
  }

  @Test
  public void testStartDateLengthThrowDataValidationException() {
    eventDto.setStartDate(null);
    assertThrows(DataValidationException.class, () -> {
      eventController.create(eventDto);
    });
  }

  @Test
  public void testOwnerIdThrowDataValidationException() {
    eventDto.setOwnerId(null);
    assertThrows(DataValidationException.class, () -> {
      eventController.create(eventDto);
    });
  }

  @Test
  public void testSuccessfulEventCreating() {
    eventController.create(eventDto);
    Mockito.verify(eventService, Mockito.times(1)).create(eventDto);
  }

  @Test
  public void testEventDeleting() {
    Long anyTestId = 1L;
    eventController.deleteEvent(anyTestId);
    Mockito.verify(eventService, Mockito.times(1)).delete(anyTestId);
  }
}