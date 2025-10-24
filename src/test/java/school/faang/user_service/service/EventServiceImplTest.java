package school.faang.user_service.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.UpdateEventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.mapper.EventMapperImpl;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventServiceImpl;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.entity.event.EventStatus;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventFilter filter1;

    @Mock
    private EventFilter filter2;

    @Mock
    private EventRepository eventRepository;

    @Spy
    private EventMapperImpl eventMapper;

    @InjectMocks
    private EventServiceImpl eventService;

    @Test
    void createEvent_shouldMapDtoToEntity_SaveEntity_ThenMapToDto() {
        EventDto inputDto = new EventDto(
                "Test Event",
                "Some description",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                123L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                EventType.WEBINAR,
                EventStatus.PLANNED
        );

//     eventService.create(new EventDto("Test Event",
//             "Some description",
//             LocalDateTime.now().plusDays(1),
//             LocalDateTime.now().plusDays(2),
//             123L,
//             LocalDateTime.now(),
//             LocalDateTime.now(),
//             EventType.WEBINAR,
//             EventStatus.PLANNED));

        Event mappedEvent = eventMapper.toEvent(inputDto);
        when(eventRepository.save(any(Event.class))).thenReturn(mappedEvent);
        verify(eventRepository).save(any(Event.class));

        EventDto result = eventService.create(inputDto);

        assertNotNull(result);
        assertEquals(inputDto.getTitle(), result.getTitle());
    }

    @Test
    void updateEvent_shouldMapDtoToEntity_SaveEntity_ThenMapToDto() {
        long eventId = 1L;

        Event existingEvent = new Event(
                1L,
                "Old title",
                "Old desc",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                "Room 101",
                100,
                new ArrayList<>(),
                new ArrayList<>(),
                new User(),
        new ArrayList<>(),
                EventType.WEBINAR,
                EventStatus.PLANNED,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1)
);

        UpdateEventDto updateDto = new UpdateEventDto(
                "New title",
                "New desc",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(3),
                123L,
                EventType.MEETING,
                EventStatus.IN_PROGRESS
        );

        EventDto expectedDto = new EventDto(
                updateDto.getTitle(),
                updateDto.getDescription(),
                updateDto.getStartDate(),
                updateDto.getEndDate(),
                updateDto.getOwnerId(),
                existingEvent.getCreatedAt(),
                existingEvent.getUpdatedAt(),
                updateDto.getEventType(),
                updateDto.getEventStatus()

        );

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(existingEvent));
        when(eventRepository.save(existingEvent)).thenReturn(existingEvent);
        doNothing().when(eventMapper).update(updateDto, existingEvent);
        when(eventMapper.toEventDto(existingEvent)).thenReturn(expectedDto);

        EventDto result = eventService.update(eventId, updateDto);

        assertNotNull(result);
        assertEquals(expectedDto, result);

        verify(eventRepository).findById(eventId);
        verify(eventMapper).update(updateDto, existingEvent);
        verify(eventRepository).save(existingEvent);
        verify(eventMapper).toEventDto(existingEvent);
    }

    @Test
    void getByFilters() {
        Event event1 = new Event(
                1L, "Title1", "Desc1",
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                "Location1",
                10,
                Collections.emptyList(),
                Collections.emptyList(),
                null, // owner
                Collections.emptyList(),
                EventType.WEBINAR,
                EventStatus.PLANNED,
                LocalDateTime.now().minusDays(10),
                LocalDateTime.now().minusDays(2)
        );

        Event event2 = new Event(
                2L, "Title2", "Desc2",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                "Location2",
                20,
                Collections.emptyList(),
                Collections.emptyList(),
                null, // owner
                Collections.emptyList(),
                EventType.MEETING,
                EventStatus.IN_PROGRESS,
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusDays(1)
        );

        List<Event> allEvents = Arrays.asList(event1, event2);
        when(eventRepository.findAll()).thenReturn(allEvents);


        EventFilterDto filterDto = new EventFilterDto();

        when(filter1.isApplicable(filterDto)).thenReturn(true);
        when(filter2.isApplicable(filterDto)).thenReturn(false);

        when(filter1.apply(any(Stream.class), eq(filterDto)))
                .thenAnswer(invocation -> {
                    Stream<Event> original = invocation.getArgument(0);
                    return original.filter(e -> e.getId() == 2L);
                });

        EventDto eventDto2 = new EventDto(
                "Title2", "Desc2",
                event2.getStartDate(), event2.getEndDate(),
                null, // ownerId, если есть
                event2.getCreatedAt(), event2.getUpdatedAt(),
                event2.getEventType(), event2.getStatus()
        );
        doReturn(eventDto2).when(eventMapper).toEventDto(event2);

        List<EventDto> result = eventService.getByFilters(filterDto);

        assertEquals(1, result.size());
        assertEquals(eventDto2, result.get(0));

        verify(eventRepository).findAll();
        verify(filter1).isApplicable(filterDto);
        verify(filter1).apply(any(Stream.class), eq(filterDto));
        verify(filter2).isApplicable(filterDto);
        verify(eventMapper).toEventDto(event2);
    }

    @Test
    void DeleteById() {

        long eventId = 42L;

        eventService.delete(eventId);

        verify(eventRepository).deleteById(eventId);
    }

}



