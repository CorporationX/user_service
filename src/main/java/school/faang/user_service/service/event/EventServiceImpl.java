package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.event.CreateEventDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.UpdateEventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper mapper;
    private final UserContext userContext;
    private final EventValidator validator;

    @Override
    @Transactional
    public EventDto createEvent(CreateEventDto dto) {
        long requesterId = userContext.getUserId();
        log.info("create event requested by userId={}", requesterId);

        User owner = userRepository.getByIdOrThrow(requesterId);

        validator.validateDates(dto.startDate(), dto.endDate());
        List<Skill> eventSkills = validator.loadAndValidateSkills(dto.relatedSkillIds());
        validator.ensureOwnerHasAllSkills(owner, eventSkills);

        Event event = mapper.toEvent(dto);
        event.setOwner(owner);
        event.setRelatedSkills(eventSkills);

        Event savedEvent = eventRepository.save(event);
        log.info("event created id={} by userId={}", savedEvent.getId(), requesterId);
        return mapper.toEventDto(savedEvent);
    }

    @Override
    @Transactional
    public EventDto updateEvent(long eventId, UpdateEventDto dto) {
        long requesterId = userContext.getUserId();
        log.info("update event requested: eventId={}, requesterId={}", eventId, requesterId);

        Event event = eventRepository.getByIdOrThrow(eventId);
        validator.ensureOwner(event, requesterId);
        validator.validateDates(dto.startDate(), dto.endDate());

        mapper.update(event, dto);

        List<Skill> newSkills = validator.loadAndValidateSkills(dto.relatedSkillIds());
        validator.ensureOwnerHasAllSkills(event.getOwner(), newSkills);
        event.setRelatedSkills(newSkills);

        Event saved = eventRepository.save(event);
        log.info("event updated id={} by userId={}", saved.getId(), requesterId);
        return mapper.toEventDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDto> getEventByFilters(EventFilterDto f) {
        log.info("get events with filters: {}", f);
        List<Event> all = eventRepository.findAll();

        return all.stream()
                .filter(e -> f.titleContains() == null
                        || containsIgnoreCase(e.getTitle(), f.titleContains()))
                .filter(e -> f.descriptionContains() == null
                        || containsIgnoreCase(e.getDescription(), f.descriptionContains()))
                .filter(e -> f.type() == null
                        || e.getType() == f.type())
                .filter(e -> f.ownerId() == null
                        || (e.getOwner() != null && Objects.equals(e.getOwner().getId(), f.ownerId())))
                .filter(e -> {
                    if (f.participantId() == null) {
                        return true;
                    }
                    return e.getAttendees() != null && e.getAttendees()
                            .stream()
                            .anyMatch(u -> Objects.equals(u.getId(), f.participantId()));
                })
                .map(mapper::toEventDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteEvent(long eventId) {
        long requesterId = userContext.getUserId();
        log.info("delete event requested: eventId={}, requesterId={}", eventId, requesterId);

        Event event = eventRepository.getByIdOrThrow(eventId);
        validator.ensureOwner(event, requesterId);
        eventRepository.delete(event);

        log.info("event deleted id={} by userId={}", eventId, requesterId);
    }

    private boolean containsIgnoreCase(String text, String needle) {
        return text != null && needle != null && text.toLowerCase().contains(needle.toLowerCase());
    }
}
