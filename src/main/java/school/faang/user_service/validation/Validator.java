package school.faang.user_service.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.Exceptions;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class Validator {
    private final Exceptions exception;

    public boolean checkLongFieldIsNullAndZero(Long event) {
        checkFieldIsNull(event == null);
        return !(event <= 0);
    }

    public boolean checkStringIsNullAndEmpty(String event) {
        checkFieldIsNull(event == null);
        return event.trim().isEmpty();
    }

    public boolean checkLocalDateTimeIsNull(LocalDateTime event) {
        checkFieldIsNull(event == null);
        return false;
    }

    public void checkFieldIsNull(boolean event) {
        if (event) exception.validateInputValuesIsNull();
    }

    public void checkEventDto(EventDto eventDto) {
        if (checkStringIsNullAndEmpty(eventDto.getTitle())
                || checkLocalDateTimeIsNull(eventDto.getStartDate())
                || !checkLongFieldIsNullAndZero(eventDto.getOwnerId())) {
            exception.validateInputValuesEmptyMessage();
        }

    }

    public void checkOwnerHasEventRelatedSkills(EventDto eventDto) {
        if (checkStringIsNullAndEmpty(eventDto.getTitle())
                || checkLocalDateTimeIsNull(eventDto.getStartDate())
                || !checkLongFieldIsNullAndZero(eventDto.getOwnerId())) {
            exception.validateInputValuesEmptyMessage();
        }

    }
}
