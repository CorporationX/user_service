package school.faang.user_service.validator.event;

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

    public void validateStartDate(EventDto eventDto) {
        Optional.ofNullable(eventDto.startDate())
                .orElseThrow(() -> new DataValidationException("Event start date can not be null"));
    }

    public void validateOwnerSkills(EventDto eventDto) {
        User owner = userRepository
                .findById(eventDto.ownerId())
                .orElseThrow(() -> new DataValidationException("Event owner does not exist"));

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
