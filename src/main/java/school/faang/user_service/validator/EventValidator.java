package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

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
        String title = eventDto.getTitle();
        if (title == null || title.isBlank()) {
            throw new DataValidationException("Event title can not be null or empty");
        }
    }

    public void checkStartDate(EventDto eventDto) {
        Optional.ofNullable(eventDto.getStartDate())
                .orElseThrow(() -> new DataValidationException("Event start date can not be null"));
    }

    public void checkOwnerId(EventDto eventDto) {
        userRepository.findById(eventDto.getOwnerId())
                .orElseThrow(() -> new DataValidationException("Event owner does not exist"));
    }

    public void checkOwnerSkills(EventDto eventDto) {
        User owner = userRepository
                .findById(eventDto.getOwnerId())
                .orElseThrow(() -> new DataValidationException("Event owner does not exist"));

        List<Long> ownerSkillsIds = owner.getSkills().stream()
                .map(Skill::getId)
                .toList();
        List<Long> eventSkillsIds = eventDto.getRelatedSkills().stream()
                .map(SkillDto::getId)
                .toList();

        if (!new HashSet<>(eventSkillsIds).containsAll(ownerSkillsIds)) {
            throw new DataValidationException("Event owner does not have skills related to this event");
        }
    }

}
