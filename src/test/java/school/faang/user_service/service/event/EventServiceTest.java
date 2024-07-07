package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    @Captor
    private ArgumentCaptor<EventDto> captor;

    Event event = new Event();
    EventDto eventDto = new EventDto();
    Event optionalEvent;
    User optionalUser;
    User nullUser = new User();
    User userWithSkill = new User();
    Skill skill1 = new Skill();
    Skill skill2 = new Skill();
    List<EventDto> filteredEvents;

    @BeforeEach
    void setUp() {
        skill1.setId(10);
        skill2.setId(20);
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
        Event eventWithSkill = new Event();
//        eventWithSkill.setRelatedSkills();
        when(eventRepository.findAll())
                .thenReturn(List.of(event, new Event()));
        when(eventFilterMapper.eventToEventFilterDto(event))
                .thenReturn(filter);
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
        when(eventRepository.findById(eventDto.getId()))
                .thenReturn(Optional.ofNullable(optionalEvent));
        assertThrows(DataValidationException.class,
                () -> eventService.updateEvent(eventDto));
    }

    @Test
    void getOwnedEvents() {
    }

    @Test
    void getParticipatedEvents() {
    }
}