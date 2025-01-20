package school.faang.user_service.validation.event;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.validation.CustomValidation;

public class EventValidator implements ConstraintValidator<CustomValidation, EventDto> {

    @Override
    public boolean isValid(EventDto eventDto, ConstraintValidatorContext context) {
        if (eventDto == null || eventDto.getStartTime() == null || eventDto.getEndTime() == null) {
            return false;
        }

        if (eventDto.getStartTime().isAfter(eventDto.getEndTime())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Start time must be before end")
                    .addPropertyNode("startTime")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
