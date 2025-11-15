package school.faang.user_service.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.UpdateEventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.EventMapperImpl;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventServiceImpl;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.entity.event.EventStatus;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Spy
    private EventMapperImpl eventMapper;

    @InjectMocks
    private EventServiceImpl eventService;

    @Test
    void createEventTest() {
        // Arrange
        EventDto inputDto = new EventDto(
                "Test Event",
                "Some description",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now(),
                LocalDateTime.now(),
                1L,
                EventType.WEBINAR,
                EventStatus.PLANNED
        );

        Event savedEvent = new Event();
        savedEvent.setTitle("Test Event");

        when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);
        EventDto result = eventService.create(inputDto);
        verify(eventRepository).save(any(Event.class));

        assertNotNull(result);
        assertEquals(inputDto.getTitle(), result.getTitle());
    }

    @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
    @Test
    void updateEventTest() {
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
                LocalDateTime.now().minusDays(1));

        UpdateEventDto updateDto = new UpdateEventDto(
                "New title",
                "New desc",
                1L,
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(3),
                EventType.MEETING,
                EventStatus.IN_PROGRESS
        );

        EventDto expectedDto = new EventDto(
                updateDto.getTitle(),
                updateDto.getDescription(),
                updateDto.getStartDate(),
                updateDto.getEndDate(),
                existingEvent.getCreatedAt(),
                existingEvent.getUpdatedAt(),
                updateDto.getOwnerId(),
                updateDto.getEventType(),
                updateDto.getStatus()

        );

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(existingEvent));
        when(eventRepository.save(existingEvent)).thenReturn(existingEvent);

        EventDto result = eventService.update(eventId, updateDto);

        assertNotNull(result);
        assertEquals(expectedDto, result);

        verify(eventRepository).findById(eventId);
        verify(eventRepository).save(existingEvent);
    }

    @Test
    void getByFilters() {
        // TODO: реализовать метод getByFilters
    }

    @Test
    void deleteById() {

        long eventId = 42L;

        eventService.delete(eventId);

        verify(eventRepository).deleteById(eventId);
    }


}