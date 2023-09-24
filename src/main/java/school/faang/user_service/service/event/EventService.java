package school.faang.user_service.service.event;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.invalidFieldException.DataValidationException;
import school.faang.user_service.exception.notFoundExceptions.event.EventNotFoundException;
import school.faang.user_service.filter.event.Filter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
    private final EventRepository eventRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    private final EventMapper eventMapper;

    private final List<Filter<Event, EventFilterDto>> filters;

    private final Executor asyncExecutor;
    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;

    @Transactional
    public EventDto create(EventDto event) {
        checkIfUserHasRequiredSkills(event);

        var attendees = userRepository.findAllById(event.getAttendees());
        Event eventEntity = eventMapper.toEntity(event);
        eventEntity.setAttendees(attendees);
        eventEntity = eventRepository.save(eventEntity);

        Event finalEventEntity = eventEntity;
        attendees.forEach(user -> user.getParticipatedEvents().add(finalEventEntity));
        userRepository.saveAll(attendees);

        return eventMapper.toDto(eventEntity);
    }

    public EventDto getEvent(long eventId) {
        var event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(String.format("Event with id: %d was not found", eventId)));
        return eventMapper.toDto(event);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        List<Event> events = eventRepository.findAll();
        var filteredEvents = filters.stream()
                .filter(eventFilter -> eventFilter.isApplicable(filter))
                .reduce(events.stream(), (stream, eventFilter) -> eventFilter.applyFilter(stream, filter), Stream::concat)
                .map(eventMapper::toDto)
                .toList();
        return filteredEvents;
    }

    public void deleteEvent(long id) {
        var event = eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException("No post with id " + id));
        event.getAttendees().forEach(user -> user.getParticipatedEvents().remove(event));
        eventRepository.deleteById(id);
    }

    public void updateEvent(EventDto eventDto) {
        checkIfUserHasRequiredSkills(eventDto);
        eventRepository.save(eventMapper.toEntity(eventDto));
    }

    public List<EventDto> getOwnedEvents(long userId) {
        return eventRepository.findAllByUserId(userId).stream().map(eventMapper::toDto).toList();
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId).stream().map(eventMapper::toDto).toList();
    }

    public void save(Event event) {
        eventRepository.save(event);
    }

    public void deletePastEvents() {
        EventFilterDto filter = EventFilterDto.builder()
                .isNeedPastEvents(true)
                .build();
        List<EventDto> pastEvents = getEventsByFilter(filter);

        deleteEventsAsync(pastEvents);
    }

    private void deleteEventsAsync(List<EventDto> pastEvents) {
        List<List<EventDto>> subLists = getSubLists(pastEvents);
        for (List<EventDto> list : subLists) {
            CompletableFuture.runAsync(
                            () -> eventRepository.deleteAllById(
                                    list.stream()
                                            .map(EventDto::getId)
                                            .toList()
                            )
                            , asyncExecutor)
                    .thenRun(() -> log.info("Finished deleting past events"));
        }
    }

    private List<List<EventDto>> getSubLists(List<EventDto> pastEvents) {
        int size = pastEvents.size();
        int subListSize = size / batchSize;
        if (size % batchSize != 0) {
            subListSize++;
        }
        List<List<EventDto>> subLists = new ArrayList<>();
        for (int i = 0; i < subListSize; i++) {
            int fromIndex = i * batchSize;
            int toIndex = Math.min(i * batchSize + batchSize, size);
            subLists.add(pastEvents.subList(fromIndex, toIndex));
        }
        return subLists;
    }

    private void checkIfUserHasRequiredSkills(EventDto event) {
        long ownerId = event.getOwnerId();
        var relatedSkills = event.getRelatedSkills();
        var userSkills = skillRepository.findAllByUserId(ownerId);

        for (SkillDto skill : relatedSkills) {
            boolean userHasSkill = userSkills.stream()
                    .anyMatch(userSkill -> userSkill.getId() == skill.getId());
            if (!userHasSkill) {
                throw new DataValidationException("User does not have the required skills for the event");
            }
        }
    }
}
