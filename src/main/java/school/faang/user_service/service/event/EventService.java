package school.faang.user_service.service.event;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final List<EventFilter> eventFilters;
    private final SkillRepository skillRepository;

    @Transactional
    public EventDto create(EventDto eventDto) {
        skillCheck(eventDto);
        Event event = eventMapper.toEntity(eventDto);
        event.setOwner(userRepository.findById(eventDto.getOwnerId()).orElseThrow(() -> new DataValidationException("ОШЫБКА"))); // в отдельный класс
        event.setRelatedSkills(skillRepository.findAllById(eventDto.getRelatedSkillsIds()));
        eventRepository.save(event);
        return eventMapper.toDto(event);
    }

    @Transactional
    public EventDto getEvent(long eventId) {
         List<Event> events = eventRepository.findAllByUserId(eventId);
         return eventMapper.toDto((Event) events);
    }

    public EventDto getEventsByFilter(EventFilterDto filters) {
        Stream<Event> events = eventRepository.findAll().stream();
        eventFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(events, filters));
        List<Event> eventList = events.toList();
        return eventMapper.toDto((Event) eventList);
    }

    public void deleteEvent(long eventId) {
        eventRepository.deleteById(eventId);
    }

    public void updateEvent(EventDto eventDto) {
        skillCheck(eventDto);
        Event event = eventRepository.findById(eventDto.getId()).orElseThrow(() -> new DataValidationException("Ошибка поиска ивента"));
        eventRepository.delete(event);
        Event event1 = eventMapper.toEntity(eventDto);
        event1.setOwner(userRepository.findById(eventDto.getOwnerId()).orElseThrow(() -> new DataValidationException("ОШЫБКА")));
        eventRepository.save(event1);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        List<Event> eventList = eventRepository.findAllByUserId(userId);
        return eventMapper.toDtoList(eventList);
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        List<Event> events = eventRepository.findParticipatedEventsByUserId(userId);
        return events.stream().map(eventMapper::toDto).toList();
    }

    public void skillCheck(EventDto eventDto) {
        List<Long> userSkill = skillRepository.findSkillsOfferedToUser(eventDto.getOwnerId()).stream().map(Skill::getId).toList();

        Set<Long> userSkillSet = new HashSet<>(userSkill);

        List<Long> eventSill = eventDto.getRelatedSkillsIds();

        if (eventSill == null) {
            throw new DataValidationException("Список связанных навыков не может быть пустым");
        }

        if (!userSkillSet.containsAll(eventSill)) {
            throw new DataValidationException("Пользователь не имеет всех необходимых навыков");
        } else {
            System.out.println("Все круто!");
        }
    }
}
