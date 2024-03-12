package school.faang.user_service.validation.event;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventValidator {
    private final SkillRepository skillRepository;
    private final EventRepository eventRepository;

    public void validateEventDtoFields(EventDto eventDto) {
        if (eventDto.getTitle() == null || eventDto.getTitle().isBlank()) {
            throw new DataValidationException("Event must have a title");
        }
        if (eventDto.getStartDate() == null) {
            throw new DataValidationException("Event must have a start date");
        }
        if (eventDto.getOwnerId() == null) {
            throw new DataValidationException("Event must have an owner");
        }
    }

    public void validateUserHasRequiredSkills(EventDto eventDto) {
        List<Skill> userSkills = skillRepository.findAllByUserId(eventDto.getOwnerId());
        List<Skill> requiredSkills = new ArrayList<>();
        eventDto.getRelatedSkillsIds().forEach(id -> {
            Skill skillToAdd = skillRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Skill doesn't exist by id: " + id));
            requiredSkills.add(skillToAdd);
        });

        if (userSkills == null || userSkills.isEmpty()) {
            throw new DataValidationException("User hasn't got any skills");
        }
        if (!(new HashSet<>(userSkills).containsAll(requiredSkills))) {
            throw new DataValidationException("User hasn't got skills required for this event");
        }
    }

    public void validateUserIsOwnerOfEvent(User user, EventDto eventDto) {
        if (user.getId() != eventDto.getOwnerId()) {
            throw new DataValidationException("User is not an owner of the Event");
        }
    }

    public void validateEventExistsById(long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new DataValidationException("There is no event with id " + eventId);
        }
    }
}
