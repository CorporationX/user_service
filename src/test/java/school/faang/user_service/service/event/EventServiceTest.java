package school.faang.user_service.service.event;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EventNotFoundException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.filter.event.EventOwnerFilter;
import school.faang.user_service.filter.event.EventTitleFilter;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.mapper.event.EventMapperImpl;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.mapper.skill.SkillMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {


    @Mock
    private EventRepository eventRepository;

    @Spy
    private EventMapper eventMapper = new EventMapperImpl();

    @Spy
    private SkillMapper skillMapper = new SkillMapperImpl();

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EventService eventService;

    private EventDto eventDto;
    private User user;


    @BeforeEach
    public void setUp() {
        eventDto = new EventDto();
        user = new User();
    }


    @Test
    public void shouldThrowUserNotFoundExceptionCreateEvent() {
        eventDto.setOwnerId(100L);

        Mockito.when(userRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.empty());
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> eventService.createEvent(eventDto));
        assertEquals("User with ID: " + eventDto.getOwnerId() + " not found", exception.getMessage());
    }


    @Test
    public void shouldThrowDataValidationExceptionCreateEvent() {
        SkillDto skillDto1 = SkillDto.builder().id(1L).title("Skill 1").build();
        SkillDto skillDto2 = SkillDto.builder().id(2L).title("Skill 2").build();
        eventDto.setOwnerId(1L);
        eventDto.setRelatedSkills(Arrays.asList(skillDto1, skillDto2));

        Skill skill1 = Skill.builder().id(3L).title("Skill 3").build();
        Skill skill2 = Skill.builder().id(4L).title("Skill 4").build();
        user.setId(1L);
        user.setSkills(Arrays.asList(skill1, skill2));

        Mockito.when(userRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.of(user));
        DataValidationException exception = assertThrows(DataValidationException.class, () -> eventService.createEvent(eventDto));
        assertEquals("User with ID: " + eventDto.getOwnerId() + " does not possess all required skills for this event", exception.getMessage());
    }


    @Test
    public void shouldCreateEvent() {
        SkillDto skillDto1 = SkillDto.builder().id(1L).title("Skill 1").build();
        SkillDto skillDto2 = SkillDto.builder().id(2L).title("Skill 2").build();
        eventDto.setId(1L);
        eventDto.setOwnerId(1L);
        eventDto.setRelatedSkills(Arrays.asList(skillDto1, skillDto2));


        Skill skill1 = Skill.builder().id(1L).title("Skill 1").build();
        Skill skill2 = Skill.builder().id(2L).title("Skill 2").build();
        user.setId(1L);
        user.setSkills(Arrays.asList(skill1, skill2));

        Event event = Event.builder().id(1L).owner(user).relatedSkills(Arrays.asList(skill1, skill2)).build();

        Mockito.when(userRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.of(user));
        Mockito.when(eventRepository.save(eventMapper.toEntity(eventDto))).thenReturn(event);

        EventDto eventDto1 = assertDoesNotThrow(() -> eventService.createEvent(eventDto));

        Mockito.verify(eventRepository).save(eventMapper.toEntity(eventDto));
        assertEquals(eventDto1, eventDto);
    }


    @Test
    public void shouldThrowDataValidationExceptionGetEvent() {
        Long eventId = 1L;
        Mockito.when(eventRepository.findById(eventId)).thenReturn(Optional.empty());
        EventNotFoundException exception = assertThrows(EventNotFoundException.class, () -> eventService.getEvent(eventId));
        assertEquals("Event with ID: " + eventId + " not found", exception.getMessage());
    }

    @Test
    public void shouldThrowEventNotFoundExceptionDeleteEvent() {
        Long eventId = 1L;
        Mockito.when(eventRepository.existsById(eventId)).thenReturn(false);
        EventNotFoundException exception = assertThrows(EventNotFoundException.class, () -> eventService.deleteEvent(eventId));
        assertEquals("Event by ID: " + eventId + " not found for deleted", exception.getMessage());
    }


    @Test
    public void shouldDeleteEvent() {
        Long eventId = 1L;
        Mockito.when(eventRepository.existsById(eventId)).thenReturn(true);
        eventService.deleteEvent(eventId);
        Mockito.verify(eventRepository).deleteById(eventId);
    }


    @Test
    public void shouldUpdateEvent() {

        SkillDto skillDto1 = SkillDto.builder().id(1L).title("Skill 1").build();
        SkillDto skillDto2 = SkillDto.builder().id(2L).title("Skill 2").build();
        eventDto.setId(1L);
        eventDto.setOwnerId(1L);
        eventDto.setRelatedSkills(Arrays.asList(skillDto1, skillDto2));


        Skill skill1 = Skill.builder().id(1L).title("Skill 1").build();
        Skill skill2 = Skill.builder().id(2L).title("Skill 2").build();
        user.setId(1L);
        user.setSkills(Arrays.asList(skill1, skill2));

        Event event = Event.builder().id(1L).owner(user).relatedSkills(Arrays.asList(skill1, skill2)).build();

        Mockito.when(userRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.of(user));
        Mockito.when(eventRepository.save(eventMapper.toEntity(eventDto))).thenReturn(event);

        EventDto eventDto1 = assertDoesNotThrow(() -> eventService.updateEvent(eventDto));

        Mockito.verify(eventRepository).save(eventMapper.toEntity(eventDto));
        assertEquals(eventDto1, eventDto);

    }


    @Test
    public void shouldGetOwnedEvents() {
        Long userId = 1L;
        Event event1 = Event.builder().id(1L).title("event 1").build();
        Event event2 = Event.builder().id(2L).title("event 2").build();

        EventDto eventDto1 = EventDto.builder().id(1L).title("event 1").build();
        EventDto eventDto2 = EventDto.builder().id(2L).title("event 2").build();

        List<EventDto> expectedEventDtoList = Arrays.asList(eventDto1, eventDto2);

        Mockito.when(eventRepository.findAllByUserId(userId)).thenReturn(Arrays.asList(event1, event2));
        List<EventDto> eventDtoList = eventService.getOwnedEvents(userId);
        assertEquals(expectedEventDtoList, eventDtoList);
    }

    @Test
    public void shouldGetParticipatedEvents() {
        Long userId = 1L;
        Event event1 = Event.builder().id(1L).title("event 1").build();
        Event event2 = Event.builder().id(2L).title("event 2").build();

        EventDto eventDto1 = EventDto.builder().id(1L).title("event 1").build();
        EventDto eventDto2 = EventDto.builder().id(2L).title("event 2").build();

        List<EventDto> expectedEventDtoList = Arrays.asList(eventDto1, eventDto2);

        Mockito.when(eventRepository.findParticipatedEventsByUserId(userId)).thenReturn(Arrays.asList(event1, event2));
        List<EventDto> eventDtoList = eventService.getParticipatedEvents(userId);
        assertEquals(expectedEventDtoList, eventDtoList);
    }


    @Test
    public void shouldGetEventsByFilter() {
        Skill skill1 = Skill.builder().id(1L).title("Skill 1").build();
        Skill skill2 = Skill.builder().id(2L).title("Skill 2").build();

        EventFilterDto eventFilterDto = new EventFilterDto();
        eventFilterDto.setOwnerId(1L);
        eventFilterDto.setTitlePattern("tittle 1");

        User user1 = User.builder().id(1L).build();
        User user2 = User.builder().id(2L).build();
        User user3 = User.builder().id(3L).build();

        Event event1 = Event.builder().id(1L).owner(user1).title("tittle 1").build();
        Event event2 = Event.builder().id(2L).owner(user2).title("event 2").build();
        Event event3 = Event.builder().id(3L).owner(user3).title("event 3").build();

        EventDto eventDto = EventDto.builder().id(1L).ownerId(1L).title("tittle 1").build();

        Mockito.when(eventRepository.findAll()).thenReturn(Arrays.asList(event1, event2, event3));

        eventService = new EventService(eventRepository, userRepository, eventMapper, skillMapper, Arrays.asList(new EventTitleFilter(), new EventOwnerFilter()));

        List<EventDto> eventDtoList = eventService.getEventsByFilter(eventFilterDto);

        assertEquals(Arrays.asList(eventDto), eventDtoList);
    }


}