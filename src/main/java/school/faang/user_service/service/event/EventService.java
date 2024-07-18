package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.ReadEvetDto;
import school.faang.user_service.dto.event.WriteEventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.event.EventFieldFilter;
import school.faang.user_service.filter.event.EventFilterDto;
import school.faang.user_service.mapper.event.EventToReadEventDtoMapper;
import school.faang.user_service.mapper.event.WriteEventDtoToEventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.valitator.event.WriteEventValidator;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {
    private final EventRepository repository;
    private final WriteEventDtoToEventMapper writeEventDtoToEventMapper;
    private final EventToReadEventDtoMapper eventToReadEventDtoMapper;
    private final WriteEventValidator validator;
    private final List<EventFieldFilter> eventFieldFilters;

    @Transactional
    public ReadEvetDto create(WriteEventDto eventDto) {
        validator.validate(eventDto);
        var event = writeEventDtoToEventMapper.map(eventDto);
        repository.save(event);
        return eventToReadEventDtoMapper.map(event);
    }

    public List<ReadEvetDto> findAllByFilter(EventFilterDto eventFilterDto) {
        Stream<Event> eventStream = repository.findAll().stream();
        return eventFieldFilters.stream()
                .filter(eventFieldFilter -> eventFieldFilter.isApplicable(eventFilterDto))
                .flatMap(eventFieldFilter -> eventFieldFilter.apply(eventStream, eventFilterDto))
                .map(eventToReadEventDtoMapper::map)
                .toList();
    }

    public ReadEvetDto findById(Long id) {
        return repository.findById(id)
                .map(eventToReadEventDtoMapper::map)
                .orElseThrow(() -> new DataValidationException("Event not found"));
    }

    @Transactional
    public void delete(Long id) {
        Event entity = repository.findById(id).orElseThrow(() -> new DataValidationException("Event not found"));
        repository.delete(entity);
        repository.flush();
    }

    @Transactional
    public ReadEvetDto update(Long id, WriteEventDto eventDto) {
        validator.validate(eventDto);
        return repository.findById(id)
                .map(entity -> writeEventDtoToEventMapper.map(eventDto, entity))
                .map(repository::saveAndFlush)
                .map(eventToReadEventDtoMapper::map)
                .orElseThrow(() -> new DataValidationException("Event not found"));
    }


    public List<ReadEvetDto> findAllByUserId(long userId) {
        return repository.findAllByUserId(userId).stream()
                .map(eventToReadEventDtoMapper::map)
                .toList();
    }

    public List<ReadEvetDto> findParticipatedEventsByUserId(long userId) {
        return repository.findParticipatedEventsByUserId(userId).stream()
                .map(eventToReadEventDtoMapper::map)
                .toList();
    }
}
