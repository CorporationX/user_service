package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.EventCreateEditDto;
import school.faang.user_service.dto.event.EventReadDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.filter.event.EventFilterDto;
import school.faang.user_service.mapper.event.EventCreateEditMapper;
import school.faang.user_service.mapper.event.EventReadMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.valitator.event.EventCreateEditValidator;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository repository;
    private final EventCreateEditMapper createEditMapper;
    private final EventReadMapper readMapper;
    private final EventCreateEditValidator validator;
    private final EventReadMapper eventReadMapper;
    private final List<EventFilter> eventFilters;

    @Transactional
    public EventReadDto create(EventCreateEditDto eventDto) {
        validate(eventDto);
        var event = createEditMapper.map(eventDto);
        repository.save(event);
        return readMapper.map(event);
    }

    public List<EventReadDto> findAllBy(EventFilterDto filter) {
        return repository.findAll().stream()
                .filter(event -> eventFilters.stream()
                        .allMatch(filterField -> filterField.apply(event, filter)))
                .map(readMapper::map)
                .toList();
    }

    public Optional<EventReadDto> findById(Long id) {
        return repository.findById(id)
                .map(eventReadMapper::map);
    }

    @Transactional
    public boolean delete(Long id) {
        return repository.findById(id)
                .map(entity -> {
                    repository.delete(entity);
                    repository.flush();
                    return true;
                })
                .orElse(false);
    }

    @Transactional
    public Optional<EventReadDto> update(Long id, EventCreateEditDto eventDto) {
        this.validate(eventDto);
        return repository.findById(id)
                .map(entity -> createEditMapper.map(eventDto, entity))
                .map(repository::saveAndFlush)
                .map(readMapper::map);
    }

    private void validate(EventCreateEditDto eventDto) {
        var validationResult = validator.validate(eventDto);
        if (validationResult.hasErrors()) {
            throw new DataValidationException(validationResult.getErrors());
        }
    }

    public List<EventReadDto> findAllByUserId(long userId) {
        return repository.findAllByUserId(userId).stream()
                .map(eventReadMapper::map)
                .toList();
    }

    public List<EventReadDto> findParticipatedEventsByUserId(long userId) {
        return repository.findParticipatedEventsByUserId(userId).stream()
                .map(eventReadMapper::map)
                .toList();
    }
}
