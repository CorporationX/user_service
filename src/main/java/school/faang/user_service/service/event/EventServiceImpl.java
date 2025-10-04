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
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final UserContext userContext;


    @Override
    public EventDto create(EventDto eventDto) {
        Event event = eventMapper.toEvent(eventDto);
        eventRepository.save(event);
        return eventMapper.toEventDto(event);
    }


    @Override
    public EventDto update(long eventId, UpdateEventDto updateEventDto) {
        Event event = eventRepository.findById(eventId).orElse(null);
        eventMapper.update(updateEventDto, event);
        return eventMapper.toEventDto(event);
    }

    @SuppressWarnings({"checkstyle:LineLength", "checkstyle:CommentsIndentation"})
    @Override
    public List<EventDto> getByFilters(EventFilterDto filters) {
        List<Event> events = eventRepository.findAll();

//        return events.stream()
//
//                .filter(event -> filters.getTitleContains() == null
//                        || event.getTitle().toLowerCase().contains(filters.getTitleContains().toLowerCase()))
//
//
//                .filter(event -> filters.getDescriptionContains() == null
//                        || event.getDescription().toLowerCase().contains(filters.getDescriptionContains().toLowerCase()))
//
//
//                .filter(event -> filters.getOwnerId() == 0
//                        || (event.getOwner() != null && event.getOwner().getId() == filters.getOwnerId()))
//
//                .filter(event -> filters.getParticipantId() == 0
//                        || (event.getParticipants() != null &&
//                        event.getParticipants().stream()
//                                .anyMatch(p -> p.getId() == filters.getParticipantId())))
//
//
//                .filter(event -> filters.getEventType() == null
//                        || event.getEventType() == filters.getEventType())
//
//                .map(eventMapper::toDto)
//                .collect(Collectors.toList());
        return null;
    }

    @Override
    public void delete(long eventId) {
        eventRepository.deleteById(eventId);
    }
}
