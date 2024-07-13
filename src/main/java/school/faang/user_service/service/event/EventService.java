package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ResourceNotFoundException;
import school.faang.user_service.filter.EventByOwnerFilter;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.filter.EventTitleFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor

public class EventService {

    private final EventRepository eventRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final SkillMapper skillMapper;
    private final List<EventFilter> eventFilters = List.of(new EventByOwnerFilter(), new EventTitleFilter());

    public User ownerValidation(EventDto eventDto) {
        return userRepository.findById(eventDto.getOwnerId())
                .orElseThrow(() -> new DataValidationException("Ошибка: пользователь не найден"));
    }

    public List<Skill> skillValidation(EventDto eventDto) {
        List<SkillDto> listSkillDto = eventDto.getRelatedSkills();
        List<Skill> eventSkillList = skillMapper.toEntity(listSkillDto);
        return eventSkillList.stream()
                .peek(skill -> {
                    if (!skillRepository.findById(skill.getId()).isPresent()) {
                        throw new DataValidationException("Ошибка: навык с ID " + skill.getId() + " не найден");
                    }
                })
                .collect(Collectors.toList());
    }

    public void inputDataValidation(EventDto eventDto) {
        User validatedOwner = ownerValidation(eventDto);
        List<Skill> validatedSkillList = skillValidation(eventDto);
        if (!validatedSkillList.stream()
                .allMatch(skill -> skill.getUsers().contains(validatedOwner))) {
            throw new DataValidationException("Ошибка: пользователь не обладает всеми необходимыми навыками");
        }
    }

    public EventDto create(EventDto eventDto) {
        inputDataValidation(eventDto);
        Event eventEntity = eventMapper.toEntity(eventDto);
        return eventMapper.toDto(eventRepository.save(eventEntity));
    }

    public EventDto getEventById(long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Ошибка: такого события не существует");
        } else {
            Event event = eventRepository.getById(eventId);
            return eventMapper.toDto(event);
        }
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filters) {
        Stream<Event> events = eventRepository.findAll().stream();
        for (EventFilter filter : eventFilters) {
            if (filter.isApplicable(filters)) {
                events = filter.apply(events, filters);
            }
        }
        return eventMapper.toDto(events.collect(Collectors.toList()));
    }

    public void deleteEvent(Long eventId) {
        eventRepository.deleteById(eventId);
    }

    public EventDto updateEvent(Long eventId, EventDto event) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Ошибка: такого события не существует");
        }
        if (!event.getRelatedSkills().stream()
                .anyMatch(skill -> skill.getUserIds().contains(event.getOwnerId()))) {
            throw new ResourceNotFoundException("Ошибка: пользователь не может провести событие с такими навыками");
        }
        Event eventEntity = eventMapper.toEntity(event);
        eventEntity.setId(event.getId());
        eventEntity.setTitle(event.getTitle());
        eventEntity.setStartDate(event.getStartDate());
        eventEntity.setEndDate(event.getEndDate());
        eventEntity.setOwner(userRepository.findById(event.getOwnerId()).orElseThrow(() -> new ResourceNotFoundException("Ошибка: такого пользователя не существует")));
        eventEntity.setDescription(event.getDescription());
        eventEntity.setRelatedSkills(skillMapper.toEntity(event.getRelatedSkills()));
        eventEntity.setLocation(event.getLocation());
        eventEntity.setMaxAttendees(event.getMaxAttendees());
        return eventMapper.toDto(eventRepository.save(eventEntity));
    }

    public List<EventDto> getOwnedEvents(Long userId) {
        return eventMapper.toDto(eventRepository.findAllByUserId(userId));
    }

    public List<EventDto> getParticipatedEvents(Long userId) {
        return eventMapper.toDto(eventRepository.findParticipatedEventsByUserId(userId));
    }
}
