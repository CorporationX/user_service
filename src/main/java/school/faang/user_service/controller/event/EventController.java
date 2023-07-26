package school.faang.user_service.controller.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class EventController {
  private final EventService eventService;
  private static final int MIN_NAME_LENGTH = 3;

  @Autowired
  public EventController(EventService eventService) {
    this.eventService = eventService;
  }

  private void validateTitle(String name) {
    if (name == null || name.length() < MIN_NAME_LENGTH) {
      throw new DataValidationException("Name is required and should be at least 3 symbols");
    }
  }

  private void validateStartDate(LocalDateTime startDate) {
    if (startDate == null) {
      throw new DataValidationException("Start date is required");
    }
  }

  private static void validateUserId(Long ownerId) {
    if (ownerId == null) {
      throw new DataValidationException("User id is required");
    }
  }

  private void validateEvent(EventDto event) {
    validateTitle(event.getTitle());
    validateStartDate(event.getStartDate());
    validateUserId(event.getOwnerId());
  }

  public EventDto create(EventDto event) {
    validateEvent(event);
    return eventService.create(event);
  }

  public void deleteEvent(Long id) {
    eventService.delete(id);
  }

  public List<EventDto> getParticipationEvents(Long userId) {
    return eventService.getParticipatedEvents(userId);
  }

  public List<EventDto> getOwnedEvents(Long ownerId) {
    return eventService.getOwnedEvents(ownerId);
  }

  public List<EventDto> getEventsByFilter(EventFilterDto filter) {
    return eventService.getEventsByFilter(filter);
  }

  public EventDto getEvent(Long id) {
    return eventService.get(id);
  }

  public EventDto updateEvent(EventDto event) {
    validateEvent(event);
    return eventService.updateEvent(event);
  }
}
