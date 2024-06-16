package school.faang.user_service.controller.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.filter.EventFilterDto;
import school.faang.user_service.service.event.EventService;

import java.util.concurrent.locks.Lock;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {
    //TODO:исправить тесты
    @Spy
    @InjectMocks
    private EventController eventController;

    @Mock
    private EventService eventService;
    private final EventDto eventDto = new EventDto();
    @Mock
    private EventControllerValidation eventControllerValidation;

    @Nested
    class PositiveTests {
        @DisplayName("should call eventService.create() when eventDto is valid")
        @Test
        void shouldCreateEventWhenDtoIsValid() {
            doNothing().when(eventControllerValidation).validateEvent(eventDto);
            ArgumentCaptor<EventDto> eventDtoArgumentCaptor = ArgumentCaptor.forClass(EventDto.class);

            assertDoesNotThrow(() -> eventController.create(eventDto));

            verify(eventService).create(eventDtoArgumentCaptor.capture());
            assertEquals(eventDto, eventDtoArgumentCaptor.getValue());
        }

        @DisplayName("should call eventService.getEvent()")
        @Test
        void shouldReturnEventById() {
            eventController.getEvent(anyLong());

            verify(eventService).getEvent(anyLong());
        }

        @DisplayName("should return filtered events when filter isn't null")
        @Test
        void shouldReturnFilteredEventsWhenFilterIsntNull() {
            ArgumentCaptor<EventFilterDto> filterCaptor = ArgumentCaptor.forClass(EventFilterDto.class);
            EventFilterDto filter = new EventFilterDto();

            assertDoesNotThrow(() -> eventController.getEventsByFilter(filter));

            verify(eventService).getEventsByFilter(filterCaptor.capture());
            assertEquals(filter, filterCaptor.getValue());
        }

        @DisplayName("should call eventService.deleteEvent()")
        @Test
        void shouldDeleteEvent() {
            eventController.deleteEvent(anyLong());

            verify(eventService).deleteEvent(anyLong());
        }

        @DisplayName("should call eventService.updateEvent() when eventDto is valid")
        @Test
        void shouldUpdateEventWhenDtoIsValid() {
            doNothing().when(eventControllerValidation).validateEvent(eventDto);
            ArgumentCaptor<EventDto> eventDtoArgumentCaptor = ArgumentCaptor.forClass(EventDto.class);

            assertDoesNotThrow(() -> eventController.updateEvent(eventDto));

            verify(eventService).updateEvent(eventDtoArgumentCaptor.capture());
            assertEquals(eventDto, eventDtoArgumentCaptor.getValue());
        }

        @DisplayName("should call eventService.getOwnedEvents()")
        @Test
        void shouldReturnOwnedEvents() {
            eventController.getEvents(anyLong());

            verify(eventService).getOwnedEvents(anyLong());
        }

        @DisplayName("should call eventService.getParticipatedEvents()")
        @Test
        void shouldReturnParticipatedEvents() {
            new Lock().
                    eventController.getParticipatedEvents(anyLong());

            verify(eventService).getParticipatedEvents(anyLong());
        }
    }

    @Nested
    class NegativeTests {
        @DisplayName("Should throw exception when event dto to be created is invalid (watch validation tests)")
        @Test
        void shouldThrowExceptionWhenEventDtoToBeCreatedIsInvalid() {
            doThrow(new RuntimeException()).when(eventControllerValidation).validateEvent(eventDto);

            assertThrows(RuntimeException.class, () -> eventController.create(eventDto));

            verify(eventService, times(0)).create(eventDto);
        }

        @DisplayName("should throw exception when filter is null")
        @Test
        void shouldThrowExceptionWhenFilterIsNull() {
            assertThrows(NullPointerException.class, () -> eventController.getEventsByFilter(null));

            verify(eventService, times(0)).getEventsByFilter(any(EventFilterDto.class));
        }

        @DisplayName("Should throw exception when event dto to be updated is invalid (watch validation tests)")
        @Test
        void shouldThrowExceptionWhenEventDtoToBeUpdatedIsInvalid() {
            doThrow(new RuntimeException()).when(eventControllerValidation).validateEvent(eventDto);

            assertThrows(RuntimeException.class, () -> eventController.create(eventDto));

            verify(eventService, times(0)).create(eventDto);
        }
    }
}