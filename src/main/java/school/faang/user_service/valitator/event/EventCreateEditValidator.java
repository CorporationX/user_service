package school.faang.user_service.valitator.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import school.faang.user_service.dto.event.EventCreateEditDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.valitator.Error;
import school.faang.user_service.valitator.ValidationResult;
import school.faang.user_service.valitator.Validator;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventCreateEditValidator implements Validator<EventCreateEditDto> {

    private final SkillRepository skillRepository;

    @Override
    public ValidationResult validate(EventCreateEditDto object) {
        var validationResult = new ValidationResult();
        if (object.getStartDate() == null) {
            validationResult.add(Error.of("invalid.start-date", getNotNullErrorText("start-date")));
        }
        if (StringUtils.isEmpty(object.getTitle())) {
            validationResult.add(Error.of("invalid.title", getNotNullErrorText("title")));
        }
        for (Long relatedSkillId : object.getRelatedSkillIds()) {
            if (!getUserSkillIds(object.getOwnerId()).contains(relatedSkillId)) {
                validationResult.add(Error.of("invalid.related-skill", String.format("Не возможно установить skill, id: %s", relatedSkillId)));
            }
        }
        return validationResult;
    }

    private Set<Long> getUserSkillIds(Long ownerId) {
        return skillRepository.findAllByUserId(ownerId).stream()
                .map(Skill::getId)
                .collect(Collectors.toSet());
    }

    private String getNotNullErrorText(String fieldName) {
        return fieldName + " не может быть пустым";
    }
}
