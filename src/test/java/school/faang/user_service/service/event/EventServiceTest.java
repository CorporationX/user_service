package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.filter.EventFilterDto;
import school.faang.user_service.filter.impl.DescriptionPatternFilter;
import school.faang.user_service.filter.impl.TitlePatternFilter;
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
    private EventRepository eventRepository;
    private SkillRepository skillRepository;
    private UserRepository userRepository;
    private EventMapperImpl eventMapper;
    private EventValidator eventValidator;
    @Captor
    private ArgumentCaptor<Event> captorEvent;

    // Пришлось использовать такой способ из-за фильтров
    @BeforeEach
    void setUp() {
        eventRepository = Mockito.mock(EventRepository.class);
        skillRepository = Mockito.mock(SkillRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        eventMapper = Mockito.spy(EventMapperImpl.class);
        eventValidator = Mockito.mock(EventValidator.class);
        EventFilter titleFilters = Mockito.spy(TitlePatternFilter.class);
        EventFilter descriptionFilters = Mockito.spy(DescriptionPatternFilter.class);
        List<EventFilter> eventFilters = List.of(titleFilters, descriptionFilters);
        eventService = new EventService(eventRepository, skillRepository, userRepository, eventMapper, eventValidator,
                eventFilters);

    }

    @Test
    public void testCreateWithNotExistUserId() {
        EventDto eventDto = createEventDto();
        User user = createUser(eventDto);
        // id пользователя в базе не нашли
        checkOwnerId(eventDto, user, false);

        // ожидаем исключение
        assertThrows(DataValidationException.class,
                () -> eventService.create(eventDto), "Пользователя с таким id не должно быть в БД");
    }

    @Test
    public void testCreateEventWithoutSkill() {
        EventDto eventDto = createEventDto();
        User owner = createUser(eventDto);
        // Пользователь с таким id в БД есть
        checkOwnerId(eventDto, owner, true);
        // Проверка списка необходимых для мероприятия умений (Провалена)
        List<Skill> skills = getSkillById(eventDto, owner);
//            Не хочет работать, если метод ничего не возвращает.
//            Об этом спрашивал. Как протестировать валидатор, который ничего не возвращает, но бросает исключения?
        when(eventValidator.checkExistenceSkill(owner, skills)).thenThrow(DataValidationException.class);

        assertThrows(DataValidationException.class, () -> eventService.create(eventDto),
                "У пользователя не было необходимых умений");
    }

    @Test
    public void testCreateEventSuitableSkills() {
        EventDto eventDto = createEventDto();
        User owner = createUser(eventDto);
        // Пользователь с таким id в БД есть
        checkOwnerId(eventDto, owner, true);
        // Проверка списка необходимых для мероприятия умений (Такие же, как у пользователя) прошла успешно
        List<Skill> skills = getSkillById(eventDto, owner);
        Event eventExp = eventMapper.toEntity(eventDto);
        eventExp.setRelatedSkills(skills);
        eventExp.setOwner(owner);
        // В методе сервиса результат проверки не используется. Просто заглушка, чтобы проверять выброс исключений.
        when(eventValidator.checkExistenceSkill(owner, skills)).thenReturn(true);

        eventService.create(eventDto);

        // Проверка вызова финального метода на репозитории и отлов возвращаемой сущности.
        verify(eventRepository, times(1)).save(captorEvent.capture());
        Event eventActual = captorEvent.getValue();
        eventExp.setCreatedAt(eventActual.getCreatedAt());
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
        List<Event> events = prepareEventsForFiltering();

        EventFilterDto eventFilterDto = new EventFilterDto();
        eventFilterDto.setTitlePattern("filter");
        eventFilterDto.setDescriptionPattern("filter");

        // ожидаем третье событие
        EventDto eventDtoExp = eventMapper.toDto(events.get(2));
        List<EventDto> eventsDtoExp = List.of(eventDtoExp);
        int sizeFilteredEventsExp = eventsDtoExp.size();
        when(eventRepository.findAll()).thenReturn(events);

        // Action
        List<EventDto> filteredEventsDto = eventService.getEventsByFilter(eventFilterDto);
        int sizeFilteredEventsActual = filteredEventsDto.size();

        // Assert
        assertEquals(sizeFilteredEventsExp, sizeFilteredEventsActual);
        assertEquals(eventsDtoExp, filteredEventsDto);
    }

    @Test
    public void testDeleteEvent() {
        Long id = 1L;

        eventService.deleteEvent(id);

        verify(eventRepository, times(1)).deleteById(id);
    }

    @Test
    public void testUpdateEventWithoutSkill() {
        // Владелец, событие и список умений события, который хранятся в БД
        EventDto eventDto = createEventDto();
        User owner = createUser(eventDto);
        List<Skill> skills = owner.getSkills();
        Event event = eventMapper.toEntity(eventDto);
        event.setOwner(owner);
        event.setRelatedSkills(skills);
        when(eventRepository.findById(eventDto.getId())).thenReturn(Optional.of(event));
        checkOwnerId(eventDto, owner, true); // Пользователь с таким id в БД есть
        // В дто пришёл изменённый список умений -> попадаем в блок if()
        Long newSkillId = 3L;
        eventDto.getRelatedSkillIds().add(newSkillId);
        // Новый список умений для обновления entity
        List<Skill> skillsForUpdate = prepareEvenDtoForUpdate(skills, newSkillId);
        when(skillRepository.findAllById(eventDto.getRelatedSkillIds())).thenReturn(skillsForUpdate);
        // Проверка списка необходимых для мероприятия умений (провалена)
        when(eventValidator.checkExistenceSkill(owner, skillsForUpdate)).thenThrow(DataValidationException.class);

        assertThrows(DataValidationException.class, () -> eventService.updateEvent(eventDto),
                "У пользователя не было необходимых умений");
    }

    @Test
    public void testUpdateEventSuitableSkills() {
        // Владелец, событие и список умений события, который хранятся в БД
        EventDto eventDto = createEventDto();
        User owner = createUser(eventDto);
        List<Skill> skills = new ArrayList<>(owner.getSkills());
        Event event = eventMapper.toEntity(eventDto);
        event.setOwner(owner);
        event.setRelatedSkills(skills);
        when(eventRepository.findById(eventDto.getId())).thenReturn(Optional.of(event));
        checkOwnerId(eventDto, owner, true); // Пользователь с таким id в БД есть
        // В дто пришёл изменённый список умений -> попадаем в блок if()
        Long newSkillId = 3L;
        eventDto.getRelatedSkillIds().add(newSkillId);
        // Новый список умений для обновления entity
        List<Skill> skillsForUpdate = prepareEvenDtoForUpdate(skills, newSkillId);
        when(skillRepository.findAllById(eventDto.getRelatedSkillIds())).thenReturn(skillsForUpdate);
        // Проверка списка необходимых для мероприятия умений (успешно)
        when(eventValidator.checkExistenceSkill(owner, skillsForUpdate)).thenReturn(true);
        // Поскольку БД вернёт мою переменную event, она претерпит изменения в методе и станет как actual.
        // Создаю expectation
        Event eventExp = new Event();
        eventExp.setId(event.getId());
        eventExp.setOwner(event.getOwner());
        eventExp.setRelatedSkills(new ArrayList<>(skillsForUpdate));


        eventService.updateEvent(eventDto);

        verify(eventRepository, times(1)).save(captorEvent.capture());
        Event eventActual = captorEvent.getValue();
        eventExp.setUpdatedAt(eventActual.getUpdatedAt());
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


    private EventDto createEventDto() {
        EventDto eventDto = new EventDto();
        eventDto.setId(1L);
        eventDto.setOwnerId(1L);
        eventDto.setRelatedSkillIds(new ArrayList<>(List.of(1L, 2L)));
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

    private List<Event> prepareEventsForFiltering() {
        Event eventFirst = new Event();
        eventFirst.setTitle("title first");
        eventFirst.setDescription("description first filter");
        Event eventSecond = new Event();
        eventSecond.setTitle("title second filter");
        eventSecond.setDescription("description second");
        Event eventThird = new Event();
        eventThird.setTitle("title third filter");
        eventThird.setDescription("description third filter");
        Event eventFourth = new Event();
        eventFourth.setTitle("title fourth filter");
        eventFourth.setDescription("description fourth");

        return List.of(eventFirst, eventSecond, eventThird, eventFourth);
    }

    private List<Skill> getSkillById(EventDto eventDto, User owner) {
        when(skillRepository.findAllById(eventDto.getRelatedSkillIds())).thenReturn(owner.getSkills());
        return skillRepository.findAllById(eventDto.getRelatedSkillIds());
    }

    private List<Skill> prepareEvenDtoForUpdate(List<Skill> skills, Long newSkillId) {
        Skill newSkill = new Skill();
        newSkill.setId(newSkillId);
        newSkill.setTitle("thirdSkill");
        List<Skill> skillsForUpdate = new ArrayList<>(skills);
        skillsForUpdate.add(newSkill);

        return skillsForUpdate;
    }

}
