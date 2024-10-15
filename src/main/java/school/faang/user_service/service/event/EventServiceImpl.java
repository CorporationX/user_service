package school.faang.user_service.service.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventStartEvent;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.publisher.EventStartEventPublisher;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final List<EventFilter> eventFilters;
    private final SkillRepository skillRepository;
    private final EventStartEventPublisher eventStartEventPublisher;
    private final ObjectMapper objectMapper;

    @Transactional
    public EventDto create(EventDto eventDto) {
//        skillCheck(eventDto);
        Event event = eventMapper.toEntity(eventDto);
        event.setOwner(userRepository.findById(eventDto.getOwnerId()).orElseThrow(() -> new DataValidationException("Пользователь не обнаружен")));
        event.setRelatedSkills(skillRepository.findAllById(eventDto.getRelatedSkillsIds()));
        eventRepository.save(event);

        EventStartEvent eventStartEvent = new EventStartEvent();
        eventStartEvent.setTitle(event.getTitle());
        eventStartEvent.setStartDate(event.getStartDate());
        eventStartEvent.setOwnerId(eventDto.getOwnerId());
        eventStartEvent.setSubscriberId(eventDto.getOwnerId());

        try {
            String json = objectMapper.writeValueAsString(eventStartEvent);
            eventStartEventPublisher.publish(json);
            log.info("Сообщение {} успешно отправлено", json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return eventMapper.toDto(event);
    }

    @Transactional(readOnly = true)
    public EventDto getEvent(long eventId) {
        return eventMapper.toDto(eventRepository.findById(eventId).orElseThrow(() -> new DataValidationException("Ивента нет в базе")));
    }

    @Transactional(readOnly = true)
    public List<EventDto> getEventsByFilter(EventFilterDto filters) {
        Stream<Event> eventList = eventRepository.findAll().stream();
        return eventMapper.toDtoList(eventFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(eventList,
                        (stream, filter) -> filter.apply(stream, filters),
                        (s1, s2) -> s1)
                .toList());
    }

    @Transactional
    public void deleteEvent(long eventId) {
        eventRepository.deleteById(eventId);
    }

    @Transactional
    public void updateEvent(EventDto eventDto) {
        skillCheck(eventDto);
        eventRepository.save(eventMapper.toEntity(eventDto));
    }

    @Transactional(readOnly = true)
    public List<EventDto> getOwnedEvents(long userId) {
        List<Event> eventList = eventRepository.findAllByUserId(userId);
        return eventMapper.toDtoList(eventList);
    }

    @Transactional(readOnly = true)
    public List<EventDto> getParticipatedEvents(long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId)
                .stream()
                .map(eventMapper::toDto)
                .toList();
    }

    @Async("taskExecutor")
    public CompletableFuture<Void> clearOutdatedEvents(List<Event> events) {
        List<Long> ids = events.stream()
                .map(Event::getId)
                .toList();

        eventRepository.deleteAllById(ids);
        return CompletableFuture.completedFuture(null);
    }

    private void skillCheck(EventDto eventDto) {
        List<Long> eventSkills = eventDto.getRelatedSkillsIds();

        if (eventSkills == null) {
            throw new DataValidationException("Список связанных навыков не может быть пустым");
        }

        Set<Long> userSkills = skillRepository.findSkillsOfferedToUser(eventDto.getOwnerId())
                .stream()
                .map(Skill::getId)
                .collect(Collectors.toSet());

        if (!userSkills.containsAll(eventSkills)) {
            throw new DataValidationException("Пользователь не имеет всех необходимых навыков");
        }
    }

}


