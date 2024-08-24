package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import school.faang.user_service.validator.WriteEventValidator;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class EventService {
    private final EventRepository eventRepository;
    private final WriteEventDtoToEventMapper writeEventDtoToEventMapper;
    private final EventToReadEventDtoMapper eventToReadEventDtoMapper;
    private final WriteEventValidator validator;
    private final List<EventFieldFilter> eventFieldFilters;

    @Transactional
    public ReadEvetDto create(WriteEventDto eventDto) {
        validator.validate(eventDto);
        var event = writeEventDtoToEventMapper.map(eventDto);
        eventRepository.save(event);
        return eventToReadEventDtoMapper.map(event);
    }

    public List<ReadEvetDto> findAllByFilter(EventFilterDto eventFilterDto) {
        Stream<Event> eventStream = eventRepository.findAll().stream();
        return eventFieldFilters.stream()
                .filter(eventFieldFilter -> eventFieldFilter.isApplicable(eventFilterDto))
                .flatMap(eventFieldFilter -> eventFieldFilter.apply(eventStream, eventFilterDto))
                .map(eventToReadEventDtoMapper::map)
                .toList();
    }

    public ReadEvetDto findById(Long id) {
        return eventRepository.findById(id)
                .map(eventToReadEventDtoMapper::map)
                .orElseThrow(() -> {
                    log.error("EventService.findById: event with id %s not found");
                    return new DataValidationException(String.format("Event with id: %s not found", id));
                });
    }

    @Transactional
    public void delete(Long id) {
        Event entity = eventRepository.findById(id).orElseThrow(() -> new DataValidationException(String.format("Event with id: %s not found", id)));
        eventRepository.delete(entity);
        eventRepository.flush();
    }

    @Transactional
    public ReadEvetDto update(Long id, WriteEventDto eventDto) {
        validator.validate(eventDto);
        return eventRepository.findById(id)
                .map(entity -> writeEventDtoToEventMapper.map(eventDto, entity))
                .map(eventRepository::saveAndFlush)
                .map(eventToReadEventDtoMapper::map)
                .orElseThrow(() -> {
                            log.error("EventService.update: event with id %s not found");
                            return new DataValidationException(String.format("Event with id: %s not found", id));
                        }
                );
    }


    public List<ReadEvetDto> findAllByUserId(long userId) {
        return eventRepository.findAllByUserId(userId).stream()
                .map(eventToReadEventDtoMapper::map)
                .toList();
    }

    public List<ReadEvetDto> findParticipatedEventsByUserId(long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId).stream()
                .map(eventToReadEventDtoMapper::map)
                .toList();
    }
}
