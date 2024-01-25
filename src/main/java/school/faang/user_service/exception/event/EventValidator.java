package school.faang.user_service.exception.event;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;

@Service
@RequiredArgsConstructor
public class EventValidator {
    public void validate(EventDto eventDto) {
        if (eventDto.getTitle() == null) throw new DataValidationException("Не заполнено название");
        if (eventDto.getStartDate() == null) throw new DataValidationException("Не заполнена дата");
        if (eventDto.getOwnerId() == 0) throw new DataValidationException("Не заполнен пользователь");
    }
}
