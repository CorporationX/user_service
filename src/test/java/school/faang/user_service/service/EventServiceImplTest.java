package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.event.CreateEventDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.UpdateEventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.event.EventServiceImpl;
import school.faang.user_service.service.event.EventValidator;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;


@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock private EventRepository eventRepository;
    @Mock private UserRepository userRepository;
    @Mock private EventMapper mapper;
    @Mock private UserContext userContext;
    @Mock private EventValidator validator;

    @InjectMocks
    private EventServiceImpl service;

    private final long requesterId = 42L;
    private User owner;
    private Skill s1;
    private Skill s2;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(requesterId);
        s1 = new Skill();
        s1.setId(1L);
        s2 = new Skill();
        s2.setId(2L);
        owner.setSkills(List.of(s1, s2));
    }

    @Test
    void create_ok_whenOwnerHasAllSkills() {
        when(userContext.getUserId()).thenReturn(requesterId);
        when(userRepository.getByIdOrThrow(requesterId)).thenReturn(owner);

        CreateEventDto dto = new CreateEventDto(
                "Title", "Desc",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                EventType.MEETING, "TLV", 50, List.of(1L, 2L)
        );

        doNothing().when(validator).validateDates(dto.startDate(), dto.endDate());
        when(validator.loadAndValidateSkills(List.of(1L, 2L))).thenReturn(List.of(s1, s2));
        doNothing().when(validator).ensureOwnerHasAllSkills(owner, List.of(s1, s2));

        Event mapped = new Event();
        mapped.setTitle(dto.title());
        mapped.setDescription(dto.description());
        mapped.setStartDate(dto.startDate());
        mapped.setEndDate(dto.endDate());
        mapped.setType(dto.type());

        Event saved = new Event();
        saved.setId(100L);
        saved.setOwner(owner);
        saved.setTitle(dto.title());
        saved.setDescription(dto.description());
        saved.setStartDate(dto.startDate());
        saved.setEndDate(dto.endDate());
        saved.setType(dto.type());
        saved.setStatus(EventStatus.PLANNED);

        EventDto out = new EventDto(
                100L, "Title", "Desc",
                dto.startDate(), dto.endDate(),
                dto.type(), "TLV", 50,
                requesterId, EventStatus.PLANNED,
                LocalDateTime.now(),
                List.of(1L, 2L), 0
        );

        when(mapper.toEvent(dto)).thenReturn(mapped);
        when(eventRepository.save(any(Event.class))).thenReturn(saved);
        when(mapper.toEventDto(saved)).thenReturn(out);

        EventDto result = service.createEvent(dto);

        assertThat(result.id()).isEqualTo(100L);
        verify(mapper).toEvent(dto);
        verify(eventRepository).save(argThat(e ->
                e.getOwner().getId() == requesterId
                        && e.getRelatedSkills().size() == 2
                        && e.getTitle().equals("Title")
        ));
        verify(validator).validateDates(dto.startDate(), dto.endDate());
        verify(validator).loadAndValidateSkills(List.of(1L, 2L));
        verify(validator).ensureOwnerHasAllSkills(owner, List.of(s1, s2));
    }

    @Test
    void create_throwsWhenEndBeforeStart() {
        when(userContext.getUserId()).thenReturn(requesterId);
        CreateEventDto dto = new CreateEventDto(
                "Title", "Desc",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(1),
                EventType.MEETING, null, null, List.of()
        );

        doThrow(new DataValidationException("endDate must be after startDate."))
                .when(validator).validateDates(dto.startDate(), dto.endDate());

        assertThatThrownBy(() -> service.createEvent(dto))
                .isInstanceOf(DataValidationException.class)
                .hasMessageContaining("endDate must be after startDate");
        verifyNoInteractions(eventRepository);
    }

    @Test
    void create_throwsWhenSkillNotFound() {
        when(userContext.getUserId()).thenReturn(requesterId);
        CreateEventDto dto = new CreateEventDto(
                "Title", "Desc",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                EventType.MEETING, null, null, List.of(1L, 99L)
        );

        doNothing().when(validator).validateDates(dto.startDate(), dto.endDate());
        doThrow(new DataValidationException("Some skills not found by ids: [1, 99]"))
                .when(validator).loadAndValidateSkills(List.of(1L, 99L));

        assertThatThrownBy(() -> service.createEvent(dto))
                .isInstanceOf(DataValidationException.class)
                .hasMessageContaining("Some skills not found");
        verifyNoInteractions(eventRepository);
    }

    @Test
    void create_throwsWhenOwnerLacksRequiredSkill() {
        when(userContext.getUserId()).thenReturn(requesterId);
        when(userRepository.getByIdOrThrow(requesterId)).thenReturn(owner);
        owner.setSkills(List.of(s1));

        CreateEventDto dto = new CreateEventDto(
                "Title", "Desc",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                EventType.MEETING, null, null, List.of(1L, 2L)
        );

        doNothing().when(validator).validateDates(dto.startDate(), dto.endDate());
        when(validator.loadAndValidateSkills(List.of(1L, 2L))).thenReturn(List.of(s1, s2));
        doThrow(new DataValidationException("Owner lacks required skills: [2]"))
                .when(validator).ensureOwnerHasAllSkills(owner, List.of(s1, s2));

        assertThatThrownBy(() -> service.createEvent(dto))
                .isInstanceOf(DataValidationException.class)
                .hasMessageContaining("Owner lacks required skills");
        verify(eventRepository, never()).save(any());
    }

    @Test
    void update_ok_whenOwnerAndDatesValid() {
        when(userContext.getUserId()).thenReturn(requesterId);
        long eventId = 777L;
        Event existing = new Event();
        existing.setId(eventId);
        existing.setOwner(owner);
        existing.setType(EventType.WEBINAR);

        UpdateEventDto dto = new UpdateEventDto(
                "New", "NewDesc",
                LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(4),
                EventType.PRESENTATION,
                EventStatus.IN_PROGRESS,
                "Dimona",
                10,
                List.of(1L)
        );

        Event saved = new Event();
        saved.setId(eventId);
        saved.setOwner(owner);
        saved.setTitle("New");
        saved.setDescription("NewDesc");
        saved.setStartDate(dto.startDate());
        saved.setEndDate(dto.endDate());
        saved.setType(dto.type());
        saved.setStatus(dto.status());

        EventDto out = new EventDto(
                eventId, "New", "NewDesc",
                dto.startDate(), dto.endDate(),
                dto.type(), "Dimona", 10,
                requesterId, dto.status(),
                LocalDateTime.now(), List.of(1L), 0
        );

        when(eventRepository.getByIdOrThrow(eventId)).thenReturn(existing);

        doNothing().when(validator).ensureOwner(existing, requesterId);
        doNothing().when(validator).validateDates(dto.startDate(), dto.endDate());
        when(validator.loadAndValidateSkills(List.of(1L))).thenReturn(List.of(s1));
        doNothing().when(validator).ensureOwnerHasAllSkills(owner, List.of(s1));

        when(eventRepository.save(existing)).thenReturn(saved);
        when(mapper.toEventDto(saved)).thenReturn(out);

        EventDto result = service.updateEvent(eventId, dto);

        assertThat(result.id()).isEqualTo(eventId);
        assertThat(result.title()).isEqualTo("New");
        assertThat(result.type()).isEqualTo(EventType.PRESENTATION);

        verify(eventRepository).getByIdOrThrow(eventId);
        verify(mapper).update(eq(existing), eq(dto));
        verify(eventRepository).save(existing);
        verify(validator).ensureOwner(existing, requesterId);
        verify(validator).validateDates(dto.startDate(), dto.endDate());
        verify(validator).loadAndValidateSkills(List.of(1L));
        verify(validator).ensureOwnerHasAllSkills(owner, List.of(s1));
    }

    @Test
    void update_throwsWhenRequesterNotOwner() {
        when(userContext.getUserId()).thenReturn(requesterId);
        long eventId = 7L;
        Event existing = new Event();
        existing.setId(eventId);
        User another = new User();
        another.setId(999L);
        existing.setOwner(another);

        when(eventRepository.getByIdOrThrow(eventId)).thenReturn(existing);

        UpdateEventDto dto = new UpdateEventDto(
                "N", "D",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                EventType.MEETING,
                EventStatus.PLANNED,
                null, null, List.of()
        );

        doThrow(new ForbiddenException("Only owner can modify/delete the event."))
                .when(validator).ensureOwner(existing, requesterId);

        assertThatThrownBy(() -> service.updateEvent(eventId, dto))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Only owner");
        verify(eventRepository, never()).save(any());
    }

    @Test
    void update_throwsWhenDatesInvalid() {
        when(userContext.getUserId()).thenReturn(requesterId);
        long eventId = 7L;
        Event existing = new Event();
        existing.setId(eventId);
        existing.setOwner(owner);

        when(eventRepository.getByIdOrThrow(eventId)).thenReturn(existing);

        UpdateEventDto dto = new UpdateEventDto(
                "N", "D",
                LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(2),
                EventType.MEETING,
                EventStatus.PLANNED,
                null, null, List.of()
        );

        doNothing().when(validator).ensureOwner(existing, requesterId);
        doThrow(new DataValidationException("endDate must be after startDate."))
                .when(validator).validateDates(dto.startDate(), dto.endDate());

        assertThatThrownBy(() -> service.updateEvent(eventId, dto))
                .isInstanceOf(DataValidationException.class)
                .hasMessageContaining("endDate must be after startDate");
        verify(eventRepository, never()).save(any());
    }

    @Test
    void update_throwsWhenOwnerLacksNewSkill() {
        when(userContext.getUserId()).thenReturn(requesterId);
        long eventId = 7L;
        Event existing = new Event();
        existing.setId(eventId);
        existing.setOwner(owner);

        when(eventRepository.getByIdOrThrow(eventId)).thenReturn(existing);

        Skill s999 = new Skill();
        s999.setId(999L);

        UpdateEventDto dto = new UpdateEventDto(
                "N", "D",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                EventType.MEETING,
                EventStatus.PLANNED,
                null, null, List.of(999L)
        );

        doNothing().when(validator).ensureOwner(existing, requesterId);
        doNothing().when(validator).validateDates(dto.startDate(), dto.endDate());
        when(validator.loadAndValidateSkills(List.of(999L))).thenReturn(List.of(s999));
        doThrow(new DataValidationException("Owner lacks required skills: [999]"))
                .when(validator).ensureOwnerHasAllSkills(owner, List.of(s999));

        assertThatThrownBy(() -> service.updateEvent(eventId, dto))
                .isInstanceOf(DataValidationException.class)
                .hasMessageContaining("Owner lacks required skills");
        verify(eventRepository, never()).save(any());
    }

    @Test
    void getByFilters_appliesAllFilters() {
        Event e1 = event(1, "Java meetup", "Spring & MapStruct",
                EventType.MEETING, owner, List.of(user(10)), "TLV");
        Event e2 = event(2, "Kotlin talk", "Coroutines",
                EventType.PRESENTATION, owner, List.of(user(20)), "Haifa");
        Event e3 = event(3, "Java webinar", "Reactive",
                EventType.WEBINAR, user(99), List.of(user(10)), "TLV");

        when(eventRepository.findAll()).thenReturn(List.of(e1, e2, e3));

        EventFilterDto f = new EventFilterDto("Java", "Reactive", 99L, 10L, EventType.WEBINAR);

        when(mapper.toEventDto(e3)).thenReturn(new EventDto(
                3L, "Java webinar", "Reactive",
                LocalDateTime.now(), LocalDateTime.now(),
                EventType.WEBINAR, "TLV", 0, 99L,
                EventStatus.PLANNED, LocalDateTime.now(), List.of(), 1
        ));

        List<EventDto> result = service.getEventByFilters(f);
        assertThat(result.get(0).id()).isEqualTo(3L);
    }

    @Test
    void getByFilters_returnsEmptyWhenNoMatch() {
        when(eventRepository.findAll()).thenReturn(List.of());
        List<EventDto> result = service.getEventByFilters(new EventFilterDto("x", "y", 1L, 2L, EventType.MEETING));
        assertTrue(result.isEmpty());
    }

    @Test
    void delete_ok_whenOwner() {
        when(userContext.getUserId()).thenReturn(requesterId);
        long eventId = 5L;
        Event e = new Event();
        e.setId(eventId);
        e.setOwner(owner);
        when(eventRepository.getByIdOrThrow(eventId)).thenReturn(e);

        doNothing().when(validator).ensureOwner(e, requesterId);

        service.deleteEvent(eventId);
        verify(eventRepository).delete(e);
    }

    @Test
    void delete_throwsWhenNotOwner() {
        when(userContext.getUserId()).thenReturn(requesterId);
        long eventId = 5L;
        Event e = new Event();
        e.setId(eventId);
        User someone = new User();
        someone.setId(777L);
        e.setOwner(someone);

        when(eventRepository.getByIdOrThrow(eventId)).thenReturn(e);
        doThrow(new ForbiddenException("Only owner can modify/delete the event."))
                .when(validator).ensureOwner(e, requesterId);

        assertThatThrownBy(() -> service.deleteEvent(eventId))
                .isInstanceOf(ForbiddenException.class);
        verify(eventRepository, never()).delete(any());
    }

    private static User user(long id) {
        User u = new User();
        u.setId(id);
        return u;
    }

    private static Event event(long id, String title, String desc,
                               EventType type, User owner, List<User> attendees, String location) {
        Event e = new Event();
        e.setId(id);
        e.setTitle(title);
        e.setDescription(desc);
        e.setType(type);
        e.setOwner(owner);
        e.setAttendees(attendees);
        e.setLocation(location);
        e.setStartDate(LocalDateTime.now().plusDays(1));
        e.setEndDate(LocalDateTime.now().plusDays(2));
        e.setStatus(EventStatus.PLANNED);
        return e;
    }
}