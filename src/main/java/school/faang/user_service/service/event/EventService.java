package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.promotion.PromotionTarget;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.promotion.PromotionManagementService;
import school.faang.user_service.validator.event.EventValidator;

import java.util.*;

@Data
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    private final EventParticipationService eventParticipationService;
    private final List<EventFilter> eventFilters;
    private final EventValidator eventValidator;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final PromotionManagementService promotionManagementService;

    private Event getEventOrThrow(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));
    }

    public EventDto create(EventDto eventDto) {
        eventValidator.validateEventDto(eventDto);
        eventValidator.validateOwnerSkills(eventDto);
        Event event = eventMapper.toEvent(eventDto);

        List<Skill> skills = skillRepository.findAllByUserId(eventDto.getOwnerId());
        event.setRelatedSkills(skills);

        User user = userRepository.getById(eventDto.getOwnerId());
        event.setOwner(user);

        event = eventRepository.save(event);
        return eventMapper.toDto(event);
    }

    public EventDto getEvent(long eventId) {
        Event event = getEventOrThrow(eventId);
        return eventMapper.toDto(event);
    }

    public void deactivatePlanningUserEventsAndDeleteEvent(User user) {
        List<Event> removedEvents = new ArrayList<>();

        user.getOwnedEvents().stream()
                .filter(event -> event.getStatus().equals(EventStatus.PLANNED))
                .forEach(event -> {
                    event.setStatus(EventStatus.CANCELED);
                    eventParticipationService.deleteParticipantsFromEvent(event);
                    if (event.getAttendees() != null) {
                        event.getAttendees().clear();
                    }
                    eventRepository.deleteById(event.getId());
                    removedEvents.add(event);
                });
        user.getOwnedEvents().removeAll(removedEvents);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        promotionManagementService.removeExpiredPromotions();

        List<Event> filteredEvents = filteredEvents(filter);
        List<Event> prioritizedEvents = prioritizedEvents(filteredEvents);

        markAsShownEvents(prioritizedEvents);

        return prioritizedEvents.stream()
                .map(eventMapper::toDto)
                .toList();
    }

    public void deleteEvent(long eventId) {
        eventRepository.deleteById(eventId);
    }

    public EventDto updateEvent(EventDto eventDto) {
        Event eventFromDB = getEventOrThrow(eventDto.getId());

        if (!Objects.equals(eventFromDB.getOwner().getId(), eventDto.getOwnerId())) {
            throw new DataValidationException("the user is not the creator of the event with id: " + eventDto.getId());
        }

        eventValidator.validateEventDto(eventDto);
        eventValidator.validateOwnerSkills(eventDto);
        Event event = eventMapper.toEvent(eventDto);

        List<Skill> skills = skillRepository.findAllByUserId(eventDto.getOwnerId());
        event.setRelatedSkills(skills);

        User user = userRepository.getById(eventDto.getOwnerId());
        event.setOwner(user);

        event = eventRepository.save(event);
        return eventMapper.toDto(event);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        List<Event> eventsOwned = eventRepository.findAllByUserId(userId);

        return mapping(eventsOwned);
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        List<Event> participatedEvents = eventRepository.findParticipatedEventsByUserId(userId);

        return mapping(participatedEvents);
    }

    private List<EventDto> mapping(List<Event> events) {
        return events.stream()
                .map(eventMapper::toDto)
                .toList();
    }

    private List<Event> filteredEvents(EventFilterDto filterDto) {
        return eventFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .map(filter -> filter.toSpecification(filterDto))
                .reduce(Specification::and)
                .map(eventRepository::findAll)
                .orElseGet(Collections::emptyList);
    }

    private List<Event> prioritizedEvents(List<Event> events) {
        return events.stream()
                .sorted(Comparator.comparingInt(this::eventPriority).reversed())
                .toList();
    }

    private Integer eventPriority(Event event) {
        if (event.getOwner() != null && event.getOwner().getPromotions() != null) {
            return event.getOwner().getPromotions().stream()
                    .filter(promotion -> PromotionTarget.EVENTS.name().equals(promotion.getTarget()))
                    .findFirst()
                    .map(Promotion::getPriority)
                    .orElse(0);
        } else {
            return 0;
        }
    }

    private void markAsShownEvents(List<Event> events) {
        List<Long> promotionIds = events.stream()
                .map(Event::getOwner)
                .map(User::getPromotions)
                .filter(promotions -> !promotions.isEmpty())
                .flatMap(promotions -> promotions.stream().filter(
                        promotion -> PromotionTarget.EVENTS.name().equals(promotion.getTarget()))
                )
                .map(Promotion::getId)
                .toList();

        promotionManagementService.markAsShowPromotions(promotionIds);
    }
}
