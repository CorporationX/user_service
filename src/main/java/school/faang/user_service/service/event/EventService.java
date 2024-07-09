package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exeption.event.DataValidationException;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.filter.EventFilterDto;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.event.EventValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final EventValidator eventValidator;
    private final List<EventFilter> eventFilters;

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

    public List<EventDto> getEventsByFilter(EventFilterDto filters) {
        List<Event> events = eventRepository.findAll();

        List<EventFilter> actualFilters = eventFilters.stream()
                .filter(f -> f.isApplicable(filters))
                .toList();

        return events.stream()
                .filter(e -> actualFilters.stream()
                        .allMatch(f -> f.test(e, filters)))
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

        return participatedEvents.stream()
                .map(eventMapper::toDto)
                .toList();
    }
}
