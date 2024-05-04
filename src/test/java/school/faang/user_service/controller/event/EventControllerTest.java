package school.faang.user_service.controller.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.filter.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static school.faang.user_service.exception.ExceptionMessage.INVALID_EVENT_START_DATE_EXCEPTION;
import static school.faang.user_service.exception.ExceptionMessage.NULL_EVENT_FILTER_EXCEPTION;
import static school.faang.user_service.exception.ExceptionMessage.NULL_EVENT_OWNER_ID_EXCEPTION;
import static school.faang.user_service.exception.ExceptionMessage.NULL_OR_BLANK_EVENT_TITLE_EXCEPTION;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {
    @Spy
    @InjectMocks
    private EventController eventController;

    @Mock
    private EventService eventService;

    private EventDto eventDto;

    @BeforeEach
    void setUp() {
        eventDto = new EventDto();
        eventDto.setTitle("Title");
        eventDto.setStartDate(LocalDateTime.of(2024, 6, 12, 12, 12));
        eventDto.setOwnerId(1L);
        eventDto.setDescription("Description");

        var skillADto = new SkillDto();
        skillADto.setTitle("SQL");
        var skillBDto = new SkillDto();
        skillBDto.setTitle("Java");
        eventDto.setRelatedSkills(List.of(skillADto, skillBDto));
        eventDto.setLocation("Location");
        eventDto.setMaxAttendees(10);
    }

    @Nested
    class PositiveTests {
        @DisplayName("should call eventService.create() when eventDto is valid")
        @Test
        void shouldCreateEventWhenDtoIsValid() {
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
            ArgumentCaptor<EventDto> eventDtoArgumentCaptor = ArgumentCaptor.forClass(EventDto.class);

            assertDoesNotThrow(() -> eventController.updateEvent(eventDto));

            verify(eventService).updateEvent(eventDtoArgumentCaptor.capture());
            assertEquals(eventDto, eventDtoArgumentCaptor.getValue());
        }

        @DisplayName("should call eventService.getOwnedEvents()")
        @Test
        void shouldReturnOwnedEvents() {
            eventController.getOwnedEvents(anyLong());

            verify(eventService).getOwnedEvents(anyLong());
        }

        @DisplayName("should call eventService.getParticipatedEvents()")
        @Test
        void shouldReturnParticipatedEvents() {
            eventController.getParticipatedEvents(anyLong());

            verify(eventService).getParticipatedEvents(anyLong());
        }
    }

    @Nested
    class NegativeTests {
        @DisplayName("Should throw exception instead of creating event with empty title")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"", "  ", "\t", "\n"})
        void shouldThrowExceptionWhenTitleIsEmpty(String pattern) {
            eventDto.setTitle(pattern);

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> eventController.create(eventDto));

            verify(eventService, times(0)).create(eventDto);
            assertEquals(NULL_OR_BLANK_EVENT_TITLE_EXCEPTION.getMessage(), exception.getMessage());
        }

        @DisplayName("Should throw exception instead of creating event with null start date")
        @Test
        void shouldThrowExceptionWhenStartDateIsNull() {
            eventDto.setStartDate(null);

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> eventController.create(eventDto));

            verify(eventService, times(0)).create(eventDto);
            assertEquals(INVALID_EVENT_START_DATE_EXCEPTION.getMessage(), exception.getMessage());
        }

        @DisplayName("Should throw exception instead of creating event with past start date")
        @Test
        void shouldThrowExceptionWhenStartDateIsInPast() {
            eventDto.setStartDate(LocalDateTime.of(2023, 6, 12, 12, 12));

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> eventController.create(eventDto));

            verify(eventService, times(0)).create(eventDto);
            assertEquals(INVALID_EVENT_START_DATE_EXCEPTION.getMessage(), exception.getMessage());
        }

        @DisplayName("Should throw exception instead of creating event with nul owner id")
        @Test
        void shouldThrowExceptionWhenOwnerIdIsNull() {
            eventDto.setOwnerId(null);

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> eventController.create(eventDto));

            verify(eventService, times(0)).create(eventDto);
            assertEquals(NULL_EVENT_OWNER_ID_EXCEPTION.getMessage(), exception.getMessage());
        }

        @DisplayName("should throw exception when filter is null")
        @Test
        void shouldThrowExceptionWhenFilterIsNull() {
            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> eventController.getEventsByFilter(null));

            verify(eventService, times(0)).getEventsByFilter(any(EventFilterDto.class));
            assertEquals(NULL_EVENT_FILTER_EXCEPTION.getMessage(), exception.getMessage());
        }

        @DisplayName("Should throw exception instead of updating event with empty title")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"", "  ", "\t", "\n"})
        void shouldThrowExceptionWhenUpdatedEventTitleIsEmpty(String pattern) {
            eventDto.setTitle(pattern);

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> eventController.updateEvent(eventDto));

            verify(eventService, times(0)).updateEvent(eventDto);
            assertEquals(NULL_OR_BLANK_EVENT_TITLE_EXCEPTION.getMessage(), exception.getMessage());
        }

        @DisplayName("Should throw exception instead of updating event with null start date")
        @Test
        void shouldThrowExceptionWhenUpdatedEventStartDateIsNull() {
            eventDto.setStartDate(null);

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> eventController.updateEvent(eventDto));

            verify(eventService, times(0)).updateEvent(eventDto);
            assertEquals(INVALID_EVENT_START_DATE_EXCEPTION.getMessage(), exception.getMessage());
        }

        @DisplayName("Should throw exception instead of updating event with past start date")
        @Test
        void shouldThrowExceptionWhenUpdatedEventStartDateIsInPast() {
            eventDto.setStartDate(LocalDateTime.of(2023, 6, 12, 12, 12));

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> eventController.updateEvent(eventDto));

            verify(eventService, times(0)).updateEvent(eventDto);
            assertEquals(INVALID_EVENT_START_DATE_EXCEPTION.getMessage(), exception.getMessage());
        }

        @DisplayName("Should throw exception instead of updating event with nul owner id")
        @Test
        void shouldThrowExceptionWhenUpdatedEventOwnerIdIsNull() {
            eventDto.setOwnerId(null);

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> eventController.updateEvent(eventDto));

            verify(eventService, times(0)).updateEvent(eventDto);
            assertEquals(NULL_EVENT_OWNER_ID_EXCEPTION.getMessage(), exception.getMessage());
        }
    }
}