package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exeption.event.DataValidationException;
import school.faang.user_service.filter.EventFilterDto;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.event.EventValidator;

import java.util.List;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final EventValidator eventValidator;

    public EventDto create(EventDto eventDto) {
        List<Skill> skills = checkExistenceSkill(eventDto);

        Event eventEntity = eventMapper.toEntity(eventDto);
        eventEntity.setRelatedSkills(skills);
        return eventMapper.toDto(eventRepository.save(eventEntity));
    }

    public EventDto getEvent(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new DataValidationException("События с таким id не существует."));

        return eventMapper.toDto(event);
    }


    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        List<Event> events = eventRepository.findAll();
        List<Predicate<Event>> filters = filter.getFilters();

        return events.stream()
                .filter(e -> filters.stream()
                        .allMatch(f -> f.test(e)))
                .map(eventMapper::toDto)
                .toList();
    }

    public void deleteEvent(Long eventId) {
        eventRepository.deleteById(eventId);
    }


    public EventDto updateEvent(EventDto eventDto) {
        List<Skill> skills = checkExistenceSkill(eventDto);

        Event eventEntity = eventMapper.toEntity(eventDto);
        eventEntity.setRelatedSkills(skills);
        return eventMapper.toDto(eventRepository.save(eventEntity));
    }

    private List<Skill> checkExistenceSkill(EventDto eventDto) {
        User owner = userRepository.findById(eventDto.getOwnerId())
                .orElseThrow(() -> new DataValidationException("Пользователя с таким id не существует."));
        List<Skill> skills = skillRepository.findAllById(eventDto.getRelatedSkillIds());

        if (!eventValidator.checkExistenceSkill(owner, skills)) {
            throw new DataValidationException("У пользователя нет необходимых умений для создания события.");
        }

        return skills;
    }

    public List<EventDto> getOwnedEvents(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("Пользователя с таким id не существует."));

        return user.getOwnedEvents().stream()
                .map(eventMapper::toDto)
                .toList();
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("Пользователя с таким id не существует."));
        List<Event> participatedEvents = eventRepository.findParticipatedEventsByUserId(user.getId());
        Event event = new Event();

        return participatedEvents.stream()
                .map(eventMapper::toDto)
                .toList();
    }
}
