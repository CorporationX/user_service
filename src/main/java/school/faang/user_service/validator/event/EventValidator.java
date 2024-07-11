package school.faang.user_service.validator.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exeption.event.DataValidationException;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class EventValidator {

    public boolean validateEventDto(EventDto eventDto) {
        return validateTitle(eventDto.getTitle())
                && validateDate(eventDto.getStartDate())
                && validateUser(eventDto.getOwnerId());
    }

    private boolean validateDate(LocalDateTime startDate) {
        return startDate != null;
    }

    private boolean validateTitle(String title) {
        return !title.isBlank();
    }

    private boolean validateUser(Long userId) {
        return userId != null;
    }

    public boolean checkExistenceSkill(User owner, List<Skill> skills) {
        boolean result = owner.getSkills().containsAll(skills);
        if (!result) {
            throw new DataValidationException("У пользователя нет необходимых умений.");
        }
        return result;
    }

    public boolean validateId(Long id) {
        return id >= 0L;
    }

}
