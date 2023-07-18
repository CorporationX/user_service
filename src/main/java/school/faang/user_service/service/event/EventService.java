package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service("restService")
@RequiredArgsConstructor
public class EventService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public EventDto create(EventDto event) {
        User user = userRepository.findById(event.getOwnerId()).orElseThrow();
        if (!(isUserContainsSkill(event, user))) {
            throw new DataValidationException("The user cannot hold such an event with such skills");
        }
        return eventMapper.toEventDto(eventRepository.save(eventMapper.toEvent(event)));
    }

    public EventDto getEvent(long eventId) {
        return eventMapper.toEventDto(eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException("User with this id was not found")));
    }
    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        List<EventDto> events = new ArrayList<>();
        eventRepository.findAll().forEach(event -> {
            events.add(eventMapper.toEventDto(event));
        });
        return events.stream()
                .filter(event -> {
                    if (event.getId() == null) {
                        return false;
                    } else {
                        return event.getId().equals(filter.getId());
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
                    if (event.getStartDate() == null) {
                        event.setStartDate(LocalDateTime.MIN);
                    }
                    if (event.getEndDate() == null) {
                        event.setEndDate(LocalDateTime.MAX);
                    }
                    return event.getStartDate().isAfter(filter.getStartDate())
                            && event.getEndDate().isBefore(filter.getEndDate());
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
                        return new HashSet<>(event.getRelatedSkills()).containsAll(filter.getRelatedSkills());
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
