package school.faang.user_service.EventOrganization.service.event;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import school.faang.user_service.EventOrganization.mapper.event.mapper.EventMapper;
import school.faang.user_service.EventOrganization.dto.event.EventDto;
import school.faang.user_service.EventOrganization.dto.event.EventFilterDto;
import school.faang.user_service.EventOrganization.exception.DataValidationException;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final SkillRepository skillRepository;
    private static final Logger LOGGER = LogManager.getLogger();

    public EventDto create(EventDto eventDto) {

        validateEventDto(eventDto);
        validateOwnerSkills(eventDto);
        return saveEventInDB(eventDto);
    }

    public EventDto getEvent(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    LOGGER.warn("Event not found with id {}:", eventId);
                    return new NoSuchElementException("Event not found with id: " + eventId);
                });
        return eventMapper.EventToDto(event);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        return eventRepository.findAll().stream()
                .map(eventMapper::EventToEventFilterDto)
                .filter(event -> event.equals(filter))
                .map(eventMapper::EventFilterDtoToEvent)
                .map(eventMapper::EventToDto)
                .toList();
    }

    public void deleteEvent(long eventId) {
        if (!eventRepository.findById(eventId).isPresent()) {
            LOGGER.warn("The deletion event was not found with id {}:", eventId);
            throw new NoSuchElementException("Event not found with id: " + eventId);
        }

        eventRepository.deleteById(eventId);
    }

    public void updateEvent(EventDto eventDto) {

        validateEventDto(eventDto);
        validateOwnerSkills(eventDto);
        saveEventInDB(eventDto);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        return eventRepository.findAllByUserId(userId).stream()
                .map(eventMapper::EventToDto)
                .toList();
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId).stream()
                .map(eventMapper::EventToDto)
                .toList();
    }

    private boolean isBlank(String str) {
        return str == null || str.isBlank();
    }

    private void validateOwnerSkills(EventDto eventDto) {
        System.out.println("начался метод по валидации скиллов дто: " + eventDto);
        //получаем список скиллов создателя события из БД
        Long ownerId = eventDto.getOwnerId();
        List<Skill> skillsOwner = skillRepository.findAllByUserId(ownerId);

        //Мапим дто в событие
        Event event = eventMapper.EventDtoToEvent(eventDto);
        System.out.println("маппер в дто сработал" + event);
        List<Skill> skillsEvent = event.getRelatedSkills();

        //Валидируем навыки по названию

        Set<String> setTitleSkillsOwner = skillsOwner.stream()
                .map(Skill::getTitle)
                .collect(Collectors.toSet());

        Set<String> setTitleSkillsEvent = skillsEvent.stream()
                .map(Skill::getTitle)
                .collect(Collectors.toSet());

        if (!setTitleSkillsEvent.equals(setTitleSkillsOwner)) {
            throw new DataValidationException("пользователь не может провести такое" +
                    " событие с такими навыками");
        }
    }

    private EventDto saveEventInDB(EventDto eventDto) {
        Event event = eventMapper.EventDtoToEvent(eventDto);
        Event eventInDB = eventRepository.save(event);
        return eventMapper.EventToDto(eventInDB);
    }

    private void validateEventDto(EventDto eventDto) {
        try {
            Objects.requireNonNull(eventDto.getId(), "ID не должен быть null");
            Objects.requireNonNull(eventDto.getStartDate(), "StartDate не должен быть null");
            Objects.requireNonNull(eventDto.getOwnerId(), "OwnerID не должен быть null");
            Objects.requireNonNull(eventDto.getRelatedSkills(), "RelatedSkills не должен быть null");
            if (isBlank(eventDto.getTitle())) {
                throw new DataValidationException("Title не должен быть пустым или null");
            }
        } catch (NullPointerException e) {
            throw new DataValidationException("Данные запроса не валидны: " + e.getMessage());
        }
    }
}
