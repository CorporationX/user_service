package school.faang.user_service.service.event;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.promotion.PromotionTarget;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.promotion.PromotionService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventParticipationService eventParticipationService;
    private final List<EventFilter> eventFilters;
    private final EventMapper eventMapper;
    private final PromotionService promotionService;

    @Transactional
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

    @Transactional
    public List<EventDto> events(EventFilterDto filterDto) {
        promotionService.removeExpiredPromotions();

        List<Event> filteredEvents = filteredEvents(filterDto);
        List<Event> prioritizedEvents = prioritizedEvents(filteredEvents);

        markAsShownEvents(prioritizedEvents);

        return prioritizedEvents.stream()
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
                .collect(Collectors.toList());
    }

    private Integer eventPriority(Event event) {
        return event.getOwner().getPromotions().stream()
                .filter(promotion -> PromotionTarget.EVENTS.name().equals(promotion.getTarget()))
                .findFirst()
                .map(Promotion::getPriority)
                .orElse(0);
    }

    private void markAsShownEvents(List<Event> events) {
        List<Long> promotionIds = events.stream()
                .map(Event::getOwner)
                .map(User::getPromotions)
                .filter(promotions -> !promotions.isEmpty())
                .flatMap(promotions -> promotions.stream().filter(promotion -> PromotionTarget.EVENTS.name().equals(promotion.getTarget())))
                .map(Promotion::getId)
                .toList();

        promotionService.markAsShowPromotions(promotionIds);
    }
}
