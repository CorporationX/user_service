package school.faang.user_service.service.event;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.UpdateEventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.user.UserRepository;
import java.util.List;
import java.util.stream.Stream;


@Data
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final UserContext userContext;
    private final List<EventFilter> filters;


    @Override
    public EventDto create(EventDto eventDto) {
        Event event = eventMapper.toEvent(eventDto);
        eventRepository.save(event);
        return eventMapper.toEventDto(event);
    }


    @Override
    public EventDto update(long eventId, UpdateEventDto updateEventDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Пустое значение"));
        eventMapper.update(updateEventDto, event);
        return eventMapper.toEventDto(event);
    }

    @SuppressWarnings({"checkstyle:LineLength", "checkstyle:CommentsIndentation"})
    @Override
    public List<EventDto> getByFilters(EventFilterDto eventFilterDto) {
        Stream<Event> filteredEvents = eventRepository.findAll().stream();
        for (EventFilter eventFilter : filters) {
            if (eventFilter.isApplicable(eventFilterDto)) {
                filteredEvents = eventFilter.apply(filteredEvents, eventFilterDto);
            }
        }

        return filteredEvents
                .map(eventMapper::toEventDto)
                .toList();
    }

    @Override
    public void delete(long eventId) {
        eventRepository.deleteById(eventId);
    }
}
