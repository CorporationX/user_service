package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Component("eventService")
@RequiredArgsConstructor
public class EventService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public EventDto create(EventDto event) {
        User user = userRepository.findById(event.getOwnerId()).orElseThrow();
        boolean userContainsSkill = isUserContainsSkill(event, user);
        if (!userContainsSkill) {
            throw new DataValidationException("Пользователь не может провести событие с такими навыками");
        }
        return eventMapper.toEventDto(eventRepository.save(eventMapper.toEvent(event)));
    }

    public EventDto getEvent(long eventId) {
        return eventMapper.toEventDto(eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException("Ошибка")));
    }

    public EventDto updateEvent(EventDto event) {
        Event event1 = eventRepository.findById(event.getId())
                .orElseThrow(() -> new DataValidationException("Ошибка в развитии"));
        eventMapper.update(event1, event);
        return create(eventMapper.toEventDto(event1));
    }

    public void deleteEvent(long eventId) {
        if (eventId <= 0) {
            throw new DataValidationException("Ошибка");
        } else {
            eventRepository.deleteById(eventId);
        }
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        List<EventDto> events = new ArrayList<>();
        eventRepository.findAll().forEach(event -> {
            events.add(eventMapper.toEventDto(event));
        });
        return events.stream()
                .filter(event -> {
                    if (event.getStartDate() == null) {
                        event.setStartDate(LocalDateTime.MIN);
                    }
                    if (event.getEndDate() == null) {
                        event.setEndDate(LocalDateTime.MAX);
                    }
                    return event.getStartDate().isAfter(filter.getStartDate())
                            && event.getStartDate().isBefore(filter.getEndDate());
                })
                .filter(event -> {
                    if (event.getOwnerId() == null) {
                        return false;
                    } else {
                        return event.getOwnerId().equals(filter.getOwnerId());
                    }
                })
                .filter(event -> {
                    if (event.getRelatedSkills() == null) {
                        return false;
                    } else {
                        return event.getRelatedSkills().containsAll(filter.getRelatedSkills());
                    }
                })
                .filter(event -> {
                    if (event.getLocation() == null) {
                        return false;
                    } else {
                        return event.getLocation().equals(filter.getLocation());
                    }
                })
                .filter(event -> {
                    if (event.getTitle() == null) {
                        return false;
                    } else {
                        return event.getTitle().equals(filter.getTitle());
                    }
                })
                .filter(event -> {
                    if (event.getDescription() == null) {
                        return false;
                    } else {
                        return event.getDescription().equals(filter.getDescription());
                    }
                })
                .filter(event -> {
                    if (event.getMaxAttendees() == 0) {
                        return false;
                    } else {
                        return event.getMaxAttendees() == (filter.getMaxAttendees());
                    }
                })
                .filter(event -> {
                    if (event.getId() == null) {
                        return false;
                    } else {
                        return event.getId().equals(filter.getId());
                    }
                })
                .toList();
    }

    private boolean isUserContainsSkill(EventDto event, User user) {
        return new HashSet<>(user.getSkills()
                .stream()
                .map(Skill::getTitle)
                .toList())
                .containsAll(event.getRelatedSkills()
                        .stream()
                        .map(SkillDto::getTitle).toList());
    }
}
