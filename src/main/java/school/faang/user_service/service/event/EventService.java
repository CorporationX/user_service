package school.faang.user_service.service.event;


import lombok.RequiredArgsConstructor;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.filters.EventFilter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventService {
  private final EventRepository eventRepository;
  private final SkillRepository skillRepository;
  private final EventMapper eventMapper;
  private final List<EventFilter> eventFilters;

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

  public List<EventDto> getEventsByFilter(EventFilterDto filters) {
    Stream<Event> events = eventRepository.findAll().stream();

    return eventFilters.stream()
        .filter(filter -> filter.isApplicable(filters))
        .flatMap(filter -> filter.apply(events, filters))
        .map(eventMapper::toDto)
        .toList();
  }

  public EventDto get(Long id) throws Exception {
    Event event = eventRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Couldn't find event with id: " + id));

    return eventMapper.toDto(event);
  }
}
