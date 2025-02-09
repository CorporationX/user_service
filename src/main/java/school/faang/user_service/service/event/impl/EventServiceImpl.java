package school.faang.user_service.service.event.impl;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
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
    public void clearEvents() {
        LocalDateTime date = LocalDateTime.now();
        List<Long> events = eventRepository.findAllEndEvents(date);
        if (events.isEmpty()) {
            log.info("There is no events to delete.");
            return;
        }
        log.info("Found {} events to delete.", events.size());
        Iterable<List<Long>> eventPartitions = ListUtils.partition(events, batchSize);
        eventPartitions.forEach(list -> {
            eventRepository.deleteAllByIdInBatch(list);
            log.info("Delete {} rows. ID:{}", list.size(), list.toString());
        });
    }
}
