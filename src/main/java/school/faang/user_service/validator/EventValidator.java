package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventValidator {

    public void validate(String title, LocalDateTime startDate, Long ovnerId) {
        if (title == null) {
            throw new DataValidationException("Не заполнено название");
        }
        if (startDate == null) {
            throw new DataValidationException("Не заполнена дата");
        }
        if (ovnerId == null) {
            throw new DataValidationException("Не заполнен пользователь");
        }
    }

    public void validate(List<String> skillListDto, List<Skill> skillListUser) {
        boolean flag = skillListUser.stream().anyMatch(skillUser ->
                skillListDto.stream().anyMatch(skill -> skill.equals(skillUser.getTitle())));
        if (!flag) {
            throw new DataValidationException("Нет подходящих скилов");
        }
    }

}
