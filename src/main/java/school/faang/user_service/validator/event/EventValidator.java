package school.faang.user_service.validator.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.adapter.user.UserRepositoryAdapter;
import school.faang.user_service.dto.entity.Skill;
import school.faang.user_service.dto.entity.User;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventValidator {
    private final UserRepositoryAdapter userRepositoryAdapter;

    public void validateEvent(EventDto event) {
        if (event.getTitle() == null || event.getTitle().isBlank()) {
            throw new DataValidationException("Event title не может быть пустым");
        } else if (event.getTitle().length() > 64) {
            throw new DataValidationException("Длина Event title не может быть больше 64");
        }
        if (event.getDescription() == null || event.getDescription().isBlank()) {
            throw new DataValidationException("Event description не может быть пустым");
        } else if (event.getDescription().length() > 4096) {
            throw new DataValidationException("Длина Event description не может быть больше 4096");
        }
        if (event.getStartDate() == null) {
            throw new DataValidationException("StartDate не может быть пустым");
        }
        if (event.getEndDate() == null) {
            throw new DataValidationException("EndDate не может быть пустым");
        }
        if (event.getLocation() == null || event.getLocation().isBlank()) {
            throw new DataValidationException("Event Location не может быть пустым");
        } else if (event.getLocation().length() > 128) {
            throw new DataValidationException("Длина Event Location не может быть больше 128");
        }
        if (event.getOwnerId() == null) {
            throw new DataValidationException("OwnerId не может быть пустым");
        }
        if (event.getEventType() == null) {
            throw new DataValidationException("EventType не может быть пустым");
        }
        if (event.getEventStatus() == null) {
            throw new DataValidationException("EventStatus не может быть пустым");
        }
    }

    public boolean userHasSkills(Long ownerId, List<Long> relatedSkillIds) {
        User user = userRepositoryAdapter.getUserById(ownerId);
        Set<Long> userSkillIdList = user.getSkills().stream()
                .map(Skill::getId)
                .collect(Collectors.toSet());
        return userSkillIdList.containsAll(relatedSkillIds);
    }

    public void userCanCreateEventBySkills(Long ownerId, List<Long> relatedSkillIds) {
        if (!userHasSkills(ownerId, relatedSkillIds)) {
            throw new DataValidationException("Пользователь не может провести такое событие с такими навыками");
        }
    }
}
