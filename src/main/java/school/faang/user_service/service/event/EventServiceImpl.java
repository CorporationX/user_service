package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.EventFilters;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.event.exceptions.InsufficientSkillsException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.filters.EventFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final List<EventFilter> eventFilters;

    @Override
    public Event create(Event event) {
        log.info("Создание события с title: {}", event.getTitle());

        User user = loadUserById(event.getOwner().getId());
        List<Skill> relatedSkills = event.getRelatedSkills();

        validateUserSkills(user, relatedSkills);

        event.setOwner(user);
        event.setRelatedSkills(relatedSkills);

        return eventRepository.save(event);
    }

    @Override
    @Transactional(readOnly = true)
    public Event getEvent(Long eventId) {
        log.info("Получение события с ID: {}", eventId);
        return findById(eventId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getEventsByFilter(EventFilters eventFilters) {
        log.info("Фильтрация событий по критериям: {}", eventFilters);

        List<Event> filteredEvents = this.eventFilters.stream()
                .filter(filter -> filter.isApplicable(eventFilters))
                .reduce(eventRepository.findAll().stream(),
                        (stream, filter) -> filter.apply(stream, eventFilters),
                        (s1, s2) -> s1)
                .toList();

        log.info("Найдено {} событий по заданным критериям", filteredEvents.size());

        return filteredEvents;
    }

    @Override
    public Event updateEvent(Event event) {
        log.info("Обновление события с ID: {}", event.getId());

        Event existingEvent = findById(event.getId());

        User user = loadUserById(event.getOwner().getId());
        List<Skill> relatedSkills = event.getRelatedSkills();

        validateUserSkills(user, relatedSkills);

        eventMapper.updateEventFromDto(existingEvent, event);

        return eventRepository.save(existingEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getSubscribersCount(Event event) {
        log.info("Получение количества подписчиков у пользователя с ID: {}", event.getOwner().getId());
        return event.getOwner().getFollowers().size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getOwnedEvents(Long userId) {
        log.info("Получение событий, созданных пользователем с ID: {}", userId);
        return eventRepository.findAllByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getParticipatedEvents(Long userId) {
        log.info("Получение событий, в которых участвовал пользователь с ID: {}", userId);
        return eventRepository.findParticipatedEventsByUserId(userId);
    }

    @Override
    public void deleteEvent(Long eventId) {
        log.info("Удаление события с ID: {}", eventId);
        try {
            eventRepository.deleteById(eventId);
            log.info("Событие с ID {} успешно удалено", eventId);
        } catch (EmptyResultDataAccessException e) {
            log.error("Ошибка удаления. Событие с ID {} не найдено", eventId);
            throw new EntityNotFoundException("Событие с ID " + eventId + " не найдено.");
        }
    }

    private void validateUserSkills(User user, List<Skill> relatedSkills) {
        log.info("Проверка навыков пользователя с ID: {}", user.getId());
        List<Skill> userSkills = Optional.ofNullable(user.getSkills()).orElse(new ArrayList<>());
        if (!userSkills.containsAll(relatedSkills)) {
            log.error("У пользователя с ID {} недостаточно навыков для события", user.getId());
            throw new InsufficientSkillsException(
                    "Пользователь не обладает необходимыми навыками для проведения этого события.");
        }
    }

    private User loadUserById(Long id) {
        log.info("Загрузка пользователя с ID: {}", id);
        return userRepository.findById(id).orElseThrow(() -> {
            log.error("Пользователь с ID {} не найден", id);
            return new EntityNotFoundException("Пользователь с ID " + id + " не найден.");
        });
    }

    private Event findById(Long id) {
        log.info("Поиск события с ID: {}", id);
        return eventRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Событие с ID {} не найдено", id);
                    return new EntityNotFoundException("Событие с ID " + id + " не найдено.");
                });
    }
}