package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.filter.EventFilter;
import school.faang.user_service.validation.event.EventValidator;
import school.faang.user_service.validation.user.UserValidator;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventMapper eventMapper;
    @Mock
    private List<EventFilter> eventFilters;
    @Mock
    private EventValidator eventValidator;
    @Mock
    private UserValidator userValidator;
    @InjectMocks
    private EventService eventService;

    private Event event;
    private EventDto eventDto;
    private Skill requiredSkill;
    private Skill userSkill;
    private User user;

    @BeforeEach
    void setUp() {
        requiredSkill = Skill.builder()
                .id(1)
                .title("Required skill")
                .build();
        userSkill = Skill.builder()
                .id(2)
                .title("User's skill")
                .build();
        user = User.builder()
                .id(3)
                .username("Valid username")
                .skills(List.of(userSkill))
                .build();
        event = Event.builder()
                .id(4)
                .title("Valid event title")
                .description("Valid description")
                .startDate(LocalDateTime.of(2024, Month.JANUARY, 2, 12, 0))
                .endDate(LocalDateTime.now().plusYears(5))
                .location("Valid location")
                .maxAttendees(50)
                .owner(user)
                .relatedSkills(List.of(requiredSkill))
                .build();
        eventDto = EventDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .ownerId(event.getOwner().getId())
                .description(event.getDescription())
                .relatedSkillsIds(List.of(requiredSkill.getId()))
                .location(event.getLocation())
                .maxAttendees(event.getMaxAttendees())
                .build();
    }

    @Test
    void create_userSavedToDb_ThenReturnedAsDto() {
        doNothing().when(eventValidator).validateUserHasRequiredSkills(eventDto);
        when(eventMapper.toEntity(eventDto)).thenReturn(event);
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        eventService.create(eventDto);

        verify(eventMapper, times(1)).toDto(event);
    }

    @Test
    void getEvent_EventIsFound_ThenReturnedAsDto() {
        doNothing().when(eventValidator).validateEventExistsById(anyLong());
        when(eventRepository.findById(anyLong())).thenReturn(Optional.ofNullable(event));

        eventService.getEvent(anyLong());
        verify(eventMapper, times(1)).toDto(any(Event.class));
    }

    @Test
    void deleteEvent_EventIsDeleted_IsValid() {
        eventService.deleteEvent(1);
        verify(eventRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void updateEvent_EventFoundAndUpdated_ThenSavedToDb() {
        doNothing().when(eventValidator).validateUserHasRequiredSkills(any(EventDto.class));
        doNothing().when(eventValidator).validateEventExistsById(anyLong());
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        doNothing().when(eventValidator).validateUserIsOwnerOfEvent(any(User.class), any(EventDto.class));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        eventService.updateEvent(eventDto);

        verify(eventRepository, times(1)).save(any(Event.class));
        verify(eventMapper, times(1)).toDto(any(Event.class));
    }

    @Test
    void getEventsByFilter_EventFilteredByTitle_ThenReturnedAsDto() {
        EventFilterDto filters = new EventFilterDto();

        eventService.getEventsByFilter(filters);

        verify(eventRepository, times(1)).findAll();
        verify(eventMapper, times(1)).toDto(anyList());
    }

    @Test
    void getOwnedEvents_OwnedEventsFound_ThenReturnedAsDto() {
        doNothing().when(userValidator).validateUserExistsById(anyLong());
        when(eventRepository.findAllByUserId(anyLong())).thenReturn(List.of(event));
        when(eventMapper.toDto(anyList())).thenReturn(List.of(eventDto));

        eventService.getOwnedEvents(1L);
        verify(eventMapper, times(1)).toDto(List.of(event));
    }

    @Test
    void getParticipatedEvents_ParticipatedEventsFound_ThenReturnedAsDto() {
        doNothing().when(userValidator).validateUserExistsById(anyLong());
        when(eventRepository.findParticipatedEventsByUserId(anyLong())).thenReturn(List.of(event));
        when(eventMapper.toDto(anyList())).thenReturn(List.of(eventDto));

        eventService.getParticipatedEvents(1L);
        verify(eventMapper, times(1)).toDto(List.of(event));
    }
}
