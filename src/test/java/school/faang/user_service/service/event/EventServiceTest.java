package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.validator.event.EventValidator;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.mapper.event.EventMapperImpl;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserService userService;
    @Mock
    private SkillService skillService;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private EventValidator eventValidator;
    @Spy
    private EventMapper eventMapper = new EventMapperImpl();
    @Mock
    private List<EventFilter> eventFilters;
    @InjectMocks
    private EventService eventService;
    EventDto inputEventDto = new EventDto(null, "test", "test", LocalDateTime.now(), LocalDateTime.now(),
            "test", 1, 1l, List.of(1l, 2l));
    EventDto eventDto = new EventDto(1l, "test", "test", LocalDateTime.now(), LocalDateTime.now(),
            "test", 1, 1l, List.of(1l, 2l));

    @BeforeEach
    void setUp() {
        eventService = new EventService(eventRepository, skillService, userService, eventMapper, eventFilters, eventValidator);
    }

//    @Test
//    void shouldCreateEqualsEventService() {
//        User user = new User();
//        Event event = new Event();
//        Skill skill = new Skill();
//        skill.setId(1);
//        skill.setTitle("test");
//        Skill skill2 = new Skill();
//        skill2.setId(2);
//        skill2.setTitle("test");
//        Optional<Skill> skillOptional = Optional.of(skill);
//        when(eventRepository.save(event)).thenReturn(event);
//        when(eventMapper.toEntity(eventDto)).thenReturn(new Event());
//        when(userService.getUser(1l)).thenReturn(user);
//        when(skillService.getSkillById(1l)).thenReturn(skill);
//        when(skillService.getSkillById(2l)).thenReturn(skill2);
//        when(skillRepository.findById(1l)).thenReturn(skillOptional);
//
//        EventDto result = eventService.create(inputEventDto);
//
//        assertAll(
//                () -> verify(eventRepository, times(1)).save(event),
//                () -> verify(eventMapper, times(1)).toEntity(eventDto),
//                () -> verify(userService, times(1)).getUser(1l),
//                () -> verify(skillService, times(1)).getSkillById(1l),
//                () -> assertEquals(inputEventDto.getTitle(), result.getTitle())
//        );
//    }

    @Test
    void shouldGetEventService() {
        long eventId = 1;
        Event event = new Event();
        event.setTitle("test");
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        EventDto result = eventService.getEvent(eventId);
        assertNotNull(result);
    }

    @Test
    void shouldDeleteEventService() {
        long eventId = 1;
        eventService.deleteEvent(eventId);
        verify(eventRepository, times(1)).deleteById(eventId);
    }


}



