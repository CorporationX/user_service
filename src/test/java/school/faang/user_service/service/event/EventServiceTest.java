package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.event.DataValidationException;
import school.faang.user_service.mapper.EventFilterMapper;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @InjectMocks
    private EventService eventService;
    @Mock
    private EventRepository eventRepository;
    @Spy
    private EventMapper eventMapper;

    @Mock
    private UserRepository userRepository;
    @Mock
    private EventFilterMapper eventFilterMapper;
    @Mock
    private SkillRepository skillRepository;

    Event event = new Event();
    EventDto eventDto = new EventDto();
    Event optionalEvent;
    User optionalUser;
    User userWithSkill = new User();
    Skill skill1 = new Skill();
    Skill skill2 = new Skill();
    Skill ownerSkill1 = new Skill();
    Skill ownerSkill2 = new Skill();
    List<EventDto> filteredEvents;

    @BeforeEach
    void setUp() {
        skill1.setId(10);
        skill2.setId(20);
        ownerSkill1.setId(30L);
        ownerSkill2.setId(40L);
        userWithSkill.setSkills(List.of(skill1, skill2));
        eventDto.setOwnerId(10L);
        eventDto.setRelatedSkillsIds(userWithSkill.getSkills()
                .stream()
                .map(Skill::getId)
                .toList());
        filteredEvents = List.of(eventDto);
    }

    @Test
    void shouldReturnDataValidationExceptionWhenCreateTest() {
        when(userRepository.findById(eventDto.getOwnerId()))
                .thenReturn(Optional.ofNullable(optionalUser));
        assertThrows(DataValidationException.class,
                () -> eventService.create(eventDto));
    }

    @Test
    void shouldReturnDataValidationExceptionWhenSaveEventOfCreateTest() {
        when(userRepository.findById(eventDto.getOwnerId()))
                .thenReturn(Optional.ofNullable(optionalUser));
        assertThrows(DataValidationException.class,
                () -> eventService.create(eventDto));
    }

    @Test
    void shouldReturnIsSavedWhenSaveEventOfCreateTest() {
        when(userRepository.findById(eventDto.getOwnerId()))
                .thenReturn(Optional.of(userWithSkill));
        when(eventMapper.eventDtoToEntity(eventDto))
                .thenReturn(event);
        when(skillRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(skill1));
        eventService.create(eventDto);
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void shouldReturnEventDtoWhenGetEventTest() {

        when(eventRepository.findById(10L)).thenReturn(Optional.ofNullable(event));
        eventRepository.findById(10L);
        verify(eventRepository, times(1))
                .findById(10L);
        when(eventMapper.eventToDto(event)).thenReturn(eventDto);
        assertEquals(eventDto, eventService.getEvent(10L));
    }

    @Test
    void shouldReturnExceptionWhenGetEventTest() {
        when(eventRepository.findById(10L))
                .thenReturn(Optional.ofNullable(optionalEvent));
        assertThrows(DataValidationException.class,
                () -> eventService.getEvent(10L));
    }

    @Test
    void shouldReturnEventDtosWhenGetEventsByFilterTest() {
        EventFilterDto filter = new EventFilterDto(null, null, null,
                0, 10L, List.of(10L, 20L), null, null,
                null, null);
        event.setRelatedSkills(List.of(Skill.builder()
                .id(30L)
                .build(), Skill.builder()
                .id(40L)
                .build()));
        Event eventWithSkill = new Event();
        eventWithSkill.setRelatedSkills(List.of(skill1, skill2));
        when(eventRepository.findAll())
                .thenReturn(List.of(event, eventWithSkill));
        when(eventFilterMapper.eventToEventFilterDto(event))
                .thenReturn(filter);
        when(eventMapper.eventToDto(event)).thenReturn(eventDto);
        assertEquals(filteredEvents, eventService.getEventsByFilter(filter));
    }

    @Test
    void shouldIsDeletedWhenDeleteEventTest() {
        eventService.deleteEvent(10L);
        verify(eventRepository, times(1))
                .deleteById(10L);
    }

    @Test
    void shouldReturnDataValidationExceptionWhenUpdateEventTest() {
        when(userRepository.findById(anyLong()))
                .thenThrow(new DataValidationException("Такой пользователь не найден!"));

        assertThrows(DataValidationException.class, () -> eventService.updateEvent(eventDto));
    }

    @Test
    void shouldReturnDataValidationExceptionWithWrongCriteriesWhenUpdateEventTest() {
        User owner = new User();
        owner.setSkills(List.of(ownerSkill1, ownerSkill2));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        assertThrows(DataValidationException.class, () -> eventService.updateEvent(eventDto));
    }

    @Test
    void shouldReturnEventDtoWhenUpdateEventTest() {
        User owner = new User();
        owner.setSkills(List.of(skill1, skill2));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(eventMapper.eventDtoToEntity(eventDto)).thenReturn(event);

        eventService.updateEvent(eventDto);

        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void shouldReturnDataValidationExceptionWhenGetOwnedEventsTest() {
        when(userRepository.findById(anyLong()))
                .thenThrow(new DataValidationException("Такого пользователя не существует!"));

        assertThrows(DataValidationException.class,
                () -> eventService.getOwnedEvents(anyLong()));
    }

    @Test
    void shouldReturnEventDtosWhenGetOwnedEventsTest() {
        event.setId(10L);
        Event event1 = new Event();
        event1.setId(20L);
        List<Event> events = List.of(event, event1);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(userWithSkill));

        when(eventRepository.findAllByUserId(userWithSkill.getId()))
                .thenReturn(events);

        when(eventMapper.listEventsToDto(events))
                .thenReturn(filteredEvents);

        assertIterableEquals(filteredEvents, eventService.getOwnedEvents(anyLong()));
    }

    @Test
    void shouldReturnDataValidationExceptionWhenGetParticipatedEvents() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(DataValidationException.class,
                () -> eventService.getParticipatedEvents(1L));
    }
}