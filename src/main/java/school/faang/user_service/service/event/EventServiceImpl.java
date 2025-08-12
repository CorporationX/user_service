package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.EventService;



import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final List<EventFilter> filters;
    private final EventCleanupService eventCleanupService;
    @Value("${event-cleanup.batch-size}")
    private int batchSize;
    private final ThreadPoolTaskExecutor executor;

    @Override
    public EventDto create(EventDto eventDto) {
        validateUserSkills(eventDto);
        Event event = eventMapper.toEvent(eventDto);
        setOwner(event, eventDto.getOwnerId());
        Event savedEvent = eventRepository.save(event);

        return eventMapper.toEventDto(savedEvent);
    }

    @Override
    public EventDto getEvent(long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow();
        return eventMapper.toEventDto(event);
    }

    @Override
    public List<EventDto> getEventsByFilter(EventFilterDto eventFilterDto) {
        List<EventDto> eventDtos = eventRepository.findAll().stream()
                .map(eventMapper::toEventDto)
                .toList();

        return applyFilters(eventDtos, eventFilterDto);
    }

    @Override
    public void deleteEvent(long id) {
        eventRepository.deleteById(id);
    }

    @Override
    public EventDto updateEvent(EventDto eventDto) {
        validateUserSkills(eventDto);
        Event event = eventMapper.toEvent(eventDto);
        setOwner(event, eventDto.getOwnerId());
        Event savedEvent = eventRepository.save(event);

        return eventMapper.toEventDto(savedEvent);
    }

    @Override
    public List<EventDto> getOwnedEvents(long userId) {
        return eventRepository.findAllByUserId(userId).stream()
                .map(eventMapper::toEventDto)
                .toList();
    }

    @Override
    public List<EventDto> getParticipatedEvents(long userId) {
        List<Event> events = eventRepository.findParticipatedEventsByUserId(userId);

        return events.stream()
                .map(eventMapper::toEventDto)
                .toList();
    }

    @Override
    public void deletePastEvents() {
        List<Event> allEvents = eventRepository.findAll();
        List<Event> pastEvents = allEvents.stream()
                .filter(event -> event.getEndDate().isBefore(LocalDateTime.now()))
                .toList();

        List<Long> pastEventIds = pastEvents.stream()
                .map(Event::getId)
                .toList();

        List<List<Long>> batches = splitIntoBatches(pastEventIds, batchSize);
        List<CompletableFuture<Void>> futures = batches.stream()
                .map(batch -> CompletableFuture.runAsync(()->eventCleanupService.deleteEventsBatch(batch),executor))
                        .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private List<List<Long>> splitIntoBatches(List<Long> initialList, int batchSize) {
        List<List<Long>> batches = new ArrayList<>();
        for (int i = 0; i < initialList.size(); i += batchSize) {
            int end = Math.min(i + batchSize, initialList.size());
            batches.add(initialList.subList(i, end));
        }
        return batches;
    }

    private void validateUserSkills(EventDto event) {
        User owner = userRepository.findById(event.getOwnerId()).orElseThrow();
        List<Long> userSkillIds = owner.getSkills().stream()
                .map(Skill::getId)
                .toList();
        if (event.getRelatedSkills() == null || userSkillIds.containsAll(event.getRelatedSkills())) {
            return;
        }
        throw new DataValidationException("Owner should have all event's skills.");
    }

    private List<EventDto> applyFilters(List<EventDto> events, EventFilterDto eventFilterDto) {
        Stream<EventDto> eventDtoStream = events.stream();
        for (EventFilter filter : filters) {
            if (filter.isApplicable(eventFilterDto)) {
                eventDtoStream = filter.apply(eventDtoStream, eventFilterDto);
            }
        }

        return eventDtoStream.toList();
    }

    private void setOwner(Event event, long ownerId) {
        User owner = userRepository.findById(ownerId).orElseThrow();
        event.setOwner(owner);
    }
}
