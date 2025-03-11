package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventUpdateDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.filters.event.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventMapper eventMapper;
    private final SkillService skillService;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final List<EventFilter> eventFilters;
    private final static int BATCH_SIZE = 100;

    @Transactional
    public EventDto create(EventDto eventDto) throws EntityNotFoundException {
        Event event = eventMapper.toEntityEvent(eventDto);
        User owner = userRepository.findById(eventDto.getOwnerId())
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + eventDto.getOwnerId()
                        + " не найден"));
        event.setOwner(owner);
        validateEventRelatedSkills(eventDto.getRelatedSkills(), skillService.getSkillsIds(owner.getSkills()));
        event.setRelatedSkills(skillService.getAllSkills(eventDto.getRelatedSkills()));
        event = eventRepository.save(event);
        return eventMapper.toDto(event);
    }

    @Transactional
    public EventDto updateEvent(EventUpdateDto eventDto) {
        Event event = eventRepository.getReferenceById(eventDto.getOwnerEventId());
        User owner = event.getOwner();
        if (!eventDto.getRelatedSkillIds().isEmpty()) {
            validateEventRelatedSkills(
                    eventDto.getRelatedSkillIds(),
                    skillService.getSkillsIds(owner.getSkills())
            );
            event.setRelatedSkills(skillService.getAllSkills(eventDto.getRelatedSkillIds()));
        }
        if (eventDto.getOwnerId() != null) {
            if (!Objects.equals(event.getOwner().getId(), eventDto.getUserId())) {
                throw new BusinessException("Основателя может сменить только основатель");
            }
        }
        eventMapper.updateEntityFromDto(event, eventDto);
        eventRepository.save(event);
        return eventMapper.toDto(event);
    }


    public EventDto getEvent(long eventId) {
        return eventMapper.toDto(eventRepository.getReferenceById(eventId));
    }

    @Transactional
    public void deleteEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Event with id " + eventId + " not found");
        }
        eventRepository.deleteById(eventId);
    }

    public List<Event> getParticipatedEvents(long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId);
    }

    public List<EventDto> getOwnedEvents(Long ownerId) {
        return eventRepository.findAllByUserId(ownerId).stream()
                .map(eventMapper::toDto)
                .toList();
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filterDto) {
        Stream<EventDto> events = eventRepository.findAll().stream()
                .map(eventMapper::toDto);
        for (EventFilter filter : eventFilters) {
            events = filter.apply(events, filterDto);
        }
        return events.toList();
    }

    @Transactional
    public void deactivateEventsByUser(Long userId) {
        List<Event> events = eventRepository.findAllByUserId(userId);

        events.forEach(event -> {
            event.setStatus(EventStatus.COMPLETED);
            eventRepository.save(event);
            eventRepository.delete(event);
        });
    }


    private void validateEventRelatedSkills(List<Long> relatedSkills, List<Long> ownerSkillsIds) {
        boolean hasCommonElements = relatedSkills.stream().anyMatch(ownerSkillsIds::contains);

        if (!hasCommonElements) {
            throw new BusinessException("Пользователь не обладает скилами для создания события");
        }
    }

    @Async("eventClearingExecutor")
    public void deletePastEventsBatch(List<Long> eventIds) {
        eventRepository.deleteAllByIdInBatch(eventIds);
        log.info("Удалены события:{}", eventIds);
    }

    public void clearPastEvents() {
        List<Long> events = eventRepository.findEventsByStatuses(List.of(EventStatus.COMPLETED, EventStatus.CANCELED))
                .stream()
                .map(Event::getId)
                .toList();

        ListUtils.partition(events, BATCH_SIZE).forEach(this::deletePastEventsBatch);
    }

}
