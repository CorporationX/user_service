package school.faang.user_service.validator.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventValidator {

    private final SkillRepository skillRepository;

    public void validateOwnerSkills(EventDto eventDto) {
        Long ownerId = eventDto.getOwnerId();
        List<Skill> skillsOwner = skillRepository.findAllByUserId(ownerId);

        if (skillsOwner.isEmpty()) {
            throw new DataValidationException(String.format("User doesn't have skills with id: %d", ownerId));
        }

        Set<Long> setIdSkillsEvent = new HashSet<>(eventDto.getRelatedSkills());

        List<Skill> skills = skillsOwner.stream()
                .filter(skill -> setIdSkillsEvent.contains(skill.getId()))
                .toList();

        if (skills.size() != setIdSkillsEvent.size()) {
            throw new DataValidationException("User can't send such event with such skills");
        }
    }

    public void validateEventDto(EventDto eventDto) {
        if (eventDto.getId() == null) {
            throw new DataValidationException("ID не должен быть null");
        }

        if (eventDto.getStartDate() == null) {
            throw new DataValidationException("StartDate не должен быть null");
        }

        if (eventDto.getOwnerId() == null) {
            throw new DataValidationException("OwnerID не должен быть null");
        }

        if (eventDto.getRelatedSkills() == null) {
            throw new DataValidationException("RelatedSkills не должен быть null");
        }

        if (isBlank(eventDto.getTitle())) {
            throw new DataValidationException("Title не должен быть пустым или null");
        }
    }

    private boolean isBlank(String str) {
        return str == null || str.isBlank();
    }
}
