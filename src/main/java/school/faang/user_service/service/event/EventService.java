package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final SkillRepository skillRepository;
    private final EventMapper eventMapper;

    public EventDto create(EventDto event) {
        validateEventSkills(event);
        var eventEntity = eventRepository.save(eventMapper.toEntity(event));
        return eventMapper.toDto(eventEntity);
    }

    public EventDto getEvent(long eventId) {
        var event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id: %d was not found", eventId)));
        return eventMapper.toDto(event);
    }

    private void validateEventSkills(EventDto event) {
        long ownerId = event.getOwnerId();
        var relatedSkills = event.getRelatedSkills();
        var userSkills = skillRepository.findAllByUserId(ownerId);

        for (SkillDto skill : relatedSkills) {
            boolean userHasSkill = userSkills.stream()
                    .anyMatch(userSkill -> userSkill.getId() == skill.getId());
            if (!userHasSkill) {
                throw new DataValidationException("User does not have the required skills for the event");
            }
        }
    }
}
