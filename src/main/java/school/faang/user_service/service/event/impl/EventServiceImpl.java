package school.faang.user_service.service.event.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.event.filter.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.common.RecordNotFoundException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.event.EventSpecification;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validation.event.EventValidation;

import java.util.ArrayList;
import java.util.List;

import static school.faang.user_service.util.LogsConstants.DELETED_EVENT_MESSAGE;
import static school.faang.user_service.util.LogsConstants.EMPTY_FILTER;
import static school.faang.user_service.util.LogsConstants.EVENT_NOT_FOUND;
import static school.faang.user_service.util.LogsConstants.USER_NOT_FOUND;
import static school.faang.user_service.util.ValidationUtils.executeIfNotNull;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final EventValidation eventValidation;
    private final UserContext userContext;

    @Transactional
    @Override
    public Event create(Event event, List<Long> eventSkillsIds) {
        Long ownerId = userContext.getUserId();
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RecordNotFoundException(
                        String.format(USER_NOT_FOUND, ownerId)));

        eventValidation.validateUserHasAllEventSkills(eventSkillsIds, owner);

        List<Skill> eventSkills = skillRepository.findAllById(eventSkillsIds);
        event.setOwner(owner);
        event.setRelatedSkills(new ArrayList<>(eventSkills));
        log.info("Создание нового ивента: {}", event);
        return eventRepository.save(event);
    }

    @Transactional
    @Override
    public Event updateEvent(Event eventUpdates, List<Long> eventSkillsIds, long id) {
        long ownerId = userContext.getUserId();
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RecordNotFoundException(
                        String.format(USER_NOT_FOUND, ownerId)));
        Event event = eventRepository.findById(id).orElseThrow(() -> new RecordNotFoundException(
                String.format(EVENT_NOT_FOUND, id)));

        eventValidation.isUserEventOwner(ownerId, event.getOwner().getId());

        eventValidation.validateUserHasAllEventSkills(eventSkillsIds, owner);

        executeIfNotNull(eventUpdates.getTitle(), () -> event.setTitle(eventUpdates.getTitle()));
        executeIfNotNull(eventUpdates.getDescription(), () -> event.setDescription(eventUpdates.getDescription()));
        executeIfNotNull(eventUpdates.getStartDate(), () -> event.setStartDate(eventUpdates.getStartDate()));
        executeIfNotNull(eventUpdates.getEndDate(), () -> event.setEndDate(eventUpdates.getEndDate()));
        executeIfNotNull(eventUpdates.getLocation(), () -> event.setLocation(eventUpdates.getLocation()));
        executeIfNotNull(eventUpdates.getMaxAttendees(), () -> event.setMaxAttendees(eventUpdates.getMaxAttendees()));
        executeIfNotNull(eventUpdates.getRelatedSkills(), () -> {
            List<Skill> eventSkills = skillRepository.findAllById(eventSkillsIds);
            event.setRelatedSkills(new ArrayList<>(eventSkills));
        });
        executeIfNotNull(eventUpdates.getType(), () -> event.setType(eventUpdates.getType()));
        executeIfNotNull(eventUpdates.getStatus(), () -> event.setStatus(eventUpdates.getStatus()));

        log.info("Обновление ивента: {}", event);
        return eventRepository.save(event);
    }

    @Transactional(readOnly = true)
    @Override
    public Event getEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new RecordNotFoundException(
                        String.format(EVENT_NOT_FOUND, eventId)
                ));
    }

    @Transactional(readOnly = true)
    @Override
    public List<Event> getEventsByFilter(EventFilterDto filter) {
        if ((filter.getTitle() == null || filter.getTitle().isBlank()) &&
        filter.getOwnerId() == null && filter.getStartDate() == null) {
            throw new IllegalArgumentException(EMPTY_FILTER);
        }
        Specification<Event> spec = EventSpecification.withFilter(filter);
        return eventRepository.findAll(spec);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Event> getOwnedEvents() {
        long ownerId = userContext.getUserId();
        log.info("Получен запрос на получение иваентов пользователя с ownerId: {}", ownerId);
        return eventRepository.findAllByUserId(ownerId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Event> getParticipatedEvents() {
        long userId = userContext.getUserId();
        log.info("Получен запрос на получение иваентов, в которых участвует пользователь с userId: {}", userId);
        return eventRepository.findParticipatedEventsByUserId(userId);
    }

    @Transactional
    @Override
    public String deleteEvent(long eventId) {
        Long ownerId = eventRepository.findOwnerIdByEventId(eventId)
                .orElseThrow(() -> new RecordNotFoundException(
                        String.format(EVENT_NOT_FOUND, eventId)
                ));
        long userId = userContext.getUserId();

        eventValidation.isUserEventOwner(userId, ownerId);

        eventRepository.deleteById(eventId);
        return String.format(DELETED_EVENT_MESSAGE, eventId);
    }
}
