package school.faang.user_service.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.thread.ThreadPoolDistributor;
import school.faang.user_service.validator.event.EventValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.entity.event.EventStatus.COMPLETED;

@ExtendWith(MockitoExtension.class)
@Disabled
public class EventServiceTest {
    @Mock
    private EventMapper eventMapper;
    @Mock
    private SkillMapper skillMapper;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EventFilter filter;
    @Mock
    private EventValidator eventValidator;

    @Mock
    private ThreadPoolDistributor threadPoolDistributor;
    @Mock
    private ThreadPoolTaskExecutor customThreadPool;

    @InjectMocks
    private EventService eventService;
    ArgumentCaptor eventCaptor = ArgumentCaptor.forClass(Event.class);

    @BeforeEach
    void setUp() {
        eventService = new EventService(eventRepository, eventMapper,
                (List<EventFilter>) filter, eventValidator, threadPoolDistributor, 2);
        when(threadPoolDistributor.customThreadPool()).thenReturn(customThreadPool);
    }

    @Test
    public void testOwnerValidation_UserFound() {
        EventDto eventDto = new EventDto();
        eventDto.setOwnerId(1L);
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User result = eventValidator.ownerValidation(eventDto);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    public void testOwnerValidation_UserNotFound() {
        EventDto eventDto = new EventDto();
        eventDto.setOwnerId(2L);
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            eventValidator.ownerValidation(eventDto);
        });
        assertEquals("Ошибка: пользователь не найден", exception.getMessage());
    }

    @Test
    public void testSkillValidation_allSkillValidated() {
        Skill firstSkill = new Skill();
        Skill secondSkill = new Skill();
        firstSkill.setId(1L);
        secondSkill.setId(2L);
        SkillDto firstSkillDto = new SkillDto();
        SkillDto secondSkillDto = new SkillDto();
        firstSkillDto.setId(1L);
        secondSkillDto.setId(2L);
        List skillDtoList = List.of(firstSkillDto, secondSkillDto);
        List skillList = List.of(firstSkill, secondSkill);
        when(skillMapper.toEntityList(skillDtoList)).thenReturn(skillList);
        when(skillRepository.findById(1L)).thenReturn(Optional.of(firstSkill));
        when(skillRepository.findById(2L)).thenReturn(Optional.of(secondSkill));
        EventDto eventDto = new EventDto();
        eventDto.setRelatedSkills(skillDtoList);
        List result = eventValidator.skillValidation(eventDto);
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(skillRepository, times(2)).findById(anyLong());
    }

    @Test
    public void testSkillValidation_someSkillInvalidated() {
        User owner = new User();
        owner.setId(1L);
        Skill firstSkill = new Skill();
        Skill secondSkill = new Skill();
        firstSkill.setId(1L);
        firstSkill.setUsers(List.of(owner));
        secondSkill.setId(2L);
        secondSkill.setUsers(List.of(owner));
        SkillDto firstSkillDto = new SkillDto();
        SkillDto secondSkillDto = new SkillDto();
        firstSkillDto.setId(1L);
        secondSkillDto.setUserIds(List.of(owner.getId()));
        firstSkillDto.setUserIds(List.of(owner.getId()));
        List skillDtoList = List.of(firstSkillDto, secondSkillDto);
        List skillList = List.of(firstSkill, secondSkill);
        EventDto eventDto = new EventDto();
        eventDto.setOwnerId(1L);
        eventDto.setRelatedSkills(skillDtoList);
        when(skillMapper.toEntityList(eventDto.getRelatedSkills())).thenReturn(skillList);
        when(skillRepository.findById(1L)).thenReturn(Optional.of(firstSkill));
        when(skillRepository.findById(2L)).thenReturn(Optional.empty());
        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            eventValidator.skillValidation(eventDto);
        });
        assertEquals("Ошибка: навык с ID " + secondSkill.getId() + " не найден", exception.getMessage());

    }

    @Test
    public void testInputDataValidation_AllSkillsValidated() {
        User owner = new User();
        owner.setId(1L);
        Skill firstSkill = new Skill();
        Skill secondSkill = new Skill();
        firstSkill.setId(1L);
        firstSkill.setUsers(List.of(owner));
        secondSkill.setId(2L);
        secondSkill.setUsers(List.of(owner));
        SkillDto firstSkillDto = new SkillDto();
        SkillDto secondSkillDto = new SkillDto();
        firstSkillDto.setId(1L);
        secondSkillDto.setUserIds(List.of(owner.getId()));
        firstSkillDto.setUserIds(List.of(owner.getId()));
        List skillDtoList = List.of(firstSkillDto, secondSkillDto);
        List skillList = List.of(firstSkill, secondSkill);
        EventDto eventDto = new EventDto();
        eventDto.setOwnerId(1L);
        eventDto.setRelatedSkills(skillDtoList);
        List result = eventValidator.skillValidation(eventDto);
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(skillMapper.toEntityList(eventDto.getRelatedSkills())).thenReturn(skillList);
        when(skillRepository.findById(1L)).thenReturn(Optional.of(firstSkill));
        when(skillRepository.findById(2L)).thenReturn(Optional.of(secondSkill));
        assertDoesNotThrow(() -> eventValidator.inputDataValidation(eventDto));
    }

    @Test
    public void testInputDataValidation_SomeSkillsNotValidated() {
        User owner = new User();
        owner.setId(1L);
        Skill firstSkill = new Skill();
        Skill secondSkill = new Skill();
        firstSkill.setId(1L);
        firstSkill.setUsers(List.of(owner));
        secondSkill.setId(2L);
        secondSkill.setUsers(List.of(new User()));
        SkillDto firstSkillDto = new SkillDto();
        SkillDto secondSkillDto = new SkillDto();
        firstSkillDto.setId(1L);
        secondSkillDto.setUserIds(List.of(owner.getId()));
        firstSkillDto.setUserIds(List.of(owner.getId()));
        List skillDtoList = List.of(firstSkillDto, secondSkillDto);
        List skillList = List.of(firstSkill, secondSkill);
        EventDto eventDto = new EventDto();
        eventDto.setOwnerId(1L);
        eventDto.setRelatedSkills(skillDtoList);
        owner.setSkills(List.of(firstSkill));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(skillMapper.toEntityList(eventDto.getRelatedSkills())).thenReturn(skillList);
        when(skillRepository.findById(1L)).thenReturn(Optional.of(firstSkill));
        when(skillRepository.findById(2L)).thenReturn(Optional.of(secondSkill));
        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            eventValidator.inputDataValidation(eventDto);
        });
        assertEquals("Ошибка: пользователь не обладает всеми необходимыми навыками", exception.getMessage());
    }

    @Test
    public void testCreate_ValidEventDto() {
        User owner = new User();
        owner.setId(1L);
        Skill firstSkill = new Skill();
        Skill secondSkill = new Skill();
        firstSkill.setId(1L);
        firstSkill.setUsers(List.of(owner));
        secondSkill.setId(2L);
        secondSkill.setUsers(List.of(owner));
        SkillDto firstSkillDto = new SkillDto();
        SkillDto secondSkillDto = new SkillDto();
        firstSkillDto.setId(1L);
        secondSkillDto.setUserIds(List.of(owner.getId()));
        firstSkillDto.setUserIds(List.of(owner.getId()));
        List skillDtoList = List.of(firstSkillDto, secondSkillDto);
        List skillList = List.of(firstSkill, secondSkill);
        EventDto eventDto = new EventDto();
        eventDto.setOwnerId(1L);
        eventDto.setRelatedSkills(skillDtoList);
        owner.setSkills(List.of(firstSkill));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(skillMapper.toEntityList(eventDto.getRelatedSkills())).thenReturn(skillList);
        when(skillRepository.findById(1L)).thenReturn(Optional.of(firstSkill));
        when(skillRepository.findById(2L)).thenReturn(Optional.of(secondSkill));
        when(eventMapper.toEntity(eventDto)).thenReturn(new Event());
        when(eventRepository.save(any())).thenReturn(new Event());
        when(eventMapper.toDto((Event) eventCaptor.capture())).thenReturn(eventDto);
        EventDto createdEventDto = eventService.create(eventDto);
        assertNotNull(createdEventDto);
    }

    @Test
    public void testCreate_InvalidEventDto() {
        User owner = new User();
        owner.setId(1L);
        Skill firstSkill = new Skill();
        Skill secondSkill = new Skill();
        firstSkill.setId(1L);
        firstSkill.setUsers(List.of(owner));
        secondSkill.setId(2L);
        secondSkill.setUsers(List.of(new User()));
        SkillDto firstSkillDto = new SkillDto();
        SkillDto secondSkillDto = new SkillDto();
        firstSkillDto.setId(1L);
        secondSkillDto.setUserIds(List.of(owner.getId()));
        firstSkillDto.setUserIds(List.of(owner.getId()));
        List skillDtoList = List.of(firstSkillDto, secondSkillDto);
        List skillList = List.of(firstSkill, secondSkill);
        EventDto eventDto = new EventDto();
        eventDto.setOwnerId(1L);
        eventDto.setRelatedSkills(skillDtoList);
        owner.setSkills(List.of(firstSkill));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(skillMapper.toEntityList(eventDto.getRelatedSkills())).thenReturn(skillList);
        when(skillRepository.findById(1L)).thenReturn(Optional.of(firstSkill));
        when(skillRepository.findById(2L)).thenReturn(Optional.of(secondSkill));
        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            eventService.create(eventDto);
        });
        assertEquals("Ошибка: пользователь не обладает всеми необходимыми навыками", exception.getMessage());
    }

    @Test
    public void testGetEventById_EventExists() {
        EventDto eventDto = new EventDto();
        eventDto.setId(1L);
        Event event = new Event();
        event.setId(1L);
        when(eventRepository.existsById(eventDto.getId())).thenReturn(true);
        when(eventRepository.getById(eventDto.getId())).thenReturn(event);
        when(eventMapper.toDto(event)).thenReturn(eventDto);
        EventDto result = eventService.getEventById(eventDto.getId());
        assertNotNull(result);
        assertEquals(eventDto.getId(), result.getId());
    }

    @Test
    public void testGetEventById_EventNotExists() {
        long eventId = 2L;
        when(eventRepository.existsById(eventId)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> {
            eventService.getEventById(eventId);
        });
    }

    @Test
    //тест падает с ошибкой IllegalStateException: stream has already been operated upon or closed
    public void testGetEventsByFilter() {
        EventFilterDto filters = new EventFilterDto();
        filters.setTitlePattern("first");
        filters.setOwnerIdPattern(1L);
        User owner = new User();
        owner.setId(1L);
        EventDto firstEventDto = new EventDto();
        firstEventDto.setId(1L);
        firstEventDto.setOwnerId(1L);
        firstEventDto.setTitle("first");
        EventDto secondEventDto = new EventDto();
        secondEventDto.setId(2L);
        secondEventDto.setOwnerId(2L);
        secondEventDto.setTitle("second");
        Event firstEvent = new Event();
        firstEvent.setId(1L);
        firstEvent.setOwner(owner);
        firstEvent.setTitle("first");
        Event secondEvent = new Event();
        secondEvent.setId(2L);
        secondEvent.setOwner(owner);
        secondEvent.setTitle("second");
        List<Event> eventList = List.of(firstEvent, secondEvent);
        List<EventDto> eventDtoList = List.of(firstEventDto, secondEventDto);

        when(eventMapper.toEntity(firstEventDto)).thenReturn(firstEvent);
        when(eventMapper.toEntity(secondEventDto)).thenReturn(secondEvent);
        when(eventRepository.findAll()).thenReturn(eventList);
        when(eventMapper.toDtoList(eventList)).thenReturn(eventDtoList);
        when(filter.isApplicable(filters)).thenReturn(true);
        doAnswer(invocation -> {
            Stream stream = invocation.getArgument(0);
            EventFilterDto filterDto = invocation.getArgument(1);
            return null;
        }).when(filter).apply(any(Stream.class), any(EventFilterDto.class));
        List<EventDto> result = eventService.getEventsByFilter(filters);
        assertNotNull(result);
        assertEquals(eventDtoList.size(), result.size());
    }

    @Test
    public void testDeleteEvent() {
        Long eventId = 1L;
        eventService.deleteEvent(eventId);
        verify(eventRepository, times(1)).deleteById(eventId);
    }

    @Test
    public void testUpdateEvent_ValidEvent() {
        User owner = new User();
        owner.setId(1L);
        EventDto eventDto = new EventDto();
        eventDto.setId(1L);
        eventDto.setOwnerId(1L);
        eventDto.setTitle("first");
        Event eventEntity = new Event();
        eventEntity.setId(1L);
        eventEntity.setOwner(owner);
        Skill firstSkill = new Skill();
        Skill secondSkill = new Skill();
        firstSkill.setId(1L);
        firstSkill.setUsers(List.of(owner));
        secondSkill.setId(2L);
        secondSkill.setUsers(List.of(owner));
        SkillDto firstSkillDto = new SkillDto();
        SkillDto secondSkillDto = new SkillDto();
        firstSkillDto.setId(1L);
        secondSkillDto.setUserIds(List.of(owner.getId()));
        firstSkillDto.setUserIds(List.of(owner.getId()));
        List<SkillDto> skillDtoList = List.of(firstSkillDto, secondSkillDto);
        List<Skill> skillList = List.of(firstSkill, secondSkill);
        eventDto.setRelatedSkills(skillDtoList);
        when(skillMapper.toEntityList(eventDto.getRelatedSkills())).thenReturn(skillList);
        when(eventRepository.findById(eventDto.getId())).thenReturn(Optional.of(eventEntity));
        eventService.updateEvent(eventDto.getId(), eventDto);
    }

    @Test
    public void testGetOwnedEvents() {
        long userId = 1L;
        List<Event> events = new ArrayList<>();
        List<EventDto> expectedEventDto = new ArrayList<>();
        when(eventRepository.findAllByUserId(userId)).thenReturn(events);
        when(eventMapper.toDtoList(events)).thenReturn(expectedEventDto);
        List<EventDto> actualEventDto = eventService.getOwnedEvents(userId);
        verify(eventRepository, times(1)).findAllByUserId(userId);
        assertEquals(expectedEventDto, actualEventDto);
    }

    @Test
    public void testGetParticipatedEvents() {
        long userId = 1L;
        List<Event> events = new ArrayList<>();
        List<EventDto> expectedEventDto = new ArrayList<>();
        when(eventRepository.findParticipatedEventsByUserId(userId)).thenReturn(events);
        when(eventMapper.toDtoList(events)).thenReturn(expectedEventDto);
        List<EventDto> actualEventDto = eventService.getParticipatedEvents(userId);
        verify(eventRepository, times(1)).findParticipatedEventsByUserId(userId);
        assertEquals(expectedEventDto, actualEventDto);
    }

    @Test
    void deletingAllPastEvents_ShouldDistributeTasksAmongThreads() {
        Event event1 = Event.builder().id(1).build();
        Event event2 = Event.builder().id(2).build();
        Event event3 = Event.builder().id(3).build();
        Event event4 = Event.builder().id(4).build();
        List<Event> events = List.of(event1, event2, event3, event4);
        var firstSubListEvents = List.of(event1, event2);
        var secondSubListEvents = List.of(event3, event4);
        when(eventRepository.findByStatus(COMPLETED)).thenReturn(events);
        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);

        eventService.deletingAllPastEvents();

        verify(eventRepository).findByStatus(COMPLETED);
        verify(threadPoolDistributor.customThreadPool(), times(2)).submit(runnableCaptor.capture());
        runnableCaptor.getAllValues().forEach(Runnable::run);
        verify(eventRepository).deleteAllInBatch(firstSubListEvents);
        verify(eventRepository).deleteAllInBatch(secondSubListEvents);
    }

    @Test
    void deletingAllPastEvents_ShouldHandleEmptyList() {
        when(eventRepository.findByStatus(COMPLETED)).thenReturn(Collections.emptyList());

        eventService.deletingAllPastEvents();

        verify(eventRepository).findByStatus(COMPLETED);
    }
}















