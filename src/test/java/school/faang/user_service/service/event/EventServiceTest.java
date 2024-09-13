package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.mapper.event.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    private SkillDto skillDto1;
    private SkillDto skillDto2;
    private EventDto eventDto;
    private Event event;
    private EventFilterDto eventFilterDto;
    private Skill skill;
    private User user;
    private List<Event> events;


    @InjectMocks
    private EventService eventService;

    @Mock
    EventRepository eventRepository;

    @Mock
    EventMapper eventMapper;

    @Mock
    SkillRepository skillRepository;

    @BeforeEach
    void setUp() {
        skillDto1 = SkillDto.builder()
                .id(1L)
                .title("skill1")
                .createdAt(LocalDateTime.now())
                .build();

        skillDto2 = SkillDto.builder()
                .id(2L)
                .title("skill2")
                .createdAt(LocalDateTime.now())
                .build();

        eventDto = EventDto.builder()
                .id(1L)
                .title("Новое событие")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.of(2024, 10, 1, 12, 0))
                .ownerId(1L)
                .description("description")
                .relatedSkills(List.of(skillDto1, skillDto2))
                .location("location")
                .maxAttendees(5)
                .build();

        skill = Skill.builder()
                .id(1L)
                .title("title")
                .createdAt(LocalDateTime.now())
                .build();

        user = new User();

        event = Event.builder()
                .id(1L)
                .title("Новое событие")
                .description("какое-то описание")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.of(2024, 10, 1, 12, 0))
                .location("location")
                .maxAttendees(5)
                .owner(user)
                .relatedSkills(List.of(skill))
                .type(EventType.GIVEAWAY)
                .status(EventStatus.IN_PROGRESS)
                .build();

        eventFilterDto = EventFilterDto.builder()
                .title("Новое событие")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.of(2024, 10, 1, 12, 0))
                .location("location")
                .maxAttendees(5)
                .type(EventType.GIVEAWAY)
                .status(EventStatus.IN_PROGRESS)
                .build();

        events = new ArrayList<>();
        events.add(event);
    }

    //positive test

    @Test
    void testGetEventWithValidObjFromDB() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        assertDoesNotThrow(() -> eventService.getEvent(1L));
    }

    @Test
    void testDeleteEventWithValidObjFromDB() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        assertDoesNotThrow(() -> eventService.getEvent(1L));
    }

    @Test
    void testGetOwnedEventsWithValidObjFromDB() {
        when(eventRepository.findAllByUserId(1L)).thenReturn(events);
        assertDoesNotThrow(() -> eventService.getOwnedEvents(1L));
    }

    @Test
    void testGetParticipatedEventsWithValidObjFromDB() {
        when(eventRepository.findParticipatedEventsByUserId(1L)).thenReturn(events);
        assertDoesNotThrow(() -> eventService.getParticipatedEvents(1L));
    }

    //negative test

    @Test
    void testGetEventWithNullFromDB() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> eventService.getEvent(1L));
    }

    @Test
    void testDeleteEventNullFromDB() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> eventService.getEvent(1L));
    }

    @Test
    void testGetOwnedEventsWithInvalidObjFromDB() {
        when(eventRepository.findAllByUserId(1L)).thenReturn(Collections.emptyList());
        assertThrows(NoSuchElementException.class, () -> eventService.getOwnedEvents(1L));
    }

    @Test
    void testGetParticipatedEventsWithInvalidObjFromDB() {
        when(eventRepository.findParticipatedEventsByUserId(1L)).thenReturn(Collections.emptyList());
        assertThrows(NoSuchElementException.class, () -> eventService.getParticipatedEvents(1L));
    }

    @Test
    void testUpdateEventWithAbsenceObjFromDB() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> eventService.updateEvent(eventDto));
    }
}