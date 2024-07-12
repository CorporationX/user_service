package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.EventCreateEditDto;
import school.faang.user_service.dto.event.EventReadDto;
import school.faang.user_service.entity.event.Event;
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
    private final List<EventFilter> eventFilters;

    @Transactional
    public EventReadDto create(EventCreateEditDto eventDto) {
        validator.validate(eventDto);
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
                .map(readMapper::map);
    }

    @Transactional
    public void delete(Long id) {
        //TODO:12.07.2024 не понимаю, NOT_FOUND это же не совсем валидация? посути это запрос
        // на не существующий контент и это ведь обязанность контроллера выкинуть HttpStatus.NOT_FOUND? или всетаки
        // пусть сервайс ошибку валидации выкидывает?

        Event entity = repository.findById(id).orElseThrow(() -> new DataValidationException("Event not found"));
        repository.delete(entity);
        repository.flush();
    }

    @Transactional
    public Optional<EventReadDto> update(Long id, EventCreateEditDto eventDto) {
        validator.validate(eventDto);
        return repository.findById(id)
                .map(entity -> createEditMapper.map(eventDto, entity))
                .map(repository::saveAndFlush)
                .map(readMapper::map);
    }


    public List<EventReadDto> findAllByUserId(long userId) {
        return repository.findAllByUserId(userId).stream()
                .map(readMapper::map)
                .toList();
    }

    public List<EventReadDto> findParticipatedEventsByUserId(long userId) {
        return repository.findParticipatedEventsByUserId(userId).stream()
                .map(readMapper::map)
                .toList();
    }
}
