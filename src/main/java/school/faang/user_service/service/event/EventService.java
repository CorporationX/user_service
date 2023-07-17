package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
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
        return EventMapper.INSTANCE.toEventDto(eventRepository.save(EventMapper.INSTANCE.toEvent(event)));
    }

    private static boolean isUserContainsSkill(EventDto event, User user) {
        return user.getSkills()
                .stream()
                .map(Skill::getTitle)
                .toList()
                .containsAll(event.getRelatedSkills()
                        .stream()
                        .map(SkillDto::getTitle).toList());
    }

    public boolean validation(EventDto event) {
        return event.getTitle() != null && !event.getTitle().isEmpty() && event.getStartDate() != null && event.getOwnerId() != null;
    }

    public EventDto getEvent(long eventId) {
        return EventMapper.INSTANCE.toEventDto(eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException("Ошибка")));
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        List<EventDto> events = new ArrayList<>();
        eventRepository.findAll().forEach(event -> {
            events.add(EventMapper
                    .INSTANCE.toEventDto(event));
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

    public EventDto updateEvent(EventDto event) {
        if (eventRepository.existsById(event.getId())) {
            EventDto eventDto = event;
            eventRepository.deleteById(event.getId());
            return create(eventDto);
        }
        return create(event);
    }

    public void deleteEvent(long eventId) {
        if (eventId <= 0) {
            throw new DataValidationException("Ошибка");
        } else {
            eventRepository.deleteById(eventId);
        }
    }
//    Создайте в классе EventService метод create(EventDto event)
//    с возвращаемым значением EventDto который будет вызываться из метода create()
//    класса EventController для непосредственного выполнения
//    нужного действия, после того, как запрос пользователя отвалидирован.

//    Здесь нужно проверить, что пользователь, который создает событие, имеет навыки,
//    связанные с событием. Если у пользователя нет этих навыков,
//    нужно вывести ошибку о том, что пользователь не может провести такое событие с
//    такими навыками. Если все в порядке, то нужно вызвать метода save класса EventRepository,
//    который сохранит новое событие в базу данных

//    Создайте в классе EventService метод updateEvent(EventDto event) для обновления события.

//    Здесь нужно проверить, что пользователь, который создает событие, имеет навыки,
//    связанные с событием. Если у пользователя нет этих навыков, нужно вывести ошибку о том,
//    что пользователь не может провести такое событие с такими навыками. Если все в порядке,
//    то нужно вызвать метода save класса EventRepository, который сохранит новое событие в базу данных.
}
