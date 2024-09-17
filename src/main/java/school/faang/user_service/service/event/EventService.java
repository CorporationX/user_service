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
import java.util.stream.Stream;

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
        deleteExpiredEventPromotions();

        return priorityFilteredEvents.stream()
                .map(mapper::toDto)
                .toList();
    }

    private List<Event> getFilteredEventsFromRepository(EventFilterDto filterDto) {
        return eventFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .map(filter -> filter.toSpecification(filterDto))
                .reduce(Specification::and)
                .map(spec -> eventRepository.findAll((Specification<Event>) spec))
                .orElseGet(Collections::emptyList);
    }

    private List<Event> getPriorityFilteredEvents(List<Event> filteredEvents, User callingUser) {

        Comparator<Event> countryAndPriorityComparator = Comparator.comparing((Event event) -> {
            if (event.getOwner().getPromotions() == null || event.getOwner().getPromotions().isEmpty()) {
                return 1;
            }

            Promotion targetPromotion = getTargetPromotion(event);

            if (targetPromotion != null &&
                    targetPromotion.getPriorityLevel() == 3 &&
                    !event.getOwner().getCountry().equals(callingUser.getCountry())) {
                return 1;
            }

            if (targetPromotion == null) {
                return 1;
            }

            return 0;
        }).thenComparing(event -> {
            if (event.getOwner().getPromotions() == null || event.getOwner().getPromotions().isEmpty()) {
                return 0;
            }
            Promotion targetPromotion = getTargetPromotion(event);
            return targetPromotion != null ? -targetPromotion.getPriorityLevel() : 0;
        });

        return filteredEvents.stream()
                .sorted(countryAndPriorityComparator)
                .toList();
    }

    private void decrementRemainingShows(List<Event> priorityFilteredEvents) {
        List<Long> promotionIds = priorityFilteredEvents.stream()
                .flatMap(event -> {
                    List<Promotion> promotions = event.getOwner().getPromotions();
                    if (promotions == null) {
                        return Stream.empty();
                    }
                    return promotions.stream()
                            .filter(promotion -> PROMOTION_TARGET.equals(promotion.getPromotionTarget()) &&
                                    promotion.getRemainingShows() > 0)
                            .map(Promotion::getId);
                })
                .toList();

        if (!promotionIds.isEmpty()) {
            promotionRepository.decreaseRemainingShows(promotionIds, PROMOTION_TARGET);
        }
    }

    private void deleteExpiredEventPromotions() {
        List<Promotion> expiredPromotions = promotionRepository.findAllExpiredPromotions(PROMOTION_TARGET);
        if (!expiredPromotions.isEmpty()) {
            promotionRepository.deleteAll(expiredPromotions);
        }
    }

    private Promotion getTargetPromotion(Event event) {
        return event.getOwner().getPromotions().stream()
                .filter(promotion -> PROMOTION_TARGET.equals(promotion.getPromotionTarget()))
                .findFirst()
                .orElse(null);
    }
}
