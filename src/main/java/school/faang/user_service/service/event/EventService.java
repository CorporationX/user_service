package school.faang.user_service.service.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {
    private final EventRepository eventRepository;

    private final EventMapper eventMapper;

    @Autowired
    public EventService(EventRepository eventRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    public EventDto create(EventDto event) {
        validateUserSkills(event);
        Event savedEvent = eventRepository.save(eventMapper.toEvent(event));
        return eventMapper.toEventDto(savedEvent);
    }
    public List<SkillDto> getUserSkills(Long userId) {
        List<Event> userEvents = eventRepository.findParticipatedEventsByUserId(userId);

        List<Skill> userSkills = userEvents.stream()
                .flatMap(event -> event.getRelatedSkills().stream())
                .toList();

        return userSkills.stream()
                .map(skill -> new SkillDto(skill.getId(), skill.getTitle()))
                .collect(Collectors.toList());
    }


    public void validateUserSkills(EventDto event) {
        List<SkillDto> eventSkills = event.getRelatedSkills();
        Long userId = event.getOwnerId();
        List<SkillDto> userSkills = getUserSkills(userId);

        for (SkillDto eventSkill : eventSkills) {
            if (!userSkills.contains(eventSkill)) {
                throw new DataValidationException("User does not have the required skill: " + eventSkill.getTitle());
            }
        }
    }

}
