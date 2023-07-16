package school.faang.user_service.service.event;


import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

  private void validateUserAccess(List<Long> skills, Long ownerId) {
    List<Skill> userSkills = skillRepository.findSkillsByGoalId(ownerId);

    Set<Long> eventSkills = new HashSet<>(skills);
    Set<Long> userSkillIds = new HashSet<>(userSkills.stream().map(Skill::getId).toList());

    boolean hasUserPermission = eventSkills.containsAll(userSkillIds);


    if (!hasUserPermission) {
      throw new DataValidationException("User doesn't have access");
    };
  }

  public EventDto create(EventDto event) {
     validateUserAccess(event.getRelatedSkills(), event.getOwnerId());
     List<Skill> skills = skillRepository.findAllById(event.getRelatedSkills());

     Event newEvent = eventMapper.toEntity(event);
     newEvent.setRelatedSkills(skills);

     Event createdEvent = eventRepository.save(newEvent);
     return eventMapper.toDto(createdEvent);
  }

  public void delete(Long id) {
    eventRepository.deleteById(id);
  }

  public List<EventDto> getParticipatedEvents(Long userId) {
    List<Event> events = eventRepository.findParticipatedEventsByUserId(userId);
    return events.stream().map(event -> eventMapper.toDto(event)).toList();
  }

  public List<EventDto> getOwnedEvents(Long ownerId) {
    List<Event> events = eventRepository.findAllByUserId(ownerId);
    return events.stream().map(event -> eventMapper.toDto(event)).toList();
  }
}
