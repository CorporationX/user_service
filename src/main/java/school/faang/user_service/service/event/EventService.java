package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.PromotionServiceClient;
import school.faang.user_service.config.AppConfig;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventResponse;
import school.faang.user_service.dto.promotion.EventPromotionRequest;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.service.event.filter.EventFilter;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.util.ConverterUtil;
import school.faang.user_service.validator.UserValidator;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static school.faang.user_service.config.KafkaConstants.EVENT_KEY;
import static school.faang.user_service.config.KafkaConstants.PAYMENT_PROMOTION_TOPIC;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final List<EventFilter> filters;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ConverterUtil converterUtil;
    private final PromotionServiceClient promotionServiceClient;
    private final UserValidator userValidator;
    private final AppConfig appConfig;

    public EventDto create(EventDto eventDto) {
        validateEventDto(eventDto);

        User owner = validateOwnerAndSkills(eventDto.ownerId(), eventDto.relatedSkills());
        Event event = eventMapper.toEntity(eventDto);
        event.setOwner(owner);

        Event savedEvent = eventRepository.save(event);
        return eventMapper.toDto(savedEvent);
    }

    public EventDto getEvent(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException("Event not found with ID: " + eventId));
        return eventMapper.toDto(event);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filterDto) {
        List<Event> events = eventRepository.findAll();
        Stream<Event> eventStream = events.stream();

        for (EventFilter filter : filters) {
            if (filter.isApplicable(filterDto)) {
                eventStream = filter.apply(eventStream, filterDto);
            }
        }

        return eventStream.map(eventMapper::toDto).collect(Collectors.toList());
    }

    public void deleteEvent(long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new DataValidationException("Event not found with ID: " + eventId);
        }
        eventRepository.deleteById(eventId);
    }

    public EventDto updateEvent(EventDto eventDto) {
        validateEventDto(eventDto);

        Event event = eventRepository.findById(eventDto.id())
                .orElseThrow(() -> new DataValidationException("Event not found with ID: " + eventDto.id()));

        if (!event.getOwner().getId().equals(eventDto.ownerId())) {
            throw new DataValidationException("Only the event owner can update the event.");
        }

        validateOwnerAndSkills(eventDto.ownerId(), eventDto.relatedSkills());

        event.setTitle(eventDto.title());
        event.setStartDate(eventDto.startDate());
        event.setEndDate(eventDto.endDate());
        event.setDescription(eventDto.description());
        event.setLocation(eventDto.location());
        event.setMaxAttendees(eventDto.maxAttendees());
        event.setRelatedSkills(eventDto.relatedSkills().stream()
                .map(skillId -> {
                    Skill skill = new Skill();
                    skill.setId(skillId);
                    return skill;
                })
                .collect(Collectors.toList()));

        Event updatedEvent = eventRepository.save(event);

        return eventMapper.toDto(updatedEvent);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        List<Event> ownedEvents = eventRepository.findAllByUserId(userId);
        return ownedEvents.stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        List<Event> participatedEvents = eventRepository.findParticipatedEventsByUserId(userId);
        return participatedEvents.stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }

    public void eventPromotion(EventPromotionRequest request) {
        validateEvent(request.eventId());
        userValidator.validateUser(request.userId());

        String message = converterUtil.convertToJson(request);
        kafkaTemplate.send(PAYMENT_PROMOTION_TOPIC, EVENT_KEY, message);
    }

    public List<EventResponse> getPromotionEvents() {
        List<Long> eventIds = promotionServiceClient.getPromotionEvents();
        return eventIds.stream()
                .map(event -> {
                    validateEvent(event);
                    return eventMapper.toEventResponse(eventRepository.findById(event).get());
                })
                .toList();
    }

    public void removeAllPastEvents() {
        Page<Event> currentPage;
        int currentPageNumber = 1;
        do {
            currentPage = eventRepository.findAllByStatusIs(
                    EventStatus.COMPLETED,
                    PageRequest.of(currentPageNumber, appConfig.getMaxDataGroupSize()));

            Page<Event> finalCurrentPage = currentPage;
            appConfig.getThreadPool().submit(() -> {
                finalCurrentPage.forEach(event -> deleteEvent(event.getId()));
            });
            currentPageNumber++;
        } while (currentPage.hasNext());
    }

    private void validateEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Event with id " + eventId + " does not exist");
        }
    }

    private void validateEventDto(EventDto eventDto) {
        if (eventDto.title() == null || eventDto.title().isEmpty()) {
            throw new DataValidationException("Event title is required.");
        }
        if (eventDto.startDate() == null || eventDto.endDate() == null) {
            throw new DataValidationException("Event start and end dates are required.");
        }
        if (eventDto.startDate().isAfter(eventDto.endDate())) {
            throw new DataValidationException("Event start date must be before the end date.");
        }
        if (eventDto.relatedSkills() == null || eventDto.relatedSkills().isEmpty()) {
            throw new DataValidationException("Event must have at least one related skill.");
        }
    }

    private User validateOwnerAndSkills(Long ownerId, List<Long> relatedSkills) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new DataValidationException("Owner not found with ID: " + ownerId));
        Set<Long> ownerSkillIds = owner.getSkills().stream()
                .map(Skill::getId)
                .collect(Collectors.toSet());

        if (!ownerSkillIds.containsAll(relatedSkills)) {
            throw new DataValidationException("Owner does not possess all required skills for the event.");
        }

        return owner;
    }
}