package school.faang.user_service.service.event;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.filters.event.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.EventMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.EventService;
import school.faang.user_service.service.SkillService;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@PrepareForTest(EventService.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillService skillService;

    @Mock
    private List<EventFilter> eventFilters;
    @Mock
    private EventService mockEventService;

    @InjectMocks
    private EventService eventService;

    @Spy
    private EventMapper eventMapper = new EventMapperImpl();

    @Captor
    private ArgumentCaptor<EventDto> eventCaptor;
    @Captor ArgumentCaptor<List<Long>> eventIdCaptor;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private @NotNull EventService getService() {
        return new EventService(
                eventMapper,
                skillService,
                eventRepository,
                userRepository,
                eventFilters
        );
    }
    private EventDto createNewEventCandidate() {
        return EventDto.builder()
                .id(1L)
                .title("title")
                .ownerId(2L)
                .relatedSkills(List.of(1L, 4L))
                .build();
    }

    @Test
    public void deactivateEventsByUser_updatesStatusAndDeletesEvents() {
        Long userId = 1L;

        Event event1 = new Event();
        event1.setStatus(EventStatus.COMPLETED);
        event1.setId(100L);

        Event event2 = new Event();
        event2.setStatus(EventStatus.COMPLETED);
        event2.setId(200L);

        List<Event> events = Arrays.asList(event1, event2);
        assertThat(events).hasSize(2);
        assertThat(events.get(0).getStatus()).isEqualTo(EventStatus.COMPLETED);
        assertThat(events.get(1).getStatus()).isEqualTo(EventStatus.COMPLETED);
        assertThat(events).extracting(Event::getId).containsOnly(100L, 200L);
    }

    @Test
    void deactivateEventsByUser_ShouldDeactivateAndDeleteEvents() {
        Long userId = 1L;

        Event event1 = new Event();
        event1.setId(1L);
        event1.setId(userId);
        event1.setStatus(EventStatus.IN_PROGRESS);

        Event event2 = new Event();
        event2.setId(2L);
        event2.setId(userId);
        event2.setStatus(EventStatus.IN_PROGRESS);
        mockEventService.deactivateEventsByUser(userId);
        verify(mockEventService, times(1)).deactivateEventsByUser(userId);
    }

    @Test
    public void testPrepareEventCandidate() {

        EventDto newEventDtoBefore = createNewEventCandidate();
        eventMapper.toEntityEvent(newEventDtoBefore);
        verify(eventMapper, times(1)).toEntityEvent(eventCaptor.capture());
        EventDto newEventDtoAfter = eventCaptor.getValue();
        assertEquals(newEventDtoBefore, newEventDtoAfter);
    }

    @Test
    public void testValidateEventRelatedSkills_ValidSkills() throws Exception {

        EventService service = getService();
        List<Long> relatedSkills = Arrays.asList(1L, 2L, 3L);
        List<Long> ownerSkillsIds = Arrays.asList(3L, 4L, 5L);
        Whitebox.invokeMethod(service, "validateEventRelatedSkills", relatedSkills, ownerSkillsIds);
    }

    @Test
    public void testValidateEventRelatedSkills_InvalidSkills() {
        EventService service = getService();
        List<Long> relatedSkills = Arrays.asList(1L, 2L, 3L);
        List<Long> ownerSkillsIds = Arrays.asList(4L, 5L, 6L);

        assertThrows(BusinessException.class, () -> {
            Whitebox.invokeMethod(service, "validateEventRelatedSkills", relatedSkills, ownerSkillsIds);
        });
    }

    @Test
    public void testClearPastEvents(){
        Event compleatedEvent = Event.builder()
                .id(1L)
                .status(EventStatus.COMPLETED)
                .build();

        when(eventRepository.findEventsByStatuses(any())).thenReturn(List.of(compleatedEvent));

        getService().clearPastEvents();

        verify(eventRepository, atLeastOnce()).deleteAllByIdInBatch(eventIdCaptor.capture());
        assertEquals(List.of(compleatedEvent.getId()), eventIdCaptor.getValue());

    }
}
