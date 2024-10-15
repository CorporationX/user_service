package school.faang.user_service.validator.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.event.EventDto;
import school.faang.user_service.model.dto.skill.SkillDto;
import school.faang.user_service.model.entity.Skill;
import school.faang.user_service.model.entity.User;
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
        validateStartDate(eventDto);
        validateOwnerPresent(eventDto);
    }

    public void validateStartDate(EventDto eventDto) {
        Optional.ofNullable(eventDto.startDate())
                .orElseThrow(() -> new DataValidationException("Event start date can not be null"));
    }

    public void validateOwnerPresent(EventDto eventDto) {
        userRepository.findById(eventDto.ownerId())
                .orElseThrow(() -> new DataValidationException("Event owner does not exist"));
    }

    public void validateOwnerSkills(EventDto eventDto) {
        validateOwnerPresent(eventDto);
        User owner = userRepository.findById(eventDto.ownerId()).get();

        List<Long> ownerSkillsIds = owner.getSkills().stream()
                .map(Skill::getId)
                .toList();
        List<Long> eventSkillsIds = eventDto.relatedSkills().stream()
                .map(SkillDto::id)
                .toList();

        if (!new HashSet<>(ownerSkillsIds).containsAll(eventSkillsIds)) {
            throw new DataValidationException("Event owner does not have skills related to this event");
        }
    }
}
