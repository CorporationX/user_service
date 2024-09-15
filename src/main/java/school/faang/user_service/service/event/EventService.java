package school.faang.user_service.service.event;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.PromotionRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final static String PROMOTION_TARGET = "event";

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final PromotionRepository promotionRepository;
    private final List<EventFilter> eventFilters;
    private final EventMapper mapper;

    @Transactional
    public List<EventDto> getFilteredEvents(EventFilterDto filterDto, Long callingUserId) {
        User callingUser = userRepository.findById(callingUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Event> filteredEvents = getFilteredEventsFromRepository(filterDto);
        List<Event> priorityFilteredEvents = getPriorityFilteredEvents(filteredEvents, callingUser);

        decrementRemainingShows(priorityFilteredEvents);
        deleteExpiredProfilePromotions();

        return priorityFilteredEvents.stream()
                .map(mapper::toDto)
                .toList();
    }

    List<Event> getFilteredEventsFromRepository(EventFilterDto filterDto) {
        return eventFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .map(filter -> filter.toSpecification(filterDto))
                .reduce(Specification::and)
                .map(spec -> eventRepository.findAll((Specification<Event>) spec))
                .orElseGet(Collections::emptyList);
    }

    private List<Event> getPriorityFilteredEvents(List<Event> filteredEvents, User callingUser) {
        Comparator<Event> countryAndPriorityComparator = Comparator.comparing((Event event) -> {
            if (event.getOwner().getPromotion() != null &&
                    event.getOwner().getPromotion().getPriorityLevel() == 3 &&
                    !event.getOwner().getCountry().equals(callingUser.getCountry())) {
                return 1;
            }

            if (event.getOwner().getPromotion() != null &&
                    !event.getOwner().getPromotion().getPromotionTarget().equals(PROMOTION_TARGET)) {
                return 1;
            }

            return event.getOwner().getPromotion() != null ? 0 : 1;
        }).thenComparing(event -> event.getOwner().getPromotion() != null &&
                event.getOwner().getPromotion().getPromotionTarget().equals(PROMOTION_TARGET)
                ? -event.getOwner().getPromotion().getPriorityLevel() : 0);

        return filteredEvents.stream()
                .sorted(countryAndPriorityComparator)
                .toList();
    }

    private void decrementRemainingShows(List<Event> priorityFilteredEvents) {
        List<Long> promotionIds = priorityFilteredEvents.stream()
                .filter(event -> event.getOwner().getPromotion() != null &&
                        event.getOwner().getPromotion().getRemainingShows() > 0)
                .map(event -> event.getOwner().getPromotion().getId())
                .toList();

        if (!promotionIds.isEmpty()) {
            promotionRepository.decreaseRemainingShows(promotionIds, PROMOTION_TARGET);
        }
    }

    private void deleteExpiredProfilePromotions() {
        List<Promotion> expiredPromotions = promotionRepository.findAllExpiredPromotions(EventService.PROMOTION_TARGET);
        if (!expiredPromotions.isEmpty()) {
            promotionRepository.deleteAll(expiredPromotions);
        }
    }
}
