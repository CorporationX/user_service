package school.faang.user_service.valitator.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import school.faang.user_service.dto.event.EventCreateEditDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventCreateEditValidator {

    private final SkillRepository skillRepository;

    public void validate(EventCreateEditDto object) {
        if (object.getStartDate() == null) {
            throw new DataValidationException(getNotNullErrorText("start-date"));
        }
        if (StringUtils.isEmpty(object.getTitle())) {
            throw new DataValidationException(getNotNullErrorText("title"));
        }
        for (Long relatedSkillId : object.getRelatedSkillIds()) {
            if (!getUserSkillIds(object.getOwnerId()).contains(relatedSkillId)) {
                throw new DataValidationException(String.format("Не возможно установить skill, id: %s", relatedSkillId));
            }
        }
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
