package school.faang.user_service.validator.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exceptions.event.DataValidationException;
import school.faang.user_service.exceptions.event.EntityNotFoundException;
import school.faang.user_service.repository.UserRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventValidatorImpl implements EventValidator {
    private final UserRepository userRepository;

    public void validate(EventDto event) {
        if (event.getTitle() == null) {
            throw new DataValidationException("title can't be null");
        }
        if (event.getTitle().isBlank()) {
            throw new DataValidationException("title can't be blank");
        }
        if (event.getStartDate() == null) {
            throw new DataValidationException("start date can't be null");
        }
        if (event.getOwnerId() == null) {
            throw new DataValidationException("event owner can't be null");
        }
    }

    public void validateOwnersRequiredSkills(EventDto event) {
        User user = userRepository
                .findById(event.getOwnerId())
                .orElseThrow(() -> new EntityNotFoundException("user with id=" + event.getOwnerId() + " not found"));

        Set<Long> requiredSkillsIds = user.getSkills().stream()
                .map(Skill::getId)
                .collect(Collectors.toSet());

        boolean doesOwnerHasRequiredSkills = event.getRelatedSkills().stream()
                .map(SkillDto::getId)
                .allMatch(requiredSkillsIds::contains);

        if (!doesOwnerHasRequiredSkills) {
            throw new DataValidationException("user with id=" + event.getOwnerId() + " has no enough skills to create event");
        }
    }
}
