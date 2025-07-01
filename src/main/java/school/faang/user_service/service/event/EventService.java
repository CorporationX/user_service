package school.faang.user_service.service.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.promotion.PromotionStatus;
import school.faang.user_service.entity.skill.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.event.ActivePromotionExistsException;
import school.faang.user_service.exception.event.EventNotFoundException;
import school.faang.user_service.exception.event.EventValidationException;
import school.faang.user_service.model.event.EventFilter;
import school.faang.user_service.aspect.score.ScoreActionType;
import school.faang.user_service.aspect.score.TrackActionScore;
import school.faang.user_service.repository.event.EventFilterRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.promotion.PromotionRedisService;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validation.event.EventValidator;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
public class EventService {
    private final UserService userService;
    private final SkillService skillService;
    private final EventRepository eventRepository;
    private final EventFilterRepository eventFilterRepository;
    private final EventValidator eventValidator;
    private final UserContext userContext;
    private final EventRedisService eventRedisService;
    private final PromotionRedisService promotionRedisService;
    private final Executor executor;

    public EventService(UserService userService,
                        SkillService skillService,
                        EventRepository eventRepository,
                        EventFilterRepository eventFilterRepository,
                        EventValidator eventValidator,
                        UserContext userContext,
                        EventRedisService eventRedisService,
                        PromotionRedisService promotionRedisService,
                        @Qualifier("getEventExecutor") Executor executor) {
        this.userService = userService;
        this.skillService = skillService;
        this.eventRepository = eventRepository;
        this.eventFilterRepository = eventFilterRepository;
        this.eventValidator = eventValidator;
        this.userContext = userContext;
        this.eventRedisService = eventRedisService;
        this.promotionRedisService = promotionRedisService;
        this.executor = executor;
    }

    @Transactional
    public Event createEvent(Event event, List<Long> relatedSkillIds) {
        long userId = userContext.getUserId();
        User owner = userService.getUserByIdOrThrow(userId);
        event.setOwner(owner);
        event.setStatus(EventStatus.PLANNED);

        if (relatedSkillIds != null && !relatedSkillIds.isEmpty()) {
            List<Skill> skills = skillService.getSkillsByIds(relatedSkillIds);
            eventValidator.validateOwnerHasSkills(userId, skills);
            event.setRelatedSkills(skills);
        }

        eventValidator.validateEventDates(event.getStartDate(), event.getEndDate());
        return eventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public Event getEventById(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(
                        eventId));
    }

    @Transactional
    public void deleteEventById(long eventId) {
        Event event = getEventById(eventId);

        boolean hasActivePromotion = event.getPromotions().stream()
                .anyMatch(promotion -> Objects.equals(promotion.getStatus(), PromotionStatus.ACTIVE));

        if (hasActivePromotion) {
            throw new ActivePromotionExistsException(eventId);
        }

        eventRepository.deleteById(eventId);
        log.info("Event {} has been deleted", event);
    }

    @Transactional
    @TrackActionScore(ScoreActionType.COMPLETE_EVENT)
    public Event updateEventData(Event event, List<Long> relatedSkillIds) {
        long userId = userContext.getUserId();
        if (!Objects.equals(userId, event.getOwner().getId())) {
            throw new EventValidationException("Обновление разрешено только автору события");
        }

        if (relatedSkillIds != null && !relatedSkillIds.isEmpty()) {
            List<Skill> skills = skillService.getSkillsByIds(relatedSkillIds);
            eventValidator.validateOwnerHasSkills(userId, skills);
            event.getRelatedSkills().clear();
            event.getRelatedSkills().addAll(skills);
        }

        eventValidator.validateEventDates(event.getStartDate(), event.getEndDate());
        Event savedEvent = eventRepository.save(event);
        log.info("Event {} has been saved", savedEvent);

        eventRedisService.updatePromotedEvent(savedEvent);
        return savedEvent;
    }

    @Transactional(readOnly = true)
    public List<Event> getOwnedEvents(long userId) {
        return eventRepository.findAllByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Event> getParticipatedEvents(long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Event> getEventsByFilter(EventFilter filter) {
        List<Long> filteredEventIds = eventFilterRepository.findByFilter(filter);

        List<CompletableFuture<Event>> futureEvents = filteredEventIds.stream()
                .map(eventId -> CompletableFuture.supplyAsync(() ->
                        eventRedisService.getEventFromRedisById(eventId)
                                .orElseGet(() -> {
                                    Event event = getEventById(eventId);
                                    eventRedisService.addEventInRedis(event);
                                    return event;
                                }), executor))
                .toList();

        List<Event> events = futureEvents.stream()
                .map(CompletableFuture::join)
                .toList();

        promotionRedisService.decrementCountViewByEventIds(filteredEventIds);

        return events;
    }
}
