package school.faang.user_service.service.impl.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.filter.EventFilterDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event_filter.Filter;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final List<Filter> eventfilters;
    private final SkillMapper skillMapper;

    @Override
    public EventDto getEvent(long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new DataValidationException("Такого события не существует"));
        return eventMapper.toDto(event);
    }

    @Override
    public EventDto create(EventDto eventDto) {
        User owner = findUser(eventDto.ownerId());
        validateOwnerAndEventsSkills(owner, eventDto);
        Event entity = eventMapper.toEntity(eventDto);
        EventDto newEvent = eventMapper.toDto(eventRepository.save(entity));
        return newEvent;
    }

    @Override
    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        Stream<Event> eventList = eventRepository.findAll().stream();
        return eventfilters.stream().filter(filter1 -> filter1.isApplicable(filter)).flatMap(filter2 -> filter2.apply(eventList, filter)
                .map(eventMapper::toDto)).toList();
    }

    public void deleteEvent(long id) {
        if (eventRepository.existsById(id)) {
            eventRepository.deleteById(id);
            log.info("Событие с id {} удалено.", id);
        } else {
            throw new DataValidationException("Такого события не существует, удаление не возможно");
        }
    }

    @Override
    public EventDto updateEvent(EventDto eventDto) {
        User owner = findUser(eventDto.ownerId());
        validateOwnerAndEventsSkills(owner, eventDto);
        Event event = eventRepository.findById(eventDto.id()).orElseThrow(() -> new DataValidationException("Такого события не существует"));
        eventMapper.update(eventDto, event);
        EventDto newEvent = eventMapper.toDto(eventRepository.save(event));
        return newEvent;
    }

    @Override
    public List<EventDto> getOwnedEvents(long userId) {
        return eventRepository.findAllByUserId(userId).stream().map(eventMapper::toDto).toList();
    }

    @Override
    public List<EventDto> getParticipatedEvents(long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId).stream().map(eventMapper::toDto).toList();
    }

    @Override
    public User findUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new DataValidationException("Такого user не существует"));
    }

    private void validateOwnerAndEventsSkills(User owner, EventDto eventDto) {
        List<Long> ownerSkillsIds = owner.getSkills().stream()
                .map(Skill::getId)
                .toList();
        List<Long> eventSkillsIds = eventDto.relatedSkills().stream()
                .map(SkillDto::getId)
                .toList();
        if (!new HashSet<>(ownerSkillsIds).containsAll(eventSkillsIds)) {
            throw new DataValidationException("У пользователя не хватает навыков");
        }
    }
}