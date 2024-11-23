package school.faang.user_service.service.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.event.EventController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.mapper.event.EventMapperImpl;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {
    @InjectMocks
    private EventController eventController;
    @Mock
    private EventService eventService;

    @Captor
    private ArgumentCaptor<Long> argumentCaptor;
    @Captor
    private ArgumentCaptor<EventFilterDto> eventFilterCaptor;
    @Captor
    private ArgumentCaptor<EventDto> eventDtoCaptor;


    @Spy
    private EventMapper eventMapper = Mappers.getMapper(EventMapperImpl.class);


    EventDto eventDto;
    List<EventDto> eventDtos;
    long userId = 5;

    @BeforeEach
    public void setupEventDto() {
        eventDto = new EventDto();
        eventDto.setTitle("title");
        eventDto.setDescription("description");
        eventDto.setId(1L);
        eventDto.setOwnerId(2L);
        eventDto.setStartDate(LocalDateTime.of(2024, 11, 21, 10, 30));
    }

    @BeforeEach
    public void setupEventDtoList() {
        eventDtos = new ArrayList<>();
        EventDto firstEventDto = new EventDto();
        EventDto secondEventDto = new EventDto();
        firstEventDto.setTitle("firstTitle");
        secondEventDto.setTitle("secondTitle");
        eventDtos.add(firstEventDto);
        eventDtos.add(secondEventDto);
    }

    @Test
    public void testCreateWithNoEvent(EventDto event) {
        eventDto = null;
        assertThrows(DataValidationException.class, () -> eventController.create(eventDto));

    }

    @Test
    public void testCreateWithBlankTitle(EventDto event) {
        eventDto.setTitle(" ");
        assertThrows(DataValidationException.class, () -> eventController.create(eventDto));

    }

    @Test
    public void testCreateWithNoStartDate(EventDto event) {
        eventDto.setStartDate(null);
        assertThrows(DataValidationException.class, () -> eventController.create(eventDto));

    }

    @Test
    public void testCreateWithNoOwnerId(EventDto event) {
        eventDto.setOwnerId(null);
        assertThrows(DataValidationException.class, () -> eventController.create(eventDto));

    }

    @Test
    public void testCreateEvent() {

        when(eventService.create(eventDto)).thenReturn(eventDto);
        EventDto result = eventController.create(eventDto);
        verify(eventService, times(1)).create(eventDtoCaptor.capture());
        EventDto capturedEventDto = eventDtoCaptor.getValue();
        assertEquals(eventDto.getId(), capturedEventDto.getId());
        assertEquals(eventDto.getTitle(), result.getTitle());
        assertEquals(eventDto.getDescription(), result.getDescription());

    }


    @Test
    public void testGetEvent() {
        when(eventService.getEvent(eventDto.getId())).thenReturn(eventDto);
        EventDto result = eventController.getEvent(eventDto.getId());
        verify(eventService, times(1)).getEvent(argumentCaptor.capture());
        Long capturedEventId = argumentCaptor.getValue();
        assertEquals(eventDto.getId(), capturedEventId);
        assertEquals(eventDto.getTitle(), result.getTitle());
        assertEquals(eventDto.getDescription(), result.getDescription());

    }

    @Test
    public void testGetEventsByFilter() {
        EventFilterDto eventFilterDto = new EventFilterDto();
        when(eventService.getEventsByFilter(eventFilterDto)).thenReturn(eventDtos);
        List<EventDto> resultList = eventController.getEventsByFilter(eventFilterDto);
        verify(eventService, times(1)).getEventsByFilter(eventFilterCaptor.capture());
        EventFilterDto capturedFilter = eventFilterCaptor.getValue();
        assertEquals(eventFilterDto, capturedFilter);
        assertEquals(eventDtos.stream().map(event -> event.getTitle()),
                resultList.stream().map(event -> event.getTitle()));

    }

    @Test
    public void testDeleteEvent() {
        when(eventService.deleteEvent(eventDto.getId())).thenReturn(eventDto.getId());
        Long result = eventController.deleteEvent(eventDto.getId());
        verify(eventService, times(1)).deleteEvent(argumentCaptor.capture());
        Long capturedEventId = argumentCaptor.getValue();
        assertEquals(eventDto.getId(), capturedEventId);
        assertEquals(eventDto.getId(), result);

    }

    @Test
    public void testUpdateEvent() {
        when(eventService.updateEvent(eventDto)).thenReturn(eventDto);
        EventDto result = eventController.updateEvent(eventDto);
        verify(eventService, times(1)).updateEvent(eventDtoCaptor.capture());
        EventDto capturedEventDto = eventDtoCaptor.getValue();
        assertEquals(eventDto.getId(), capturedEventDto.getId());
        assertEquals(eventDto.getTitle(), result.getTitle());
        assertEquals(eventDto.getDescription(), result.getDescription());

    }

    @Test
    public void testGetOwnedEvents() {
        when(eventService.getOwnedEvents(userId)).thenReturn(eventDtos);
        List<EventDto> resultList = eventController.getOwnedEvents(userId);
        verify(eventService, times(1)).getOwnedEvents(argumentCaptor.capture());
        long capturedUserId = argumentCaptor.getValue();
        assertEquals(userId, capturedUserId);
        assertEquals(eventDtos, resultList);

    }

    @Test
    public void testGetParticipatedEvents() {
        when(eventService.getParticipatedEvents(userId)).thenReturn(eventDtos);
        List<EventDto> resultList = eventController.getParticipatedEvents(userId);
        verify(eventService, times(1)).getParticipatedEvents(argumentCaptor.capture());
        long capturedUserId = argumentCaptor.getValue();
        assertEquals(userId, capturedUserId);
        assertEquals(eventDtos, resultList);

    }


}
