package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventDtoValidator {
    private final UserService userService;
    private final SkillMapper skillMapper;

    public void validate(EventDto eventDto) {
        validateTitle(eventDto.getTitle());
        validateStartDate(eventDto.getStartDate());
        validateOwnerOfEvent(eventDto);
    }

    public void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new DataValidationException("Please set title of the event");
        }
    }

    public void validateStartDate(LocalDateTime startDate) {
        if (startDate == null) {
            throw new DataValidationException("Please set start date of the event");
        }
        LocalDateTime today = LocalDateTime.now();
        if (startDate.isBefore(today)) {
            throw new DataValidationException("Please set date no earlier than today");
        }
    }

    public void validateOwnerOfEvent(EventDto eventDto) {
        User user = userService.findById(eventDto.getOwnerId());
        HashSet<Skill> userSkills = new HashSet<>(user.getSkills());
        List<Skill> eventSkills = skillMapper.toListEntity(eventDto.getRelatedSkills());

        if (!userSkills.containsAll(eventSkills)) {
            throw new DataValidationException("User must have all the skills specified in the event");
        }
    }
}
