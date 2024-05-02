package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.exceptions.event.DataValidationException;
import school.faang.user_service.exceptions.event.EventNotFoundException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.mapper.event.EventMapperImpl;
import school.faang.user_service.mapper.event.SkillMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private EventMapper mapper = new EventMapperImpl(Mappers.getMapper(SkillMapper.class));
    @InjectMocks
    private EventServiceImpl service;

    private EventDto eventDto1, eventDto2, eventDto3, eventDto4;
    private User owner1;
    private EventFilterDto filter;

    @BeforeEach
    void getEvents() {
        List<SkillDto> skills1 = List.of(
                new SkillDto(1L, "skill1"),
                new SkillDto(2L, "skill2")
        );
        List<SkillDto> skills2 = List.of(
                new SkillDto(1L, "skill1"),
                new SkillDto(3L, "skill3")
        );
        List<SkillDto> skills3 = List.of(
                new SkillDto(2L, "skill3"),
                new SkillDto(3L, "skill4")
        );
        List<SkillDto> skills4 = List.of(
                new SkillDto(1L, "skill1"),
                new SkillDto(4L, "skill4")
        );
        eventDto1 = EventDto.builder()
                .id(1L)
                .title("event1")
                .startDate(LocalDateTime.now().plusHours(10))
                .endDate(LocalDateTime.now().plusDays(1))
                .ownerId(1L)
                .location("loc1")
                .relatedSkills(skills1)
                .build();
        eventDto2 = EventDto.builder()
                .id(2L)
                .title("event2")
                .startDate(LocalDateTime.now().plusMinutes(20))
                .endDate(LocalDateTime.now().plusHours(2))
                .ownerId(2L)
                .location("loc2")
                .relatedSkills(skills2)
                .build();
        eventDto3 = EventDto.builder()
                .id(3L)
                .title("event3")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMinutes(30))
                .ownerId(3L)
                .location("loc3")
                .relatedSkills(skills3)
                .build();
        eventDto4 = EventDto.builder()
                .id(4L)
                .title("event4")
                .startDate(LocalDateTime.now().plusDays(3))
                .endDate(LocalDateTime.now().plusDays(5))
                .ownerId(4L)
                .location("loc4")
                .relatedSkills(skills4)
                .build();
    }

    @BeforeEach
    void getOwners() {
        owner1 = User.builder()
                .id(1L)
                .username("owner1")
                .build();
    }

    @BeforeEach
    void getFilter() {
        filter = new EventFilterDto();
    }

    @Test
    void createNoOwnerEvent() {
        eventDto1.setOwnerId(-1L);
        DataValidationException e = assertThrows(DataValidationException.class, () -> service.create(eventDto1));
        assertEquals("owner with id=-1 not found", e.getMessage());
    }

    @Test
    void createOwnerHasNoEnoughSkills() {
        owner1.setSkills(new ArrayList<>());
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner1));
        DataValidationException e = assertThrows(DataValidationException.class, () -> service.create(eventDto1));
        assertEquals("user with id=1 has no enough skills to create event", e.getMessage());
    }

    @Test
    void createGoodEvent() {
        Event eventEntity = mapper.toEntity(eventDto1);
        owner1.setSkills(eventEntity.getRelatedSkills());
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner1));
        when(eventRepository.save(eventEntity)).thenReturn(eventEntity);
        assertEquals(eventDto1, service.create(eventDto1));
    }

    @Test
    void getNonExistingEvent() {
        EventNotFoundException e = assertThrows(EventNotFoundException.class, () -> service.getEvent(-1L));
        assertEquals("cannot find event with id=-1", e.getMessage());
    }

    @Test
    void getExistingEvent() {
        Event eventEntity = mapper.toEntity(eventDto1);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(eventEntity));
        assertEquals(eventDto1, service.getEvent(1L));
    }

    @Test
    void getEventsFilteredByFromDate() {
        filter.setStartDate(eventDto4.getStartDate().minusMinutes(1));
        List<Event> eventList = Stream.of(eventDto1, eventDto2, eventDto3, eventDto4)
                .map(mapper::toEntity)
                .toList();
        when(eventRepository.findAll()).thenReturn(eventList);
        assertIterableEquals(List.of(eventDto4), service.getEventsByFilter(filter));
    }

    @Test
    void getEventsFilteredByToDate() {
        filter.setEndDate(LocalDateTime.now().plusMinutes(31));
        List<Event> eventList = Stream.of(eventDto1, eventDto2, eventDto3, eventDto4)
                .map(mapper::toEntity)
                .toList();
        when(eventRepository.findAll()).thenReturn(eventList);
        assertIterableEquals(List.of(eventDto3), service.getEventsByFilter(filter));
    }

    @Test
    void getEventsFilteredByRelatedSkillsOne() {
        filter.setRelatedSkills(eventDto1.getRelatedSkills());
        List<Event> eventList = Stream.of(eventDto1, eventDto2, eventDto3, eventDto4)
                .map(mapper::toEntity)
                .toList();
        when(eventRepository.findAll()).thenReturn(eventList);
        assertIterableEquals(List.of(eventDto1), service.getEventsByFilter(filter));
    }

    @Test
    void getEventsFilteredByRelatedSkillsSeveral() {
        filter.setRelatedSkills(List.of(eventDto1.getRelatedSkills().get(0)));
        List<Event> eventList = Stream.of(eventDto1, eventDto2, eventDto3, eventDto4)
                .map(mapper::toEntity)
                .toList();
        when(eventRepository.findAll()).thenReturn(eventList);
        assertArrayEquals(List.of(eventDto1, eventDto2, eventDto4).toArray(), service.getEventsByFilter(filter).toArray());
    }

    @Test
    void getEventsFilteredByLocation() {
        filter.setLocation(eventDto1.getLocation());
        List<Event> eventList = Stream.of(eventDto1, eventDto2, eventDto3, eventDto4)
                .map(mapper::toEntity)
                .toList();
        when(eventRepository.findAll()).thenReturn(eventList);
        assertIterableEquals(List.of(eventDto1), service.getEventsByFilter(filter));
    }

    @Test
    void getEventsFilteredByStatus() {
        filter.setStatus(EventStatus.IN_PROGRESS);
        List<Event> eventList = Stream.of(eventDto1, eventDto2, eventDto3, eventDto4)
                .map(mapper::toEntity)
                .peek(event -> event.setStatus(EventStatus.PLANNED))
                .toList();
        eventList.get(0).setStatus(EventStatus.IN_PROGRESS);
        when(eventRepository.findAll()).thenReturn(eventList);
        assertIterableEquals(List.of(eventDto1), service.getEventsByFilter(filter));
    }

    @Test
    void getEventsFilteredByType() {
        filter.setType(EventType.GIVEAWAY);
        List<Event> eventList = Stream.of(eventDto1, eventDto2, eventDto3, eventDto4)
                .map(mapper::toEntity)
                .peek(event -> event.setType(EventType.POLL))
                .toList();
        eventList.get(0).setType(EventType.GIVEAWAY);
        when(eventRepository.findAll()).thenReturn(eventList);
        assertIterableEquals(List.of(eventDto1), service.getEventsByFilter(filter));
    }

    @Test
    void getEventsFilteredByMultiplyFilters() {
        filter.setType(EventType.GIVEAWAY);
        filter.setStartDate(eventDto2.getStartDate());
        filter.setEndDate(eventDto4.getEndDate());
        List<Event> eventList = Stream.of(eventDto1, eventDto2, eventDto3, eventDto4)
                .map(mapper::toEntity)
                .peek(event -> event.setType(EventType.POLL))
                .toList();
        eventList.get(0).setType(EventType.GIVEAWAY);
        when(eventRepository.findAll()).thenReturn(eventList);
        assertIterableEquals(List.of(eventDto1), service.getEventsByFilter(filter));
    }

    @Test
    void getEventsFilteredByEmptyFilter() {
        List<Event> eventList = Stream.of(eventDto1, eventDto2, eventDto3, eventDto4)
                .map(mapper::toEntity)
                .toList();
        when(eventRepository.findAll()).thenReturn(eventList);
        assertArrayEquals(new EventDto[]{eventDto1, eventDto2, eventDto3, eventDto4}, service.getEventsByFilter(filter).toArray());
    }

    @Test
    void deleteNonExistingEvent() {
        EventNotFoundException e = assertThrows(EventNotFoundException.class, () -> service.deleteEvent(-1L));
        assertEquals("cannot find event with id=-1", e.getMessage());
    }

    @Test
    void deleteExistingEvent() {
        Event eventEntity = mapper.toEntity(eventDto1);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(eventEntity));
        assertEquals(eventDto1, service.deleteEvent(1L));
        verify(eventRepository, times(1)).deleteById(1L);
    }

    @Test
    void updateNoOwnerEvent() {
        eventDto1.setOwnerId(-1L);
        DataValidationException e = assertThrows(DataValidationException.class, () -> service.create(eventDto1));
        assertEquals("owner with id=-1 not found", e.getMessage());
    }

    @Test
    void updateOwnerHasNoEnoughSkills() {
        owner1.setSkills(new ArrayList<>());
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner1));
        DataValidationException e = assertThrows(DataValidationException.class, () -> service.updateEvent(eventDto1));
        assertEquals("user with id=1 has no enough skills to update event", e.getMessage());
    }

    @Test
    void updateGoodEvent() {
        Event eventEntity = mapper.toEntity(eventDto1);
        owner1.setSkills(eventEntity.getRelatedSkills());
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner1));
        when(eventRepository.save(eventEntity)).thenReturn(eventEntity);
        assertEquals(eventDto1, service.updateEvent(eventDto1));
    }

    @Test
    void getOwnedEvents() {
        Event eventEntity = mapper.toEntity(eventDto1);
        when(eventRepository.findAllByUserId(1L)).thenReturn(List.of(eventEntity));
        assertIterableEquals(List.of(eventDto1), service.getOwnedEvents(1L));
    }

    @Test
    void getOwnedEventsNonExistingUserId() {
        assertIterableEquals(new ArrayList<>(), service.getOwnedEvents(1L));
    }

    @Test
    void getParticipatedEvents() {
        Event eventEntity = mapper.toEntity(eventDto1);
        when(eventRepository.findParticipatedEventsByUserId(1L)).thenReturn(List.of(eventEntity));
        assertIterableEquals(List.of(eventDto1), service.getParticipatedEvents(1L));
    }

    @Test
    void getParticipatedEventsNonExistingUserId() {
        assertIterableEquals(new ArrayList<>(), service.getParticipatedEvents(1L));
    }
}