package school.faang.user_service.controller.event;

import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.doNothing;
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
    void createTest() {
        //before
        doNothing().when(eventController).validateEvent(eventDto);
        ArgumentCaptor<EventDto> eventDtoArgumentCaptor = ArgumentCaptor.forClass(EventDto.class);

        //when
        eventController.create(eventDto);

        //then
        verify(eventService, times(1)).create(eventDtoArgumentCaptor.capture());
        assertEquals(eventDto, eventDtoArgumentCaptor.getValue());
    }

    @Test
    void validateEventPositiveTest() {
        //when
        assertDoesNotThrow(() -> eventController.validateEvent(eventDto));
    }

    @Test
    void validateEventWithNullTitleTest() {
        //before
        eventDto.setTitle(null);

        //when
        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> eventController.validateEvent(eventDto));

        //then
        assertEquals(NULL_OR_BLANK_EVENT_TITLE_EXCEPTION.getMessage(), dataValidationException.getMessage());
    }

    @Test
    void validateEventWithBlankTitleTest() {
        //before
        eventDto.setTitle("  ");

        //when
        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> eventController.validateEvent(eventDto));

        //then
        assertEquals(NULL_OR_BLANK_EVENT_TITLE_EXCEPTION.getMessage(), dataValidationException.getMessage());
    }

    @Test
    void validateEventWithNullStartDateTest() {
        //before
        eventDto.setStartDate(null);

        //when
        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> eventController.validateEvent(eventDto));

        //then
        assertEquals(INVALID_EVENT_START_DATE_EXCEPTION.getMessage(), dataValidationException.getMessage());
    }

    @Test
    void validateEventWithPastStartDateTest() {
        //before
        eventDto.setStartDate(LocalDateTime.of(2023, 6, 12, 12, 12));

        //when
        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> eventController.validateEvent(eventDto));

        //then
        assertEquals(INVALID_EVENT_START_DATE_EXCEPTION.getMessage(), dataValidationException.getMessage());
    }

    @Test
    void validateEventWithNullOwnerIdTest() {
        //before
        eventDto.setOwnerId(null);

        //when
        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> eventController.validateEvent(eventDto));

        //then
        assertEquals(NULL_EVENT_OWNER_ID_EXCEPTION.getMessage(), dataValidationException.getMessage());
    }
}