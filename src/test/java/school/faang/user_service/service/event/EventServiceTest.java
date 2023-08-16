package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.service.event.filters.EventEndDateFilter;
import school.faang.user_service.service.event.filters.EventFilter;
import school.faang.user_service.service.event.filters.EventStartDateFilter;
import school.faang.user_service.service.event.filters.EventTitleFilter;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    EventDto eventDto;

    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventMapper eventMapper;
    @Mock
    private SkillRepository skillRepository;

    @Mock
    private UserRepository userRepository;

    private EventService eventService;

    private Skill userSkill = new Skill();


    @BeforeEach
    public void init() {
        EventFilter eventTitleFilter = new EventTitleFilter();
        List<EventFilter> eventFilterList = List.of(eventTitleFilter);
        eventService = new EventService(eventRepository, skillRepository, eventMapper, eventFilterList, userRepository);

        userSkill.setTitle("Coding");
        userSkill.setId(1L);
        eventDto = EventMock.getEventDto();
    }

    @Test
    public void testCreateEvent() {
        User alex = new User();
        alex.setId(1L);

        Mockito.when(eventMapper.toEntity(eventDto)).thenReturn(new Event());
        Mockito.when(userRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.of(alex));

        eventService.create(eventDto);
        Mockito.verify(eventRepository, Mockito.times(1)).save(eventMapper.toEntity(eventDto));
    }

    @Test
    public void testUpdateEvent() {
        User alex = new User();
        alex.setId(1L);
        Long anyId = 1L;

        Mockito.when(userRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.of(alex));
        Mockito.lenient().when(eventMapper.toEntity(eventDto)).thenReturn(new Event());

        EventDto existingEventDto = EventMock.getEventDto();
        Event existingEventEntity = EventMock.getEventEntity();

        Mockito.when(eventRepository.findById(anyId)).thenReturn(Optional.of(existingEventEntity));
        Mockito.when(eventMapper.toEntity(existingEventDto)).thenReturn(existingEventEntity);
        Mockito.when(eventMapper.toDto(existingEventEntity)).thenReturn(existingEventDto);

        EventDto eventToUpdateDto = EventMock.getEventDto();

        eventToUpdateDto.setTitle("Updated Title");
        eventToUpdateDto.setDescription("Updated description");

        eventService.updateEvent(eventToUpdateDto);
        Mockito.verify(eventMapper, Mockito.times(1)).update(existingEventDto, eventToUpdateDto);
        Mockito.verify(eventRepository, Mockito.times(1)).save(existingEventEntity);
    }

    @Test
    public void testCreateSkillsValidation() {
        Skill mockedSkill = new Skill();
        mockedSkill.setTitle("Running");

        Mockito.when(skillRepository.findAllByUserId(1L)).thenReturn(List.of(mockedSkill));

        assertThrows(DataValidationException.class, () -> {
            eventService.create(eventDto);
        });
    }

    @Test
    public void testEditSkillsValidation() {
        Skill mockedSkill = new Skill();
        mockedSkill.setTitle("Running");

        Mockito.when(skillRepository.findAllByUserId(1L)).thenReturn(List.of(mockedSkill));

        assertThrows(DataValidationException.class, () -> {
            eventService.updateEvent(eventDto);
        });
    }

    @Test
    public void testGetEventByIdSuccess() {
        Long anyId = 1L;
        Event mockEvent = new Event();
        mockEvent.setTitle("Mock");

        Mockito.lenient().when(eventRepository.findById(anyId)).thenReturn(Optional.of(mockEvent));
        try {
            eventService.get(anyId);
            Mockito.verify(eventRepository, Mockito.times(1)).findById(anyId);
        } catch (Exception e) {
        }
    }

    @Test
    public void testGetEventByIdFail() {
        Long anyId = 1L;
        Mockito.lenient().when(skillRepository.findById(anyId)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> {
            eventService.get(anyId);
        });
    }


    @Test
    public void testGetAllUserParticipationEvents() {
        long anyUserId = 1L;
        Mockito.lenient().when(eventRepository.findParticipatedEventsByUserId(anyUserId)).thenReturn(List.of(new Event(), new Event(), new Event()));
        List<EventDto> events = eventService.getParticipatedEvents(anyUserId);
        Assertions.assertEquals(3, events.size());
    }

    @Test
    public void testEventDeleting() {
        Long anyTestId = 1L;
        eventService.delete(anyTestId);
        Mockito.verify(eventRepository, Mockito.times(1)).deleteById(anyTestId);
    }

    @Test
    public void testGetAllUserEvents() {
        long anyUserId = 1L;
        Mockito.lenient().when(eventRepository.findAllByUserId(anyUserId)).thenReturn(List.of(new Event(), new Event()));
        List<EventDto> events = eventService.getOwnedEvents(anyUserId);
        Assertions.assertEquals(2, events.size());
    }

    @Test
    void testGetAllUserEventsByTitleFilter() {
        Event javaEvent = new Event();
        javaEvent.setTitle("Java");

        Event pythonEvent = new Event();
        pythonEvent.setTitle("Python");

        Event jsEvent = new Event();
        jsEvent.setTitle("JavaScript");

        Mockito.lenient().when(eventRepository.findAll()).thenReturn(List.of(javaEvent, pythonEvent, jsEvent));

        EventFilterDto eventFilterDto = new EventFilterDto();

        eventFilterDto.setTitle("Jav");

        List<EventDto> events = eventService.getEventsByFilter(eventFilterDto);

        Assertions.assertEquals(2, events.size());
    }

    @Test
    void testGetAllUserEventsByStartDateFilter() {
        List<EventFilter> eventFilterList = List.of(new EventStartDateFilter());
        eventService = new EventService(eventRepository, skillRepository, eventMapper, eventFilterList, userRepository);

        Event javaEvent = new Event();
        javaEvent.setTitle("Java");
        javaEvent.setStartDate(LocalDateTime.of(2015,
            Month.JULY, 29, 19, 0, 0));

        Event pythonEvent = new Event();
        pythonEvent.setTitle("Python");
        pythonEvent.setStartDate(LocalDateTime.of(2018,
            Month.JULY, 29, 19, 0, 0));

        Event jsEvent = new Event();
        jsEvent.setTitle("JavaScript");
        jsEvent.setStartDate(LocalDateTime.of(2020,
            Month.JULY, 29, 19, 0, 0));

        Mockito.lenient().when(eventRepository.findAll()).thenReturn(List.of(javaEvent, pythonEvent, jsEvent));

        EventFilterDto eventFilterDto = new EventFilterDto();

        eventFilterDto.setStartDate(LocalDateTime.of(2018, Month.JANUARY, 8, 0, 0));

        List<EventDto> events = eventService.getEventsByFilter(eventFilterDto);

        Assertions.assertEquals(2, events.size());
    }

    @Test
    void testGetAllUserEventsByEndDateFilter() {
        List<EventFilter> eventFilterList = List.of(new EventEndDateFilter());
        eventService = new EventService(eventRepository, skillRepository, eventMapper, eventFilterList, userRepository);

        Event javaEvent = new Event();
        javaEvent.setTitle("Java");
        javaEvent.setEndDate(LocalDateTime.of(2015,
            Month.JULY, 29, 19, 0, 0));

        Event pythonEvent = new Event();
        pythonEvent.setTitle("Python");
        pythonEvent.setEndDate(LocalDateTime.of(2018,
            Month.JULY, 29, 19, 0, 0));

        Event jsEvent = new Event();
        jsEvent.setTitle("JavaScript");
        jsEvent.setEndDate(LocalDateTime.of(2020,
            Month.JULY, 29, 19, 0, 0));

        Mockito.lenient().when(eventRepository.findAll()).thenReturn(List.of(javaEvent, pythonEvent, jsEvent));

        EventFilterDto eventFilterDto = new EventFilterDto();

        eventFilterDto.setEndDate(LocalDateTime.of(2019, Month.JANUARY, 8, 0, 0));

        List<EventDto> events = eventService.getEventsByFilter(eventFilterDto);

        Assertions.assertEquals(2, events.size());
    }

    @Test
    public void testDeleteAllByIds() {
        eventService.deleteAllByIds(List.of(1L, 2L, 3L, 4L));

        verify(eventRepository, times(1)).deleteAllById(List.of(1L, 2L, 3L, 4L));
    }

    @Test
    @DisplayName("Should remove specific user from event.attendee list, if users > 2")
    public void testRemoveUserFromEvents() {
        Event running = new Event();
        Event swimming = new Event();
        Event coding = new Event();
        Event relaxing = new Event();

        User alex = new User();
        alex.setId(1L);

        User blake = new User();
        blake.setId(2L);

        running.setId(1L);
        running.setAttendees(List.of(alex, blake));

        swimming.setId(2L);
        swimming.setAttendees(List.of(alex, blake));

        coding.setId(3L);
        coding.setAttendees(List.of(alex, blake));

        relaxing.setId(4L);
        relaxing.setAttendees(List.of(alex, blake));

        when(eventRepository.findAllById(List.of(1L, 2L, 3L, 4L))).thenReturn(List.of(running, swimming, coding, relaxing));

        int removedUsersFromEventCount = eventService.removeUserFromEvents(List.of(1L, 2L, 3L, 4L), 1L);

        assertEquals(4, removedUsersFromEventCount);
        assertEquals(1, running.getAttendees().size());
        assertEquals(1, swimming.getAttendees().size());
        assertEquals(1, coding.getAttendees().size());
        assertEquals(1, relaxing.getAttendees().size());
    }
}