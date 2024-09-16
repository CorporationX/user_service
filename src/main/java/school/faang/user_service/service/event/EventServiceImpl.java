package school.faang.user_service.service.event;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.filter.EventFilter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final List<EventFilter> eventFilters;
    private final SkillRepository skillRepository;

    @Transactional
    public EventDto create(EventDto eventDto) {
        skillCheck(eventDto);
        Event event = eventMapper.toEntity(eventDto);
        event.setOwner(userRepository.findById(eventDto.getOwnerId()).orElseThrow(() -> new DataValidationException("Пользователь не обнаружен")));
        event.setRelatedSkills(skillRepository.findAllById(eventDto.getRelatedSkillsIds()));
        eventRepository.save(event);
        return eventMapper.toDto(event);
    }

    @Transactional(readOnly = true)
    public EventDto getEvent(long eventId) {
         return eventMapper.toDtoOp(eventRepository.findById(eventId));
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
        List<Event> events = eventRepository.findParticipatedEventsByUserId(userId);
        return events.stream().map(eventMapper::toDto).toList();
    }

    private void skillCheck(EventDto eventDto) {
        List<Long> eventSkill = eventDto.getRelatedSkillsIds();

        if (eventSkill == null) {
            throw new DataValidationException("Список связанных навыков не может быть пустым");
        }

        List<Long> userSkill = skillRepository.findSkillsOfferedToUser(eventDto.getOwnerId())
                .stream()
                .map(Skill::getId)
                .toList();

        Set<Long> userSkillSet = new HashSet<>(userSkill);

        if (!userSkillSet.containsAll(eventSkill)) {
            throw new DataValidationException("Пользователь не имеет всех необходимых навыков");
        }
    }
}
