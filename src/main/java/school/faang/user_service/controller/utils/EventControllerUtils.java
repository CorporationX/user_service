package school.faang.user_service.controller.utils;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.DataValidationException;

@Component
public class EventControllerUtils {
    public void isValidDateRange(EventDto event) {
        if (event.getEndDate() != null) {
            if (!event.getStartDate().isBefore(event.getEndDate())) {
                throw new DataValidationException("The end date must be after the start date");
            }
        }
    }

    public void isValidDateRange(EventFilterDto eventFilter) {
        if (eventFilter.getStartDate() != null && eventFilter.getEndDate() != null) {
            if (!eventFilter.getStartDate().isBefore(eventFilter.getEndDate())) {
                throw new DataValidationException("The end date must be after the start date");
            }
        }
    }
}
