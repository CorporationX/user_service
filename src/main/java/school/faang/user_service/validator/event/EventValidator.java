package school.faang.user_service.validator.event;

import school.faang.user_service.dto.event.EventDto;

public interface EventValidator {
    void validate(EventDto eventDto);

    void validateOwnersRequiredSkills(EventDto event);
}
