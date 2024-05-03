package school.faang.user_service.controller.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static school.faang.user_service.exception.ExceptionMessage.INVALID_EVENT_START_DATE_EXCEPTION;
import static school.faang.user_service.exception.ExceptionMessage.NULL_EVENT_OWNER_ID_EXCEPTION;
import static school.faang.user_service.exception.ExceptionMessage.NULL_OR_BLANK_EVENT_TITLE_EXCEPTION;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {
    @Spy
    @InjectMocks
    EventController eventController;

    @Mock
    EventService eventService;

    EventDto eventDto;

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

    @Test
    void createEventPositiveTest() {
        ArgumentCaptor<EventDto> eventDtoArgumentCaptor = ArgumentCaptor.forClass(EventDto.class);

        assertDoesNotThrow(() -> eventController.create(eventDto));

        verify(eventService).create(eventDtoArgumentCaptor.capture());
        assertEquals(eventDto, eventDtoArgumentCaptor.getValue());
    }

    @DisplayName("Should throw exception instead of creating event with null title")
    @Test
    void shouldThrowExceptionWhenCreateEventWithNullTitleTest() {
        eventDto.setTitle(null);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> eventController.create(eventDto));

        verify(eventService, times(0)).create(eventDto);
        assertEquals(NULL_OR_BLANK_EVENT_TITLE_EXCEPTION.getMessage(), exception.getMessage());
    }

    @DisplayName("Should throw exception instead of creating event with blank title")
    @Test
    void shouldThrowExceptionWhenCreateEventWithBlankTitleTest() {
        eventDto.setTitle("   ");

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> eventController.create(eventDto));

        verify(eventService, times(0)).create(eventDto);
        assertEquals(NULL_OR_BLANK_EVENT_TITLE_EXCEPTION.getMessage(), exception.getMessage());
    }

    @DisplayName("Should throw exception instead of creating event with null start date")
    @Test
    void shouldThrowExceptionWhenCreateEventWithNullStartDateTest() {
        eventDto.setStartDate(null);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> eventController.create(eventDto));

        verify(eventService, times(0)).create(eventDto);
        assertEquals(INVALID_EVENT_START_DATE_EXCEPTION.getMessage(), exception.getMessage());
    }

    @DisplayName("Should throw exception instead of creating event with past start date")
    @Test
    void shouldThrowExceptionWhenCreateEventWithPastStartDateTest() {
        eventDto.setStartDate(LocalDateTime.of(2023, 6, 12, 12, 12));

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> eventController.create(eventDto));

        verify(eventService, times(0)).create(eventDto);
        assertEquals(INVALID_EVENT_START_DATE_EXCEPTION.getMessage(), exception.getMessage());
    }

    @DisplayName("Should throw exception instead of creating event with nul owner id")
    @Test
    void shouldThrowExceptionWhenCreateEventWithOwnerIdTest() {
        eventDto.setOwnerId(null);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> eventController.create(eventDto));

        verify(eventService, times(0)).create(eventDto);
        assertEquals(NULL_EVENT_OWNER_ID_EXCEPTION.getMessage(), exception.getMessage());
    }
}