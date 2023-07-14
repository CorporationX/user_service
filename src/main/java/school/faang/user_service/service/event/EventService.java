package school.faang.user_service.service.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventDtoMapper;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final EventDtoMapper eventDtoMapper;
    private final UserRepository userRepository;

    @Autowired
    public EventService(EventRepository eventRepository, EventDtoMapper eventDtoMapper, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.eventDtoMapper = eventDtoMapper;
        this.userRepository = userRepository;
    }

    public EventDto create(EventDto event) {
        validateUserSkills(event);
        Event createdEvent = eventRepository.save(eventDtoMapper.mapToEntity(event));
        return eventDtoMapper.mapToDto(createdEvent);
    }

    void validateUserSkills(EventDto event) {
        List<SkillDto> requiredSkills = event.getRelatedSkills();
        Long ownerId = event.getOwnerId();

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new DataValidationException("Invalid owner ID"));

        List<Skill> ownerSkills = owner.getSkills();

        for (SkillDto requiredSkill : requiredSkills) {
            boolean hasSkill = ownerSkills.stream()
                    .anyMatch(skill -> skill.getTitle().equals(requiredSkill.getTitle()));

            if (!hasSkill) {
                throw new DataValidationException("User does not have the required skills for the event");
            }
        }
    }
}
