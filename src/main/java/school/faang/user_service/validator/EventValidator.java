package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EventValidator {
    private final UserRepository userRepository;

    public void validateEvent(EventDto eventDto) {
        checkTitle(eventDto);
        checkStartDate(eventDto);
        checkOwnerId(eventDto);
    }

    public void checkTitle(EventDto eventDto) {
        String eventTitle = eventDto.getTitle();
        if (eventTitle == null || eventTitle.isBlank()) {
            throw new DataValidationException("Event title cannot be empty.");
        }
    }

    public void checkStartDate(EventDto eventDto) {
        LocalDateTime eventStartDate = eventDto.getStartDate();
        if (eventStartDate == null) {
            throw new DataValidationException("Event start date cannot be empty.");
        }
    }

    public void checkOwnerId(EventDto eventDto) {
        Optional<User> owner = userRepository.findById(eventDto.getOwnerId());
        if (owner.isEmpty()) {
            throw new DataValidationException("Event owner does not exist.");
        }
    }

    public void checkOwnerSkills(EventDto eventDto) {
        Optional<User> optionalOwner = userRepository.findById(eventDto.getOwnerId());
        User owner = optionalOwner.orElseThrow(() -> new DataValidationException("Event owner does not exist."));

        List<Long> ownerSkillsIds = owner.getSkills().stream().map(Skill::getId).toList();
        if (!new HashSet<>(ownerSkillsIds)
                .containsAll(eventDto.getRelatedSkillsIds())) {
            throw new DataValidationException("Event owner does not have skills related to this event.");
        }
    }
}
