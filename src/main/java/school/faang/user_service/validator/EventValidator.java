package school.faang.user_service.validator;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;

import java.util.HashSet;

@Component
public class EventValidator {

    public void checkUserIsOwnerEvent(User user, Event event) {
        if (!event.getOwner().equals(user)) {
            throw new DataValidationException("Пользователь не является автором события " + event.getTitle());
        }
    }

    public void checkNeedSkillsForEvent(User user, Event event) {
        if (!new HashSet<>(user.getSkills()).containsAll(event.getRelatedSkills())) {
            throw new DataValidationException("Пользователь " + user.getUsername()
                    + " не может провести такое событие с такими навыками.");
        }
    }
}
