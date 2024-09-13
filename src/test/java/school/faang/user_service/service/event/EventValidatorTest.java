package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.test_data.event.TestDataEvent;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventValidatorTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private SkillService skillService;
    @InjectMocks
    private EventValidator eventValidator;

    private TestDataEvent testDataEvent;
    private EventDto eventDto;

    @BeforeEach
    void setUp() {
        testDataEvent = new TestDataEvent();

        eventDto = testDataEvent.getEventDto();
    }

    @Nested
    class PositiveTests {
        @Test
        public void eventDatesValidation_Success() {
            assertDoesNotThrow(() -> eventValidator.eventDatesValidation(eventDto));
        }


        @Test
        public void testRelatedSkillsValidation_Success() {
            when(skillService.getUserSkillDtoList(eventDto.getOwnerId())).thenReturn(eventDto.getRelatedSkills());

            assertDoesNotThrow(() -> eventValidator.relatedSkillsValidation(eventDto));
        }

        @Test
        public void testEventExistByIdValidation_Success() {
            when(eventRepository.existsById(eventDto.getId())).thenReturn(true);

            assertDoesNotThrow(() -> eventValidator.eventExistByIdValidation(eventDto.getId()));
        }
    }

    @Nested
    class NegativeTest {
        @Test
        void testCreateEvent_startDateAfterEndDate_throwDataValidationException() {
            eventDto.setStartDate(LocalDateTime.now().plusDays(2));
            eventDto.setEndDate(LocalDateTime.now().plusDays(1));

            var exception = assertThrows(DataValidationException.class,
                    () -> eventValidator.eventDatesValidation(eventDto)
            );

            assertEquals("Start_date cannot be after end_date.", exception.getMessage());
        }

        @Test
        void testCreateEvent_invalidSkills_throwDataValidationException() {
            SkillDto invalidSkillDto = testDataEvent.getInvalidSkillDto();
            List<SkillDto> invalidSkillDtoList = List.of(invalidSkillDto);

            when(skillService.getUserSkillDtoList(eventDto.getOwnerId())).thenReturn(invalidSkillDtoList);

            var exception = assertThrows(DataValidationException.class,
                    () -> eventValidator.relatedSkillsValidation(eventDto)
            );

            assertEquals("Owner must have valid skills.", exception.getMessage());
        }

        @Test
        public void testEventExistByIdValidation_NotFound_throwDataValidationException() {
            when(eventRepository.existsById(eventDto.getId())).thenReturn(false);

            var exception = assertThrows(DataValidationException.class,
                    () -> eventValidator.eventExistByIdValidation(eventDto.getId())
            );

            assertEquals("Event by ID: 1 dont exist.", exception.getMessage());

            verify(eventRepository, atLeastOnce()).existsById(1L);
        }
    }
}
