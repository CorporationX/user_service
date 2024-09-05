package school.faang.user_service.service.event;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.SkillDto;
import school.faang.user_service.dto.event.UserDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserMapper userMapper;
    private final List<EventFilter> eventFilters;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;


    //готово, написать тесты
    public EventDto create(EventDto eventDto) {
        skillCheck(eventDto);
       Event event = eventMapper.toEntity(eventDto);
       eventRepository.save(event);
       return eventMapper.toDto(event);
    }

    //проверить на опшинал
    public EventDto getEvent(long eventId) {
        try {
         Optional<Event> event = eventRepository.findById(eventId);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        return null;
    }

    //доделать
    public List<EventDto> getEventsByFilter(EventFilterDto filters) {
        Stream<Event> events = eventRepository.findAll().stream();
        eventFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(events, filters));
        return Collections.singletonList(eventMapper.toDto((Event) events.toList()));
    }

    //готово, но проверить
    public void deleteEvent(long eventId) {
        eventRepository.deleteById(eventId);
    }

    //доделать
    public void updateEvent(EventDto eventDto) {
        Event event = new Event();
        event.setId(eventDto.getId());
        eventRepository.save(event);
    }

    //проверить
    public List<Event> getOwnedEvents(long userId) {
        List<Event> eventList = eventRepository.findAllByUserId(userId);
        return eventList;
    }

    //проверить, похож на предыдущий, надо исправить что-то
    public List<Event> getParticipatedEvents(long userId) {
        List<Event> events = eventRepository.findAllByUserId(userId);
        return events;
    }


    private EventDto validate(EventDto event) {
        try {
            if (event.getRelatedSkills().isEmpty()) {
                return event;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Вы не можете провести событие с такими навыками");
        }
        return null;
    }

    private EventDto skillCheck(EventDto eventDto) {
        Optional<User> userOptional = userRepository.findById(eventDto.getOwnerId());
        UserDto userDto = userMapper.toDto(userOptional.get());
        Stream<Long> userSkills = userDto.getSkills().stream()
                .map(SkillDto::getId);
        Stream<Long> eventSkills = eventDto.getRelatedSkills().stream()
                .map(SkillDto::getId);

        if (userSkills.equals(eventSkills)) {
            return eventDto;
        } else {
            throw new IllegalArgumentException("У вас недостаточно навыков для создания события");
        }
    }
}
