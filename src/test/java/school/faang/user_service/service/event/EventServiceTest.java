package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exceptions.event.DataValidationException;
import school.faang.user_service.exceptions.event.EventNotFoundException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.filter.EventFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    private EventRepository eventRepository;
    private UserRepository userRepository;
    private EventMapper mapper;
    private EventServiceImpl service;

    private final Long event = 1L;
    private EventDto eventDto;
    private User owner;

    @BeforeEach
    void init() {
        eventRepository = mock(EventRepository.class);
        userRepository = mock(UserRepository.class);
        mapper = mock(EventMapper.class);
        EventFilter filter = mock(EventFilter.class);
        List<EventFilter> filters = List.of(filter);
        service = new EventServiceImpl(eventRepository, userRepository, filters, mapper);

        List<SkillDto> skills1 = List.of(
                SkillDto.builder().id(1L).build(),
                SkillDto.builder().id(2L).build()
        );

        eventDto = EventDto.builder()
                .id(1L)
                .ownerId(1L)
                .relatedSkills(skills1)
                .build();

        owner = User.builder().id(event).build();

        Event eventToReturn = Event.builder()
                .id(event)
                .owner(User.builder().id(1L).build())
                .relatedSkills(List.of(Skill.builder().id(1L).build(), Skill.builder().id(2L).build()))
                .build();

        Mockito.lenient().when(filters.get(0).isAcceptable(any(EventFilterDto.class))).thenReturn(true);
        Mockito.lenient().when(filters.get(0).apply(any(), any(EventFilterDto.class))).thenReturn(Stream.of(eventToReturn));

        Mockito.lenient().when(mapper.toDto(any())).thenReturn(eventDto);
        Mockito.lenient().when(mapper.toEntity(any())).thenReturn(eventToReturn);
    }

    @Test
    void createNoOwnerEvent() {
        eventDto.setOwnerId(event);
        DataValidationException e = assertThrows(DataValidationException.class, () -> service.create(eventDto));
        assertEquals("owner with id=1 not found", e.getMessage());
    }

    @Test
    void createOwnerHasNoEnoughSkills() {
        owner.setSkills(new ArrayList<>());
        when(userRepository.findById(event)).thenReturn(Optional.of(owner));
        DataValidationException e = assertThrows(DataValidationException.class, () -> service.create(eventDto));
        assertEquals("user with id=1 has no enough skills to create event", e.getMessage());
    }

    @Test
    void createGoodEvent() {
        Event eventEntity = mapper.toEntity(eventDto);
        owner.setSkills(eventEntity.getRelatedSkills());
        when(userRepository.findById(event)).thenReturn(Optional.of(owner));
        when(eventRepository.save(eventEntity)).thenReturn(eventEntity);
        assertEquals(eventDto, service.create(eventDto));
    }

    @Test
    void getNonExistingEvent() {
        EventNotFoundException e = assertThrows(EventNotFoundException.class, () -> service.getEvent(event));
        assertEquals("cannot find event with id=1", e.getMessage());
    }

    @Test
    void getExistingEvent() {
        Event eventEntity = mapper.toEntity(eventDto);
        when(eventRepository.findById(event)).thenReturn(Optional.of(eventEntity));
        assertEquals(eventDto, service.getEvent(event));
    }

    @Test
    void getEventsByFilter() {
        EventFilterDto filter = new EventFilterDto();
        List<Event> eventList = List.of(mapper.toEntity(eventDto));
        when(eventRepository.findAll()).thenReturn(eventList);
        assertIterableEquals(List.of(eventDto), service.getEventsByFilter(filter));
    }

    @Test
    void deleteNonExistingEvent() {
        EventNotFoundException e = assertThrows(EventNotFoundException.class, () -> service.deleteEvent(event));
        assertEquals("cannot find event with id=1", e.getMessage());
    }

    @Test
    void deleteExistingEvent() {
        Event eventEntity = mapper.toEntity(eventDto);
        when(eventRepository.findById(event)).thenReturn(Optional.of(eventEntity));
        assertEquals(eventDto, service.deleteEvent(event));
        verify(eventRepository, times(1)).deleteById(event);
    }

    @Test
    void updateNoOwnerEvent() {
        eventDto.setOwnerId(event);
        DataValidationException e = assertThrows(DataValidationException.class, () -> service.create(eventDto));
        assertEquals("owner with id=1 not found", e.getMessage());
    }

    @Test
    void updateOwnerHasNoEnoughSkills() {
        owner.setSkills(new ArrayList<>());
        when(userRepository.findById(event)).thenReturn(Optional.of(owner));
        DataValidationException e = assertThrows(DataValidationException.class, () -> service.updateEvent(eventDto));
        assertEquals("user with id=1 has no enough skills to update event", e.getMessage());
    }

    @Test
    void updateGoodEvent() {
        Event eventEntity = mapper.toEntity(eventDto);
        owner.setSkills(eventEntity.getRelatedSkills());
        when(userRepository.findById(event)).thenReturn(Optional.of(owner));
        when(eventRepository.save(eventEntity)).thenReturn(eventEntity);
        assertEquals(eventDto, service.updateEvent(eventDto));
    }

    @Test
    void getOwnedEvents() {
        Event eventEntity = mapper.toEntity(eventDto);
        when(eventRepository.findAllByUserId(event)).thenReturn(List.of(eventEntity));
        assertIterableEquals(List.of(eventDto), service.getOwnedEvents(event));
    }

    @Test
    void getOwnedEventsNonExistingUserId() {
        assertIterableEquals(new ArrayList<>(), service.getOwnedEvents(event));
    }

    @Test
    void getParticipatedEvents() {
        Event eventEntity = mapper.toEntity(eventDto);
        when(eventRepository.findParticipatedEventsByUserId(event)).thenReturn(List.of(eventEntity));
        assertIterableEquals(List.of(eventDto), service.getParticipatedEvents(event));
    }

    @Test
    void getParticipatedEventsNonExistingUserId() {
        assertIterableEquals(new ArrayList<>(), service.getParticipatedEvents(event));
    }
}