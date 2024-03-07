package school.faang.user_service.validation.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class EventValidator {
    private final SkillRepository skillRepository;
    private final EventRepository eventRepository;
    private final SkillMapper skillMapper;

    public void validateEventDtoFields(EventDto eventDto) {
        if (eventDto.getTitle() == null || eventDto.getTitle().isEmpty() || eventDto.getTitle().isBlank()) {
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
        List<Skill> requiredSkills = skillMapper.toEntity(eventDto.getRelatedSkills());

        if (userSkills == null) {
            throw new DataValidationException("User hasn't got any skills");
        }
        if (!(new HashSet<>(userSkills).containsAll(requiredSkills))) {
            throw new DataValidationException("User hasn't got required skills for this event");
        }
    }

    public void validateUserIsOwnerOfEvent(User user, EventDto eventDto) {
        if (user.getId() != eventDto.getOwnerId()) {
            throw new IllegalStateException("User is not an owner of the Event");
        }
    }

    public void validateEventExistsById(long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NoSuchElementException("There is no event with id " + eventId);
        }
    }
}
