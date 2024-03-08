package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
    @Autowired
    private final EventRepository eventRepository;
    private final SkillRepository skillRepository;

    @Autowired
    private EventMapper mapper;

    public EventDto create(EventDto event) {
        validateUserSkills(event);
        Event eventEntity = mapper.toEntity(event);
        Event saveEvent = eventRepository.save(eventEntity);
        return mapper.toDto(saveEvent);
    }

    public void validateUserSkills(EventDto event) {
        Long ownerID = event.getOwnerId();
        List<SkillDto> evenTSkills = event.getRelatedSkills();

        List<Skill> userSkills = skillRepository.findAllByUserId(ownerID);

        List<Long> eventSkills = evenTSkills.stream()
                .map(SkillDto::getId)
                .collect(Collectors.toList());

        if (!userSkills.containsAll(eventSkills)) {
            throw new DataValidationException(" User does not have the required skill");
        }
    }

    public EventDto getEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException(" Event not found " + eventId));
        return mapper.toDto(event);

    }

    public EventDto deleteEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException(" Event not found for deletion " + eventId));
        eventRepository.delete(event);
        return mapper.toDto(event);
    }
}
