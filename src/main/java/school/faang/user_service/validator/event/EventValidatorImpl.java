package school.faang.user_service.validator.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.UserRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventValidatorImpl implements EventValidator {
    private final UserRepository userRepository;

    @Override
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
        if (event.getDescription() == null) {
            throw new DataValidationException("description can't be null");
        }
        if (event.getStatus() == null) {
            throw new DataValidationException("status can't be null");
        }
        if (event.getType() == null) {
            throw new DataValidationException("type can't be null");
        }
    }

    @Override
    @Transactional
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

    @Override
    public void validateUserId(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("user with id=" + userId + " not found");
        }
    }
}
