package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.ResourceNotFoundException;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor

public class EventService {

    private final EventRepository eventRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final UserMapper userMapper;
    private final SkillMapper skillMapper;
    private final List<EventFilter> eventFilters;

    public EventDto create(EventDto event) {
        if (!event.getRelatedSkills().stream()
                .anyMatch(skill -> skill.getUserIds().contains(event.getOwnerId()))) {
            throw new ResourceNotFoundException("Ошибка: пользователь не может создать событие с такими навыками");
        } else {
            Event eventEntity = eventMapper.toEntity(event);
            return eventMapper.toDto(eventRepository.save(eventEntity));
        }
    }

    public EventDto getEventById(long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Ошибка: такого события не существует");
        } else {
            return eventMapper.toDto(eventRepository.getById(eventId));
        }
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filters) {
        Stream<Event> events = eventRepository.findAll().stream();
        eventFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(events, filters));
        return eventMapper.toDto(events.toList());
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
        /* eventEntity.setRelatedSkills(new ArrayList<>());
                for (SkillDto skillDto : event.getRelatedSkills()) {
                        Skill skill = skillMapper.toEntity(skillDto);
                        Skill updatedSkill = skillRepository.findById(skill.getId())
                                .orElseThrow(() -> new ResourceNotFoundException("Ошибка: скилл не найден"));
                        eventEntity.getRelatedSkills().add(updatedSkill);
                }*/
        return eventMapper.toDto(eventRepository.save(eventEntity));
    }

    public List<EventDto> getOwnedEvents(Long userId) {
        return eventMapper.toDto(eventRepository.findAllByUserId(userId));
    }

    public List<EventDto> getParticipatedEvents(Long userId) {
        return eventMapper.toDto(eventRepository.findParticipatedEventsByUserId(userId));
    }
}

/*public List<SkillDto> getSkills(SkillFilterDto filters) ‹
List<Skill> skills = skillRepository.findAll():
        return skills.stream()Stream<Skill>
.filter(skill -> filters getTitlePattern() == null || skill.getTitle().contains(filters-getTitlePattern()))
        .filter(skill -> filters getUserId() == null || skill.getUsers().stream().anyMatch(user -> user.getId() == filters getUserId()))
        .filter(filter(skill -
        filters-getOwned() == null || filters.getOwned() = skill.getUsers(),stream() .anyMatch(user > user-getI]() = filters.getUserIdO))))
.map (skillMapper:: toDto) Stream<SkillDto>
toList()*/