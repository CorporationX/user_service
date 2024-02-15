package school.faang.user_service.exception.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventUpdateDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventValidator {

    public void validate(EventDto eventDto) {
        if (eventDto.getTitle() == null) {
            throw new DataValidationException("Не заполнено название");
        }
        if (eventDto.getStartDate() == null) {
            throw new DataValidationException("Не заполнена дата");
        }
        if (eventDto.getOwnerId() == null) {
            throw new DataValidationException("Не заполнен пользователь");
        }
    }

    public void validate(EventUpdateDto eventUpdateDto) {
        if (eventUpdateDto.getTitle() == null) {
            throw new DataValidationException("Не заполнено название");
        }
        if (eventUpdateDto.getStartDate() == null) {
            throw new DataValidationException("Не заполнена дата");
        }
        if (eventUpdateDto.getOwnerId() == null) {
            throw new DataValidationException("Не заполнен пользователь");
        }
    }

    public void validate(List<String> skillListDto, List<Skill> skillListUser) {
        boolean flag = skillListUser.stream().anyMatch(skillUser -> {
            return skillListDto.stream().anyMatch(skill -> skill.equals(skillUser.getTitle()));
        });
        if (flag == false) {
            throw new DataValidationException("Нет подходящих скилов");
        }
    }

    public void validate(Optional<Event> eventOptional) {
        eventOptional.orElseThrow(() -> new DataValidationException("Такого эвента нет"));
    }

}
