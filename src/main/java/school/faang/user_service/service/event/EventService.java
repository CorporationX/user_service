package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.event.DataValidationException;
import school.faang.user_service.mapper.EventFilterMapper;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final EventFilterMapper eventFilterMapper;

    // Создать событие
    public EventDto create(EventDto eventDto) {
        return saveEvent(eventDto);
    }

    // получить событие
    public EventDto getEvent(long eventId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            throw new DataValidationException("Такого события не существует!");
        }
        return eventMapper.eventToDto(optionalEvent.get());
    }

    //Получить все события с фильтрами
    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        return eventRepository.findAll()
                .stream()
                .filter(event -> filter.equals(eventFilterMapper.eventToEventFilterDto(event)))
                .map(eventMapper::eventToDto)
                .toList();
    }

    // Удалить событие
    public void deleteEvent(long eventId) {
        eventRepository.deleteById(eventId);
    }

    // Обновить событие

    public EventDto updateEvent(EventDto event) {
        User owner = userRepository.findById(event.getOwnerId())
                .orElseThrow(() -> new DataValidationException("Такой пользователь не найден!"));

        List<Long> skillsIds = owner.getSkills()
                .stream()
                .map(Skill::getId)
                .toList();

        event.getRelatedSkillsIds().forEach(skillId -> {
            if (!skillsIds.contains(skillId)) {
                throw new DataValidationException("Пользователь не соответствует критериям!");
            }
        });
        return eventMapper.eventToDto(eventRepository.save(eventMapper.eventDtoToEntity(event)));
    }

    // Получить все созданные пользователем события
    public List<EventDto> getOwnedEvents(long userId) {
        if ((userRepository.findById(userId)).isEmpty()) {
            throw new DataValidationException("Такого пользователя не существует!");
        }
        return eventMapper.listEventsToDto(eventRepository.findAllByUserId(userId));
    }

    // Получить все события, в которых пользователь принимает участие
    public List<EventDto> getParticipatedEvents(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new DataValidationException("Такого пользователя не существует!");
        }
        return eventMapper.listEventsToDto(eventRepository
                .findParticipatedEventsByUserId(userId));
    }

    private EventDto saveEvent(EventDto newEventDto) {
        Long ownerId = newEventDto.getOwnerId();
        Event event;
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() ->
                        new DataValidationException("Такого пользователя не существует!"));
        if (validateUserSkills(newEventDto, owner)) {
            event = eventRepository.save(eventMapper.eventDtoToEntity(newEventDto));
        } else {
            throw new DataValidationException("У пользователя нет необходимых навыков," +
                    " чтобы создать данное событие!");
        }
        return eventMapper.eventToDto(event);
    }

    public boolean validateUserSkills(EventDto eventDto, User user) {
        return eventDto.getRelatedSkillsIds().equals(user.getSkills()
                .stream()
                .map(Skill::getId)
                .toList());
    }
}
