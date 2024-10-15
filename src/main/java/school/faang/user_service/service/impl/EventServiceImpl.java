package school.faang.user_service.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.model.dto.EventDto;
import school.faang.user_service.model.filter_dto.EventFilterDto;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.entity.Event;
import school.faang.user_service.model.entity.Promotion;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.PromotionRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.EventRepository;
import school.faang.user_service.service.EventService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final static String PROMOTION_TARGET = "event";

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final PromotionRepository promotionRepository;
    private final List<EventFilter> eventFilters;
    private final EventMapper mapper;

    @Value("${scheduler.clear-events.batchSize}")
    @Setter
    private int batchSize;

    @Override
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
        return filteredEvents.stream()
                .sorted((Comparator
                        .comparing((Event event) -> calculateCountryPriority(event, callingUser))
                        .thenComparing(this::calculatePriorityLevel)))
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

    private int calculateCountryPriority(Event event, User callingUser) {
        User eventOwner = event.getOwner();
        if (eventOwner.getPromotions() == null || eventOwner.getPromotions().isEmpty()) {
            return 1;
        }

        Promotion targetPromotion = getTargetPromotion(event);
        if (targetPromotion == null || (targetPromotion.getPriorityLevel() == 3
                && !eventOwner.getCountry().equals(callingUser.getCountry()))) {
            return 1;
        }

        return 0;
    }

    private int calculatePriorityLevel(Event event) {
        User eventOwner = event.getOwner();
        if (eventOwner.getPromotions() == null || eventOwner.getPromotions().isEmpty()) {
            return 0;
        }

        Promotion targetPromotion = getTargetPromotion(event);
        return targetPromotion != null ? -targetPromotion.getPriorityLevel() : 0;
    }

    @Override
    public void clearPastEvents() {
        List<Event> events = eventRepository.findAll();
        Optional<List<Event>> pastEventsOptional = Optional.of(events)
                .filter(e -> !e.isEmpty())
                .map(e -> e.stream()
                        .filter(event -> event.getEndDate().isBefore(LocalDateTime.now()))
                        .toList())
                .filter(pastEvents -> !pastEvents.isEmpty());

        pastEventsOptional.ifPresent(pastEvents -> {
            List<List<Event>> partitions = partitionList(pastEvents, batchSize);
            ExecutorService executor = Executors.newFixedThreadPool(partitions.size());

            for (List<Event> partition : partitions) {
                List<Long> deletableIds = partition.stream().map(Event::getId).toList();
                executor.submit(() -> eventRepository.deleteAllByIdInBatch(
                        deletableIds
                ));
            }
            executor.shutdown();
        });
    }

    private <T> List<List<T>> partitionList(List<T> list, int size) {
        int numPartitions = (list.size() + size - 1) / size;
        return IntStream.range(0, numPartitions)
                .mapToObj(i -> list.subList(i * size, Math.min((i + 1) * size, list.size())))
                .toList();
    }
}