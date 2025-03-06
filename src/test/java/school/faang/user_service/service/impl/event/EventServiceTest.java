package school.faang.user_service.service.impl.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.filter.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapperImpl;
import school.faang.user_service.mapper.SkillMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event_filter.Filter;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    private EventServiceImpl eventService;
    EventRepository eventRepository = Mockito.mock(EventRepository.class);
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    EventMapperImpl eventMapper = Mockito.mock(EventMapperImpl.class);
    SkillMapperImpl skillMapper = Mockito.mock(SkillMapperImpl.class);

    Filter filtermock = Mockito.mock(Filter.class);
    List<Filter> filterList = List.of(filtermock);

    EventDto eventDto;
    Event event;
    User user;
    SkillDto skillDto1;
    SkillDto skillDto2;
    Skill skill1;
    Skill skill2;
    List<Skill> skills;
    List<SkillDto> skillsDtos;
    List<Event> ownedEvents;
    List<EventDto> ownedEventsDto;
    EventFilterDto filterDto;

    @BeforeEach
    void setUp() {

        eventService = new EventServiceImpl(eventRepository, userRepository, eventMapper, filterList, skillMapper);

        skillDto1 = new SkillDto();
        skillDto1.setId(1L);
        skillDto2 = new SkillDto();
        skillDto2.setId(2L);


        skillsDtos = new ArrayList<>();
        skillsDtos.add(skillDto1);
        skillsDtos.add(skillDto2);

        skill1 = Skill.builder().id(1L).title("Java").build();
        skill2 = Skill.builder().id(2L).title("Spring").build();

        skills = new ArrayList<>();
        skills.add(skill1);
        skills.add(skill2);

        user = User.builder()
                .id(10L)
                .skills(skills)
                .ownedEvents(ownedEvents)
                .build();

        event = Event.builder()
                .id(22L)
                .owner(user)
                .title("title")
                .relatedSkills(skills)
                .build();

        ownedEvents = new ArrayList<>();
        ownedEvents.add(event);

        eventDto = EventDto.builder()
                .id(22L)
                .ownerId(user.getId())
                .title("title")
                .relatedSkills(skillsDtos)
                .build();

        ownedEventsDto = new ArrayList<>();
        ownedEventsDto.add(eventDto);

        filterDto = EventFilterDto.builder()
                .id(22L)
                .title("title")
                .build();
    }

    @Test
    void testGetEvent() {

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(eventMapper.toDto(event)).thenReturn(eventDto);

        EventDto result = eventService.getEvent(eventDto.id());
        assertEquals(eventDto.id(), result.id());
    }


    @Test
    void testCreateEventwithSufficientSkills() {

        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(eventMapper.toEntity(eventDto)).thenReturn(event);
        when(eventRepository.save(event)).thenReturn(event);
        when(eventMapper.toDto(event)).thenReturn(eventDto);
        EventDto result = eventService.create(eventDto);

        assertEquals(eventDto, result);

        verify(eventRepository, times(1)).save(event);
    }


    @Test
    public void testCreateEventwithInsufficientSkills() {

        when(userRepository.findById(10L)).thenReturn(Optional.empty());
        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            eventService.create(eventDto);
        });
        assertEquals("Такого user не существует", exception.getMessage());

    }

    @Test
    public void testGetEventsByFilter() {
        when(filterList.get(0).isApplicable(new EventFilterDto(22L, "title"))).thenReturn(true);
        when(filterList.get(0).apply(any(), any())).thenReturn(Stream.of(ownedEvents.get(0)));
        List<EventDto> result = eventService.getEventsByFilter(filterDto);

        assertEquals(1, result.size());
    }

    @Test
    public void testDeleteIdEvent() {
        when(eventRepository.existsById(event.getId())).thenReturn(true);
        eventService.deleteEvent(event.getId());
        verify(eventRepository, times(1)).deleteById(event.getId());
    }

    @Test
    public void testGetOwnedEvents() {
        when(eventRepository.findAllByUserId(10L)).thenReturn(ownedEvents);
        when(eventMapper.toDto(event)).thenReturn(eventDto);
        List<EventDto> result = eventService.getOwnedEvents(10L);
        assertEquals(result, ownedEventsDto);
    }

    @Test
    public void testUpdateEvent() {
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(skillMapper.toDto(skill1)).thenReturn(skillDto1);
        when(skillMapper.toDto(skill2)).thenReturn(skillDto2);
        when(eventMapper.toEntity(eventDto)).thenReturn(event);
        when(eventMapper.toDto(event)).thenReturn(eventDto);
        when(eventRepository.save(event)).thenReturn(event);
        when(eventRepository.findById(22L)).thenReturn(Optional.of(event));
        eventMapper.update(eventDto,event);

        EventDto result = eventService.updateEvent(eventDto);
        assertEquals(result, eventDto);
    }
    @Test
    public void testGetParticipatedEvents(){
        when(eventRepository.findParticipatedEventsByUserId(10L)).thenReturn(ownedEvents);
        when(eventMapper.toDto(event)).thenReturn(eventDto);
        List<EventDto> result = eventService.getParticipatedEvents(10L);
        assertEquals(result, ownedEventsDto);
    }
}
