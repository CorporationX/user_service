package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
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
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.filter.event.EventOwnerIdPattern;
import school.faang.user_service.filter.event.EventTitlePattern;
import school.faang.user_service.mapper.EventMapperImpl;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.EventValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @Mock
    private EventRepository eventRepository;

    @Spy
    private EventMapperImpl eventMapper;

    @Mock
    private EventValidator eventValidator;

    @InjectMocks
    private EventService eventService;

    private User owner;
    private Event firstEvent;
    private Event secondEvent;
    private Event thirdEvent;
    private List<EventFilter> eventFilters = new ArrayList<>();
    private EventFilterDto eventFilterDtoTitle;
    private EventFilterDto eventFilterDtoOwner;

    @BeforeEach
    void prepareData() {
        eventFilters.add(new EventTitlePattern());
        eventFilters.add(new EventOwnerIdPattern());

        eventService = new EventService(eventRepository, eventMapper, eventValidator, eventFilters);

        owner = User.builder()
                .id(1L)
                .build();

        firstEvent = Event.builder()
                .id(1L)
                .title("First Event")
                .owner(owner)
                .relatedSkills(List.of(Skill.builder()
                        .id(1L)
                        .build()))
                .build();
        secondEvent = Event.builder()
                .id(2L)
                .title("Second Event")
                .owner(owner)
                .relatedSkills(List.of(Skill.builder()
                        .id(1L)
                        .build()))
                .build();
        thirdEvent = Event.builder()
                .id(3L)
                .title("Third Event")
                .owner(owner)
                .relatedSkills(List.of(Skill.builder()
                        .id(1L)
                        .build()))
                .build();

        eventFilterDtoTitle = EventFilterDto.builder()
                .titlePattern("Third Event")
                .build();
        eventFilterDtoOwner = EventFilterDto.builder()
                .ownerPattern(1L)
                .build();
    }

    @Test
    public void testEventIsCreated() {
        EventDto eventDto = new EventDto();
        Event eventEntity = eventMapper.toEntity(eventDto);
        Mockito.when(eventMapper.toEntity(eventDto)).thenReturn(eventEntity);
        eventService.create(eventDto);
        Mockito.verify(eventRepository, Mockito.times(1)).save(eventEntity);
    }

    @Test
    public void testGetEventDtoNotFound() {
        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.empty());
        Assert.assertThrows(EntityNotFoundException.class, () -> eventService.getEventDto(1L));
    }

    @Test
    public void testGetEventDto() {
        EventDto eventDtoExpected = EventDto.builder()
                .id(1L)
                .title("Title")
                .relatedSkillsIds(List.of(1L))
                .build();
        Event eventEntity = Event.builder()
                .id(1L)
                .title("Title")
                .relatedSkills(List.of(Skill.builder()
                        .id(1L)
                        .build()))
                .build();

        long eventId = eventDtoExpected.getId();
        Mockito.when(eventRepository.findById(eventId)).thenReturn(Optional.of(eventEntity));
        eventService.getEventDto(eventDtoExpected.getId());
        EventDto eventDtoActual = eventService.getEventDto(eventId);
        assertEquals(eventDtoExpected, eventDtoActual);
    }

    @Test
    public void testGetEventsByFilterTitle() {
        Mockito.when(eventRepository.findAll()).thenReturn(List.of(firstEvent, secondEvent, thirdEvent));
        List<EventDto> actualResult = eventService.getEventsByFilter(eventFilterDtoTitle);
        assertEquals(1, actualResult.size());
    }

    @Test
    public void testGetEventsByFilterOwner() {
        Mockito.when(eventRepository.findAll()).thenReturn(List.of(firstEvent, secondEvent, thirdEvent));
        List<EventDto> actualResult = eventService.getEventsByFilter(eventFilterDtoOwner);
        assertEquals(3, actualResult.size());
    }

    @Test
    public void testDeleteEvent() {
        Event event = Event.builder()
                .id(3L)
                .build();
        long eventId = event.getId();

        eventService.deleteEvent(eventId);
        Mockito.verify(eventRepository, Mockito.times(1)).deleteById(eventId);
    }

    @Test
    public void testDeleteEventIncorrectId() {
        Mockito.verify(eventRepository, Mockito.never()).deleteById(999L);
    }

    @Test
    public void testEventIsUpdated() {
        EventDto eventDto = new EventDto();
        Event eventEntity = eventMapper.toEntity(eventDto);

        eventService.create(eventDto);
        Mockito.verify(eventRepository, Mockito.times(1)).save(eventEntity);
    }

    @Test
    public void testGetOwnedEvents() {
        User user = User.builder()
                .id(1L)
                .build();
        long userId = user.getId();
        List<Event> emptyEvents = new ArrayList<>();
        Mockito.when(eventRepository.findAllByUserId(userId)).thenReturn(Collections.emptyList());

        Assertions.assertIterableEquals(emptyEvents, eventService.getOwnedEvents(userId));
    }

    @Test
    public void testGetParticipatedEvents() {
        long userId = 1L;

        List<Event> events = List.of(
                Event.builder()
                        .id(1L)
                        .relatedSkills(List.of(Skill.builder()
                                .id(1L)
                                .build()))
                        .build(),
                Event.builder()
                        .id(2L)
                        .relatedSkills(List.of(Skill.builder()
                                .id(2L)
                                .build()))
                        .build()
        );

        Mockito.when(eventRepository.findParticipatedEventsByUserId(userId)).thenReturn(events);
        List<EventDto> eventsExpected = eventMapper.toListDto(events);
        List<EventDto> eventsActual = eventService.getParticipatedEvents(userId);

        Mockito.verify(eventRepository, Mockito.times(1)).findParticipatedEventsByUserId(userId);
        assertEquals(eventsExpected, eventsActual);
    }
}
