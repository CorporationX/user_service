package school.faang.user_service.validator.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import school.faang.user_service.dto.event.WriteEventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WriteEventValidator {

    private final SkillRepository skillRepository;

    public void validate(WriteEventDto object) {
        checkStartDate(object);
        checkTitle(object);
        checkRelatedSkill(object);
    }

    private void checkRelatedSkill(WriteEventDto object) {
        Set<Long> userSkillIds = getUserSkillIds(object.getOwnerId());
        for (Long relatedSkillId : object.getRelatedSkillIds()) {
            if (!userSkillIds.contains(relatedSkillId)) {
                throw new DataValidationException(String.format("Can't install skill, id: %s", relatedSkillId));
            }
        }
    }

    private void checkTitle(WriteEventDto object) {
        if (StringUtils.isEmpty(object.getTitle())) {
            throw new DataValidationException(getNotNullErrorText("title"));
        }
    }

    private void checkStartDate(WriteEventDto object) {
        if (object.getStartDate() == null) {
            throw new DataValidationException(getNotNullErrorText("start-date"));
        }
    }

    private Set<Long> getUserSkillIds(Long ownerId) {
        return skillRepository.findAllByUserId(ownerId).stream()
                .map(Skill::getId)
                .collect(Collectors.toSet());
    }

    private String getNotNullErrorText(String fieldName) {
        return fieldName + " cannot be empty";
    }
}
