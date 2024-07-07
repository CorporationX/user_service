package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exeption.event.DataValidationException;
import school.faang.user_service.filter.EventFilterDto;
import school.faang.user_service.mapper.EventMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.event.EventValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @InjectMocks
    private EventService eventService;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private EventMapperImpl eventMapper;
    @Mock
    private EventValidator eventValidator;
    @Captor
    private ArgumentCaptor<Event> captorEvent;

    @Test
    public void testCreateWithNotExistUserId() {
        EventDto eventDto = createEventDto();
        User user = createUser(eventDto);
        // id пользователя в базе не нашли
        checkOwnerId(eventDto, user, false);

        // ожидаем исключение
        assertThrows(DataValidationException.class, () -> eventService.create(eventDto), "Пользователя с таким id не должно быть в БД");
    }

    private EventDto createEventDto() {
        EventDto eventDto = new EventDto();
        eventDto.setId(1L);
        eventDto.setOwnerId(1L);
        eventDto.setRelatedSkillIds(List.of(1L, 2L));
        return eventDto;
    }

    private void checkOwnerId(EventDto eventDto, User user, boolean resultCheckOwnerId) {
        Optional<User> optionalUser = Optional.of(user);

        if (resultCheckOwnerId) {
            when(userRepository.findById(eventDto.getOwnerId())).thenReturn(optionalUser);
        } else {
            when(userRepository.findById(eventDto.getOwnerId())).thenThrow(DataValidationException.class);
        }
    }

    private User createUser(EventDto eventDto) {
        Skill skillFirst = new Skill();
        skillFirst.setId(eventDto.getRelatedSkillIds().get(0));
        skillFirst.setTitle("skillFirst");
        Skill skillSecond = new Skill();
        skillSecond.setId(eventDto.getRelatedSkillIds().get(1));
        skillSecond.setTitle("skillSecond");
        List<Skill> skills = List.of(skillFirst, skillSecond);

        User user = new User();
        user.setId(eventDto.getOwnerId());
        user.setSkills(skills);

        return user;
    }

    @Test
    public void testCreateEventWithoutSkill() {
        EventDto eventDto = createEventDto();
        User user = createUser(eventDto);
        // Пользователь с таким id в БД есть
        checkOwnerId(eventDto, user, true);
        // Проверка списка необходимых для мероприятия умений (Провалена)
        checkExistSkill(eventDto, user, false);

        assertThrows(DataValidationException.class, () -> eventService.create(eventDto),
                "У пользователя не было необходимых умений");
    }

    private List<Skill> checkExistSkill(EventDto eventDto, User user, boolean resultCheckExistenceSkill) {
        when(skillRepository.findAllById(eventDto.getRelatedSkillIds())).thenReturn(user.getSkills());
        List<Skill> skills = skillRepository.findAllById(eventDto.getRelatedSkillIds());
        when(eventValidator.checkExistenceSkill(user, skills)).thenReturn(resultCheckExistenceSkill);
        return skills;
    }

    @Test
    public void testCreateEventSuitableSkills() {
        EventDto eventDto = createEventDto();
        User user = createUser(eventDto);
        // Пользователь с таким id в БД есть
        checkOwnerId(eventDto, user, true);
        // Проверка списка необходимых для мероприятия умений (Такие же, как у пользователя)
        List<Skill> relatedSkills = checkExistSkill(eventDto, user, true);
        Event eventExp = eventMapper.toEntity(eventDto);
        eventExp.setRelatedSkills(relatedSkills);

        eventService.create(eventDto);

        verify(eventRepository, times(1)).save(captorEvent.capture());
        Event eventActual = captorEvent.getValue();
        assertEquals(eventExp, eventActual);
    }

    @Test
    public void testGetEventNotExistingId() {
        Long notExistingId = 1L;
        when(eventRepository.findById(notExistingId)).thenThrow(DataValidationException.class);

        assertThrows(DataValidationException.class, () -> eventService.getEvent(notExistingId));
    }

    @Test
    public void testGetEventWithExistingId() {
        Long eventId = 1L;
        Event event = new Event();
        event.setId(eventId);
        event.setTitle("title");
        Optional<Event> optionalEvent = Optional.of(event);
        when(eventRepository.findById(eventId)).thenReturn(optionalEvent);
        EventDto eventDtoExp = eventMapper.toDto(event);

        EventDto eventDtoActual = eventService.getEvent(eventId);

        assertEquals(eventDtoExp, eventDtoActual);
    }

    @Test
    public void testGetEventsByFilter() {
        // Arrange
        EventFilterDto eventFilterDto = new EventFilterDto();
        eventFilterDto.setTitle("filter");

        Event firstEvent = new Event();
        Event secondEvent = new Event();
        firstEvent.setTitle("firstEvent");
        secondEvent.setTitle("secondEvent filter");
        List<Event> events = List.of(firstEvent, secondEvent);

        List<EventDto> eventsDto = new ArrayList<>();
        eventsDto.add(eventMapper.toDto(secondEvent));
        int sizeFilteredEventsExp = eventsDto.size();
        when(eventRepository.findAll()).thenReturn(events);

        // Action
        List<EventDto> filteredEventsDto = eventService.getEventsByFilter(eventFilterDto);
        int sizeFilteredEventsActual = filteredEventsDto.size();

        // Assert
        assertEquals(sizeFilteredEventsExp, sizeFilteredEventsActual);
        assertEquals(eventsDto, filteredEventsDto);
    }

    @Test
    public void testDeleteEvent() {
        Long id = 1L;

        eventService.deleteEvent(id);

        verify(eventRepository, times(1)).deleteById(id);
    }

    @Test
    public void testUpdateWithNotExistUserId() {
        EventDto eventDto = createEventDto();
        User user = createUser(eventDto);
        // id пользователя в базе не нашли
        checkOwnerId(eventDto, user, false);

        // ожидаем исключение
        assertThrows(DataValidationException.class, () -> eventService.updateEvent(eventDto), "Пользователя с таким id не должно быть в БД");
    }

    @Test
    public void testUpdateEventWithoutSkill() {
        EventDto eventDto = createEventDto();
        User user = createUser(eventDto);
        // Пользователь с таким id в БД есть
        checkOwnerId(eventDto, user, true);
        // Проверка списка необходимых для мероприятия умений (провалена)
        checkExistSkill(eventDto, user, false);

        assertThrows(DataValidationException.class, () -> eventService.updateEvent(eventDto),
                "У пользователя не было необходимых умений");
    }

    @Test
    public void testUpdateEventSuitableSkills() {
        EventDto eventDto = createEventDto();
        User user = createUser(eventDto);
        // Пользователь с таким id в БД есть
        checkOwnerId(eventDto, user, true);
        // Проверка списка необходимых для мероприятия умений (Успешно)
        List<Skill> relatedSkills = checkExistSkill(eventDto, user, true);
        Event eventExp = eventMapper.toEntity(eventDto);
        eventExp.setRelatedSkills(relatedSkills);

        eventService.updateEvent(eventDto);

        verify(eventRepository, times(1)).save(captorEvent.capture());
        Event eventActual = captorEvent.getValue();
        assertEquals(eventExp, eventActual);
    }

    @Test
    public void testGetOwnedEventsWithNotExistUserId() {
        long userId = -1L;
        when(userRepository.findById(userId)).thenThrow(DataValidationException.class);

        assertThrows(DataValidationException.class, () -> eventService.getOwnedEvents(userId), "Пользователя с таким id не должно быть в БД");
    }

    @Test
    public void testGetOwnedEventsWithExistUserId() {
        long userId = 1L;
        Event eventFirst = new Event();
        Event eventSecond = new Event();
        eventFirst.setTitle("eventFirst");
        eventSecond.setTitle("eventSecond");
        User user = new User();
        user.setId(userId);
        user.setOwnedEvents(List.of(eventFirst, eventSecond));
        List<EventDto> expectationEvents = user.getOwnedEvents().stream()
                .map(eventMapper::toDto)
                .toList();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        List<EventDto> actualEvents = eventService.getOwnedEvents(userId);

        assertEquals(expectationEvents, actualEvents);
    }

    @Test
    public void testGetParticipatedEventsWithNotExistUserId() {
        long userId = -1L;
        when(userRepository.findById(userId)).thenThrow(DataValidationException.class);

        assertThrows(DataValidationException.class, () -> eventService.getParticipatedEvents(userId), "Пользователя с таким id не должно быть в БД");
    }

    @Test
    public void testGetParticipatedEventsWithExistUserId() {
        User userFirst = new User();
        User userSecond = new User();
        User userThird = new User();
        userFirst.setId(1L);
        userSecond.setId(2L);
        userThird.setId(3L);
        Event eventFirst = new Event();
        Event eventSecond = new Event();
        eventFirst.setAttendees(List.of(userFirst, userSecond));
        eventSecond.setAttendees(List.of(userFirst, userThird));
        List<Event> events = List.of(eventFirst, eventSecond);
        List<EventDto> expectationEvents = events.stream()
                .map(eventMapper::toDto)
                .toList();
        when(userRepository.findById(userFirst.getId())).thenReturn(Optional.of(userFirst));
        when(eventRepository.findParticipatedEventsByUserId(userFirst.getId()))
                .thenReturn(events);

        List<EventDto> actualEvents = eventService.getParticipatedEvents(userFirst.getId());

        assertEquals(expectationEvents, actualEvents);
    }

}
