package school.faang.user_service.validator.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;

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

    public boolean checkExistenceSkill(User user, List<Skill> skills) {
        return user.getSkills().containsAll(skills);
    }

    public boolean validateId(Long id) {
        return id >= 0L;
    }

}
