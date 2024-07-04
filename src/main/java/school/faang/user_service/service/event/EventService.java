package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.EventCreateEditDto;
import school.faang.user_service.dto.event.EventReadDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventCreateEditMapper;
import school.faang.user_service.mapper.event.EventReadMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.valitator.event.EventCreateEditValidator;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository repository;
    private final EventCreateEditMapper createEditMapper;
    private final EventReadMapper readMapper;
    private final EventCreateEditValidator validator;

    public EventReadDto create(EventCreateEditDto eventDto) {
        var validationResult = validator.validate(eventDto);
        if (validationResult.hasErrors()) {
            throw new DataValidationException(validationResult.getErrors());
        }
        var event = createEditMapper.map(eventDto);
        repository.save(event);
        return readMapper.map(event);
    }

}
