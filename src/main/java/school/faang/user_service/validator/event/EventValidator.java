package school.faang.user_service.validator.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.Optional;

@Component
public class EventValidator {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Autowired
    public EventValidator(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public void validateEventInController(EventDto eventDto) {
        validateTitle(eventDto);
        validateStartDate(eventDto);
        validateOwnerId(eventDto);
    }

    public void validateEventInService(EventDto eventDto) {
        Optional<User> optionalOwner = userRepository.findById(eventDto.getOwnerId());
        if (optionalOwner.isEmpty()){
            throw new DataValidationException("Invalid ownerId");
        }
    }

    public void validateTitle(EventDto eventDto) {
        if (eventDto.getTitle() == null || eventDto.getTitle().isBlank()) {
            throw new DataValidationException("Invalid title");
        }
        //TODO: Add validation of an existing title based on the event repository
    }

    public void validateStartDate(EventDto eventDto) {
        if (eventDto.getStartDate() == null) {
            throw new DataValidationException("Invalid startDate");
        }
    }

    public void validateOwnerId(EventDto eventDto) {
        if (eventDto.getOwnerId() == null || !userRepository.existsById(eventDto.getOwnerId())) {
            throw new DataValidationException("Invalid owner id");
        }
    }
}
