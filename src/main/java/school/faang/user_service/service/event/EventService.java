package school.faang.user_service.service.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.SkillDto;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Controller
public class EventService {
  EventRepository eventRepository;

  @Autowired
  public EventService(EventRepository eventRepository) {
    this.eventRepository = eventRepository;
  }

  private static void validateUserAccess(List<SkillDto> skills, List<String> userSkills) throws Exception {
    throw new Exception("User doesn't have access");
  }
  public EventDto create(EventDto event) {
    try {
      validateUserAccess(event.getRelatedSkills(), List.of("one"));
      eventRepository.save(event);
    } catch (Exception e) {
      System.out.println(e);
    }
  }
}
