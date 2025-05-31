package school.faang.user_service.service.event;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.enums.Plan;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.kafka.events.AnalyticsEvent;
import school.faang.user_service.kafka.producer.DataSender;
import school.faang.user_service.kafka.producer.KafkaTopics;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.analytics.AnalyticsEventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.promotion.utils.EventPromotionsViewCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventServiceUtils eventServiceUtils;
    private final DataSender dataSender;
    private final KafkaTopics kafkaTopics;
    private final AnalyticsEventMapper analyticsEventMapper;
    private final EventPromotionsViewCalculator viewCalculator;

    @Transactional
    public EventDto create(EventDto eventDto) {
        eventServiceUtils.checkOwnerHasRelatedSkills(eventDto);
        Event event = eventMapper.toEntity(eventDto);
        return eventMapper.toDto(eventRepository.save(event));
    }

    @Transactional
    public EventDto getEvent(Long eventId) {
        return eventMapper.toDto(eventRepository.findById(eventId).orElseThrow(() ->
                new DataValidationException("Event not found")));
    }

    @Transactional
    public List<EventDto> getEventsByFilter(EventFilterDto filter, PageRequest pageRequest, Long userId) {
        Map<Plan, Integer> planIntegerMap = viewCalculator.calculatePromotedViews(pageRequest.getPageSize());
        Slice<Event> eventsVipPromotion = eventRepository.findAllActivePromotionByPlan(Plan.VIP,
                PageRequest.of(pageRequest.getPageNumber(), planIntegerMap.get(Plan.VIP)));
        Slice<Event> eventsGoldPromotion = eventRepository.findAllActivePromotionByPlan(Plan.GOLD,
                PageRequest.of(pageRequest.getPageNumber(), planIntegerMap.get(Plan.GOLD)));
        Slice<Event> eventsPlusPromotion = eventRepository.findAllActivePromotionByPlan(Plan.PLUS,
                PageRequest.of(pageRequest.getPageNumber(), planIntegerMap.get(Plan.PLUS)));
        Slice<Event> eventsNoPromotion = eventRepository.findAllWithoutPromotion(PageRequest.of(pageRequest.getPageNumber(),
                planIntegerMap.get(null)));

        List<Event> promotedEvents = new ArrayList<>();
        eventsVipPromotion.forEach(promotedEvents::add);
        eventsGoldPromotion.forEach(promotedEvents::add);
        eventsPlusPromotion.forEach(promotedEvents::add);

        List<Event> allEvents = new ArrayList<>(promotedEvents);
        eventsNoPromotion.forEach(allEvents::add);

        List<Event> promotedFilteredEvents = eventServiceUtils.filterEvents(promotedEvents.stream(), filter).toList();
        List<Event> allFilteredEvents = eventServiceUtils.filterEvents(allEvents.stream(), filter).toList();

        sendPromotedEventsAnalytics(promotedFilteredEvents, userId);
        sendAllEventsAnalytics(allFilteredEvents, userId);

        return eventMapper.toDtoList(allFilteredEvents);
    }

    private void sendAllEventsAnalytics(List<Event> allFilteredEvents, long userId) {
        for (Event event : allFilteredEvents) {
            AnalyticsEvent analyticsEvent = analyticsEventMapper.fromEvent(event, userId);
            dataSender.send(kafkaTopics.getAnalyticsCreatedTopic(), analyticsEvent);
            log.info("AnalyticsEvent = {} sent to AnalyticsCreatedTopic", analyticsEvent);
        }
    }

    private void sendPromotedEventsAnalytics(List<Event> promotedFilteredEvents, long userId) {
        for (Event event : promotedFilteredEvents) {
            AnalyticsEvent analyticsEvent = analyticsEventMapper.fromEvent(event, userId);
            dataSender.send(kafkaTopics.getAnalyticsProfileEventTopic(), analyticsEvent);
            log.info("AnalyticsEvent = {} sent to ProfileEventTopic", analyticsEvent);
        }
    }

    @Transactional
    public void deleteEvent(Long eventId) {
        eventRepository.deleteById(eventId);
    }

    @Transactional
    public EventDto updateEvent(EventDto eventDto) {
        eventServiceUtils.checkOwnerHasRelatedSkills(eventDto);
        return eventMapper.toDto(eventRepository.save(eventMapper.toEntity(eventDto)));
    }

    @Transactional
    public List<EventDto> getOwnedEvents(Long userId) {
        return eventRepository.findAllByUserId(userId).stream()
                .map(eventMapper::toDto)
                .toList();
    }

    @Transactional
    public List<EventDto> getParticipatedEvents(long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId).stream()
                .map(eventMapper::toDto)
                .toList();
    }
}
