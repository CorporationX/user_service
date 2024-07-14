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

import java.time.LocalDateTime;
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

    // Создать событие
    public EventDto create(EventDto eventDto) {
        // Владелец события + проверка, что такой пользователь существует в базе
        User owner = findByIdUser(eventDto.getOwnerId());

        List<Skill> skills = skillRepository.findAllById(eventDto.getRelatedSkillIds());

        // Проверка, что у пользователя имеются скилы для создания события
        eventValidator.checkExistenceSkill(owner, skills);

        // Заполнение недостающих полей entity
        Event eventEntity = eventMapper.toEntity(eventDto);
        eventEntity.setRelatedSkills(skills);
        eventEntity.setOwner(owner);
        eventEntity.setCreatedAt(LocalDateTime.now());
        return eventMapper.toDto(eventRepository.save(eventEntity));
    }

    // Обновить событие
    public EventDto updateEvent(EventDto eventDto) {
        Event event = eventRepository.findById(eventDto.getId())
                .orElseThrow(() -> new DataValidationException("События с таким id не существует."));
        List<Long> skillIds = event.getRelatedSkills().stream()
                .map(Skill::getId).toList();
        // Если умения события были изменены, то получаем новые умения для entity
        if (!skillIds.containsAll(eventDto.getRelatedSkillIds())) {
            User owner = findByIdUser(eventDto.getOwnerId());
            List<Skill> skills = skillRepository.findAllById(eventDto.getRelatedSkillIds());
            // Проверка, что у пользователя имеются скилы
            eventValidator.checkExistenceSkill(owner, skills);
            event.setRelatedSkills(skills);
        }

        Event eventEntity = eventMapper.toEntity(eventDto);
        eventEntity.setRelatedSkills(event.getRelatedSkills());
        // Владельца сменить нельзя. Сообщений об этом не вывожу.
        eventEntity.setOwner(event.getOwner());
        eventEntity.setUpdatedAt(LocalDateTime.now());
        return eventMapper.toDto(eventRepository.save(eventEntity));
    }

    // Получить событие
    public EventDto getEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .map(eventMapper::toDto)
                .orElseThrow(() -> new DataValidationException("События с таким id не существует."));
    }

    // Фильтрация событий по шаблону
    public List<EventDto> getEventsByFilter(EventFilterDto filters) {
        List<Event> events = eventRepository.findAll();

        // Список подключенных фильтров
        List<EventFilter> actualFilters = eventFilters.stream()
                .filter(f -> f.isApplicable(filters))
                .toList();

        return events.stream()
                .filter(e -> actualFilters.stream()
                        .allMatch(f -> f.test(e, filters)))
                .map(eventMapper::toDto)
                .toList();
    }

    // Удаление события
    public void deleteEvent(Long eventId) {
        eventRepository.deleteById(eventId);
    }

    // Получение событий, что создал пользователь.
    public List<EventDto> getOwnedEvents(long userId) {
        User user = findByIdUser(userId);

        return user.getOwnedEvents().stream()
                .map(eventMapper::toDto)
                .toList();
    }

    // Получение событий, где пользователь принимает участие
    public List<EventDto> getParticipatedEvents(long userId) {
        User user = findByIdUser(userId);
        List<Event> participatedEvents = eventRepository.findParticipatedEventsByUserId(user.getId());

        return participatedEvents.stream()
                .map(eventMapper::toDto)
                .toList();
    }

    // Получение владельца и проверка, что такой пользователь существует
    private User findByIdUser(long ownerId) {
        return userRepository.findById(ownerId)
                .orElseThrow(() -> new DataValidationException("Пользователя с таким id не существует."));
    }
}
