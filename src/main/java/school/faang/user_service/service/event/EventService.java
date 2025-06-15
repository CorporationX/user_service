package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;

    public EventDto create(EventDto eventDto) {
        if (eventDto.getOwnerId() == null) {
            throw new DataValidationException("ID пользователя обязателен");
        }

        User user = userRepository.findById(eventDto.getOwnerId())
                .orElseThrow(() -> new DataValidationException("Пользователь не найден"));

        Set<Long> userSkillIds = user.getSkills().stream()
                .map(Skill::getId)
                .collect(Collectors.toSet());

        boolean hasAllRequiredSkills = userSkillIds.containsAll(eventDto.getRelatedSkills());

        if (!hasAllRequiredSkills) {
            throw new DataValidationException("Пользователь не обладает всеми необходимыми " +
                    "навыками для проведения события");
        }

        Event event = eventMapper.toEntity(eventDto);
        Event savedEvent = eventRepository.save(event);

        return eventMapper.toDto(savedEvent);
    }

    public EventDto getEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException("Событие с ID " + eventId + " не найдено"));
        return eventMapper.toDto(event);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        return eventRepository.findAll().stream()
                .filter(event -> filter.getTitle() == null || event.getTitle().toLowerCase().contains(filter.getTitle().toLowerCase()))
                .filter(event -> filter.getStartDateFrom() == null || !event.getStartDate().isBefore(filter.getStartDateFrom()))
                .filter(event -> filter.getStartDateTo() == null || !event.getStartDate().isAfter(filter.getStartDateTo()))
                .filter(event -> filter.getEventType() == null || event.getType() == filter.getEventType())
                .filter(event -> filter.getEventStatus() == null || event.getStatus() == filter.getEventStatus())
                .filter(event -> filter.getOwnerId() == null || event.getOwner().equals(filter.getOwnerId()))
                .filter(event -> filter.getRelatedSkills() == null || event.getRelatedSkills().containsAll(filter.getRelatedSkills()))
                .filter(event -> filter.getLocation() == null || event.getLocation().equalsIgnoreCase(filter.getLocation()))
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }

    public void deleteEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new DataValidationException("Событие с id " + eventId + " не найдено.");
        }
        eventRepository.deleteById(eventId);
    }

    public EventDto updateEvent(EventDto dto) {
        Event existing = eventRepository.findById(dto.getId())
                .orElseThrow(() -> new DataValidationException("Событие с id " + dto.getId() + " не найдено."));

        if (!existing.getOwner().getId().equals(dto.getOwnerId())) {
            throw new DataValidationException("Пользователь не является автором события.");
        }

        User owner = userRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> new DataValidationException("Пользователь с id " + dto.getOwnerId() + " не найден."));

        List<Long> ownerSkills = owner.getSkills().stream()
                .map(skill -> skill.getId())
                .toList();

        for (Long skillId : dto.getRelatedSkills()) {
            if (!ownerSkills.contains(skillId)) {
                throw new DataValidationException("Пользователь не обладает навыком с id: " + skillId);
            }
        }

        Event updated = eventMapper.toEntity(dto);
        updated.setId(existing.getId());
        Event saved = eventRepository.save(updated);
        return eventMapper.toDto(saved);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        return eventRepository.findAllByUserId(userId).stream()
                .map(eventMapper::toDto)
                .toList();
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId).stream()
                .map(eventMapper::toDto)
                .toList();
    }
}
