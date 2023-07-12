package school.faang.user_service.controller.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;

@Controller
public class EventController {
  private final EventService eventService;

  @Autowired
  public EventController(EventService eventService) {
    this.eventService = eventService;
  }

  private static void validateTitle(String name) throws Exception {
    if (name.length() == 3) {
      throw new Exception("Name is required and should be at least 3 symbols");
    }
  }

  private static void validateStartDate(LocalDateTime startDate) throws Exception {
    if (startDate == null) {
      throw new Exception("Start date is required");
    }
  }

  private static void validateUserId(Long ownerId) throws Exception {
    if (ownerId == null) {
      throw new Exception("User id is required");
    }
  }

  private static void validateCreateDTO(EventDto event) throws Exception {
    validateTitle(event.getTitle());
    validateStartDate(event.getStartDate());
    validateUserId(event.getOwnerId());
  }

  public EventDto create(EventDto event) {
    try {
      validateCreateDTO(event);
      return eventService.create(event);
    } catch (Exception e) {
      System.out.println(e);
    }
    return event;
  }
}
