package school.faang.user_service.service.event.impl;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.event.filter.EventFilter;
import school.faang.user_service.adapter.user.UserRepositoryAdapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final SkillService skillService;
    private final UserRepositoryAdapter userRepositoryAdapter;
    private final EventMapper eventMapper;
    private final List<EventFilter> eventFilters;

    @Value("${app.batch_size}")
    @Setter
    private int batchSize;
    @Value("${app.max_iterations_to_find_db}")
    @Setter
    private int maxIterations;

    @Override
    @Transactional
    public EventDto create(EventDto eventDto) {
        Event event = eventMapper.toEntity(eventDto);
        event.setOwner(userRepositoryAdapter.getUserById(eventDto.getOwnerId()));
        event.setRelatedSkills(skillService.getSkillListBySkillIds(eventDto.getRelatedSkillIds()));
        return eventMapper.toDto(eventRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public EventDto getEvent(long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Событие по id: %s не найдено!", id)));
        return eventMapper.toDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDto> getEventsByFilter(EventFilterDto filters) {
        Stream<Event> events = eventRepository.findAll().stream();
        for (EventFilter filter : eventFilters) {
            if (filter.isApplicable(filters)) {
                events = filter.apply(events, filters);
            }
        }
        return eventMapper.toDto(events.toList());
    }

    @Override
    @Transactional
    public void deleteEvent(long id) {
        eventRepository.deleteById(id);
    }

    @Override
    @Transactional
    public EventDto updateEvent(long id, EventDto eventDto) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Событие по id: %s не найдено!", id)));
        eventMapper.update(event, eventDto);
        event.setRelatedSkills(skillService.getSkillListBySkillIds(eventDto.getRelatedSkillIds()));
        return eventMapper.toDto(eventRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDto> getOwnedEvents(long userId) {
        List<Event> eventsByUserId = eventRepository.findAllByUserId(userId);
        return eventMapper.toDto(eventsByUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDto> getParticipatedEvents(long userId) {
        List<Event> participatedEventsByUserId = eventRepository.findParticipatedEventsByUserId(userId);
        return eventMapper.toDto(participatedEventsByUserId);
    }

    @Override
    @Async("cachedThreadPool")
    @Transactional
    public void clearEvents() {
        LocalDateTime date = LocalDateTime.now();
        int iterations = 0;
        while (iterations < maxIterations) {
            boolean isExit = findAndDeleteEndEvents(date);
            if (isExit) {
                break;
            }
            iterations++;
            if (iterations >= maxIterations) {
                log.error("Maximum number of loop iterations reached when worked method 'clear events'.");
            }
        }
    }

    private boolean findAndDeleteEndEvents(LocalDateTime date) {
        Pageable pageable = PageRequest.of(0, batchSize);
        Page<Long> events = eventRepository.findAllEndEvents(date, pageable);
        if (events.isEmpty()) {
            log.info("There are no events to delete.");
            return true;
        }
        log.info("Founded {} events to delete.", events.getContent().size());
        eventRepository.deleteAllByIdInBatch(events.getContent());
        log.info("Deleted {} rows. ID:{}", events.getContent().size(), events.getContent().toString());
        return false;
    }
}
