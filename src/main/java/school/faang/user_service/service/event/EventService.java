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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
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

    //готово, написать тесты
    public EventDto getEvent(long eventId) {
        try {
         Optional<Event> event = eventRepository.findById(eventId);
         return eventMapper.toDto(event.get());
        } catch (Exception e) {
            throw new IllegalArgumentException("Событие не найдено!");
        }
    }

    //доделать
    public List<EventDto> getEventsByFilter(EventFilterDto filters) {
        Stream<Event> events = eventRepository.findAll().stream();
        eventFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(events, filters));
        return Collections.singletonList(eventMapper.toDto((Event) events.toList()));
    }

    //готово, написать тесты
    public void deleteEvent(long eventId) {
        eventRepository.deleteById(eventId);
    }

    //готово, написать тесты
    public void updateEvent(EventDto eventDto) {
       skillCheck(eventDto);
       eventRepository.save(eventMapper.toEntity(eventDto));
    }

    //готово, проверить
    public List<EventDto> getOwnedEvents(long userId) {
        List<Event> eventList = eventRepository.findAllByUserId(userId);
        return eventList.stream().map(eventMapper::toDto).toList();
    }

    //проверить, похож на предыдущий, надо исправить что-то
    public List<EventDto> getParticipatedEvents(long userId) {
        List<Event> events = eventRepository.findParticipatedEventsByUserId(userId);
        return events.stream().map(eventMapper::toDto).toList();
    }

    //можно написать тест и проверить эту махину
    private EventDto skillCheck(EventDto eventDto) {
        Optional<User> userOptional = userRepository.findById(eventDto.getOwnerId());
        User user = userOptional.get();
        Stream<Long> userSkills = user.getSkills().stream()
                .map(Skill::getId);
        Stream<Long> eventSkills = eventDto.getRelatedSkills().stream()
                .map(SkillDto::getId);

        if (userSkills.equals(eventSkills)) {
            return eventDto;
        } else {
            throw new IllegalArgumentException("У вас недостаточно навыков для создания события");
        }
    }
}
