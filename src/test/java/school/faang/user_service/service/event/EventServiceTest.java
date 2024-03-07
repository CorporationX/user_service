package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.filter.EventFilter;
import school.faang.user_service.validation.event.EventValidator;

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

@SpringBootTest
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EventValidator eventValidator;
    @Mock
    private SkillMapper skillMapper;
    @Mock
    private EventMapper eventMapper;
    @Mock
    private List<EventFilter> eventFilters;
    @InjectMocks
    private EventService eventService;

    private Event event;
    private EventDto eventDto;
    private Skill requiredSkill;
    private SkillDto requiredSkillDto;
    private Skill userSkill;
    private SkillDto userSkillDto;
    private User user;

    @BeforeEach
    void setUp() {
        requiredSkill = Skill.builder()
                .id(1)
                .title("Required skill")
                .build();
        requiredSkillDto = SkillDto.builder()
                .id(requiredSkill.getId())
                .title(requiredSkill.getTitle())
                .build();
        userSkill = Skill.builder()
                .id(2)
                .title("User's skill")
                .build();
        userSkillDto = SkillDto.builder()
                .id(userSkill.getId())
                .title(userSkill.getTitle())
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
                .relatedSkills(List.of(requiredSkillDto))
                .location(event.getLocation())
                .maxAttendees(event.getMaxAttendees())
                .build();
    }

    @Test
    void create_userSavedToDb_isValid() {
        doNothing().when(eventValidator).validateUserHasRequiredSkills(eventDto);
        eventService.create(eventDto);
        verify(eventRepository, times(1)).save(eventMapper.toEntity(eventDto));
    }

    @Test
    void getEvent_EventIsFound_isValid() {
        doNothing().when(eventValidator).validateEventExistsById(anyLong());
        when(eventRepository.findById(anyLong())).thenReturn(Optional.ofNullable(event));

        eventService.getEvent(anyLong());
        verify(eventMapper, times(1)).toDto(any(Event.class));
    }

    @Test
    void deleteEvent_EventIsDeleted_IsValid() {
        doNothing().when(eventValidator).validateEventExistsById(anyLong());

        eventService.deleteEvent(1);
        verify(eventRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void updateEvent_EventFoundAndUpdated_ThenSavedToDb() {
        doNothing().when(eventValidator).validateUserHasRequiredSkills(any(EventDto.class));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        doNothing().when(eventValidator).validateUserIsOwnerOfEvent(any(User.class), any(EventDto.class));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        eventService.updateEvent(eventDto);

        verify(eventRepository, times(1)).save(any(Event.class));
        verify(eventMapper, times(1)).toDto(any(Event.class));
    }

    @Test
    void getEventsByFilter_FiltersByTitle_IsValid() {
        EventFilterDto filters = new EventFilterDto();

        eventService.getEventsByFilter(filters);

        verify(eventRepository, times(1)).findAll();
        verify(eventMapper, times(1)).toDto(anyList());
    }
}
