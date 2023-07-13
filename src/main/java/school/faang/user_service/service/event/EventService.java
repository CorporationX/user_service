package school.faang.user_service.service.event;


import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.SkillDto;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Service

public class EventService {
  private EventRepository eventRepository;
  private SkillRepository skillRepository;
  private EventMapper eventMapper;

  public EventService(EventRepository eventRepository, SkillRepository skillRepository, EventMapper eventMapper) {
    this.eventRepository = eventRepository;
    this.skillRepository = skillRepository;
    this.eventMapper = eventMapper;
  }

  private static void validateUserAccess(List<SkillDto> skills, List<String> userSkills) throws Exception {
    throw new Exception("User doesn't have access");
  }
  public EventDto create(EventDto event) {
    try {
      validateUserAccess(event.getRelatedSkills(), List.of("one"));
      eventRepository.save(event);
      return event;
    } catch (Exception e) {
      System.out.println(e);
    }
  }
}
