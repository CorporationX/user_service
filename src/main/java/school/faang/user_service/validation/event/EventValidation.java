package school.faang.user_service.validation.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.event.EventCreationNotAllowedException;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static school.faang.user_service.util.LogsConstants.NOT_ENOUGH_SKILLS;

@Component
public class EventValidation {
    public void validateUserHasAllEventSkills(List<Long> eventSkillsIds, User owner) {
        List<Skill> ownersSkills = owner.getSkills();
        Set<Long> ownersSkillsIds = new HashSet<>(
                ownersSkills.stream()
                        .map(Skill::getId)
                        .toList()
        );
        if (eventSkillsIds != null && !eventSkillsIds.isEmpty()) {
            Set<Long> requiredSkillsIds = new HashSet<>(eventSkillsIds);
            requiredSkillsIds.removeAll(ownersSkillsIds);

            if (!requiredSkillsIds.isEmpty()) {
                throw new EventCreationNotAllowedException(
                        String.format(NOT_ENOUGH_SKILLS, requiredSkillsIds)
                );
            }
        }
    }

    public void isUserEventOwner(Long userId, Long ownerId) {
        if (!Objects.equals(userId, ownerId)) {
            throw new IllegalArgumentException("У вас нет прав на редактирование этого ивента");
        }
    }
}
