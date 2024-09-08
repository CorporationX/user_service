package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final List<EventFilter> eventFilters;
    private final SkillRepository skillRepository;

    //готово, написать тесты
    public EventDto create(EventDto eventDto) {
        validateForCreate(eventDto);
        skillCheck(eventDto);
        Event event = eventMapper.toEntity(eventDto);
        eventRepository.save(event);
        return eventMapper.toDto(event);
    }

    //готово, написать тесты
    public EventDto getEvent(long eventId) {
         List<Event> events = eventRepository.findAllByUserId(eventId);
         return eventMapper.toDto((Event) events);
    }

    //доделать
    public EventDto getEventsByFilter(EventFilterDto filters) {
        Stream<Event> events = eventRepository.findAll().stream();
        eventFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(events, filters));
        List<Event> eventList = events.toList();
        return eventMapper.toDto((Event) eventList);
    }

    //готово, написать тесты
    public void deleteEvent(long eventId) {
        eventRepository.deleteById(eventId);
    }

    //готово, написать тесты
    public void updateEvent(EventDto eventDto) {
        validateForUpdate(eventDto);
        skillCheck(eventDto);
        eventRepository.save(eventMapper.toEntity(eventDto));
    }

    //готово, проверить
    public List<EventDto> getOwnedEvents(long userId) {
        List<Event> eventList = eventRepository.findAllByUserId(userId);
        return eventMapper.toDtoList(eventList);
    }

    //проверить, похож на предыдущий, надо исправить что-то
    public List<EventDto> getParticipatedEvents(long userId) {
        List<Event> events = eventRepository.findParticipatedEventsByUserId(userId);
        return events.stream().map(eventMapper::toDto).toList();
    }

    //вернуть старт дату!!!!!!!!!!!!!!!!
    private void validateForCreate(EventDto event) {
        if (event.getTitle() == null || event.getOwnerId() == null) {
            throw new DataValidationException("Обязательный поля пустые");
        }
    }

    //можно написать тест и проверить эту махину
    private void skillCheck(EventDto eventDto) {
        List<Long> userSkills = skillRepository.findSkillsOfferedToUser(eventDto.getOwnerId()).stream()
                .map(Skill::getId)
                .toList();
        List<Long> eventSkills = eventDto.getRelatedSkills().stream()
                .map(SkillDto::getId)
                .toList();
        if (userSkills.size() != eventSkills.size()) {
            throw new DataValidationException("Недостаточно навыков для создания ивента");
        }
    }


    private void validateForUpdate(EventDto event) {
        if (event.getTitle() == null || event.getTitle().isBlank() || event.getOwnerId() == null || event.getStartDate() == null) {
            throw new DataValidationException("Обязательный поля пустые");
        }
    }
}
