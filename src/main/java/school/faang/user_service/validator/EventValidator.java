package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

@Component
@RequiredArgsConstructor
public class EventValidator {

    private final SkillRepository skillRepository;

    public void validateOwnerSkills(EventDto eventDto) {

        boolean hasMissingSkills = eventDto.getRelatedSkills().stream()
                .map(id -> skillRepository.findUserSkill(id, eventDto.getOwnerId()))
                .anyMatch(skill -> !skill.isPresent());

        if (hasMissingSkills) {
            throw new DataValidationException("User can't send such event with such skills");
        }

//        1 реализация.
//        Long ownerId = eventDto.getOwnerId();
//        if (skillRepository.findAllByUserId(ownerId) == null) {
//            throw new DataValidationException(String.format("User doesn't have skills with id: %d", ownerId));
//        }
//
//        List<Skill> skillsOwner = skillRepository.findAllByUserId(ownerId);
//
//        Set<Long> setIdSkillsOwner = skillsOwner.stream()
//                .map(Skill::getId)
//                .collect(Collectors.toSet());
//
//        Set<Long> setIdSkillsEvent = new HashSet<>(eventDto.getRelatedSkills());
//
//        if (!setIdSkillsEvent.equals(setIdSkillsOwner)) {
//            throw new DataValidationException("User can't send such event with such skills");
//        }
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
