package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.EventValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventValidator eventValidator;
    private final List<EventFilter> eventFilters;

    @Value("${events.sublist_size}")
    private int sublistSize;

    @Value("${events.threads_num}")
    private int threadsNum;

    public EventDto create(EventDto eventDto) {
        eventValidator.checkOwnerSkills(eventDto);
        Event eventEntity = eventMapper.toEntity(eventDto);
        return eventMapper.toDto(eventRepository.save(eventEntity));
    }

    public EventDto getEventDto(long eventId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        Event event = optionalEvent.orElseThrow(() -> new EntityNotFoundException("Event with Id " + eventId + " not found."));
        return eventMapper.toDto(event);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filterDto) {
        Stream<Event> events = eventRepository.findAll().stream();

        for (EventFilter eventFilter : eventFilters) {
            if (eventFilter.isApplicable(filterDto)) {
                events = eventFilter.apply(events, filterDto);
            }
        }
        return eventMapper.toListDto(events.collect(Collectors.toList()));
    }

    public void deleteEvent(long eventId) {
        eventRepository.deleteById(eventId);
    }

    public EventDto updateEvent(EventDto eventDto) {
        Long eventDtoId = eventDto.getId();
        Event event = eventRepository.findById(eventDtoId)
                .orElseThrow(() -> new EntityNotFoundException("Event with Id " + eventDtoId + " not found"));
        eventValidator.checkOwnerSkills(eventDto);

        return eventMapper.toDto(eventRepository.save(event));
    }

    public List<EventDto> getOwnedEvents(long userId) {
        return eventMapper.toListDto(eventRepository.findAllByUserId(userId));
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        List<Event> participatedEventsByUserId = eventRepository.findParticipatedEventsByUserId(userId);
        return eventMapper.toListDto(participatedEventsByUserId);
    }

    @Transactional
    public void deleteEndEvents() {
        ExecutorService executorService = Executors.newFixedThreadPool(threadsNum);

        List<Event> events = eventRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        List<Long> ids = events.stream()
                .filter(event -> event.getEndDate().isBefore(now))
                .map(Event::getId)
                .toList();

        if (!ids.isEmpty()) {
            List<CompletableFuture<Void>> futureList = new ArrayList<>();
            for (int i = 0; i < ids.size(); i += sublistSize) {
                int endIndex = i + sublistSize;
                if (endIndex > ids.size()) {
                    endIndex = ids.size();
                }
                List<Long> partIds = ids.subList(i, endIndex);
                log.info("Remove event from {} to {} index in list", i, endIndex);
                CompletableFuture<Void> futureTask =
                        CompletableFuture.runAsync(() -> eventRepository.deleteAllById(partIds));
                futureList.add(futureTask);
            }
            executorService.shutdown();
            CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
        }
    }
}
