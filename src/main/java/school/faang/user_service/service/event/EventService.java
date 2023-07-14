package school.faang.user_service.service.event;


import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
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

  private void validateUserAccess(List<SkillDto> skills, Long ownerId) throws DataValidationException {
    List<Skill> userSkills = skillRepository.findSkillsByGoalId(ownerId);
    boolean hasUserPermission = skills.stream()
        .allMatch((skill ->  userSkills.stream()
            .anyMatch(userSkill -> userSkill.getTitle().equals(skill.getTitle()))
            )
        );

    if (!hasUserPermission) throw new DataValidationException("User doesn't have access");;
  }
  public EventDto create(EventDto event) throws DataValidationException {
     validateUserAccess(event.getRelatedSkills(), event.getOwnerId());
     Event createdEvent = eventRepository.save(eventMapper.toEntity(event));
     return eventMapper.toDto(createdEvent);
  }
}
