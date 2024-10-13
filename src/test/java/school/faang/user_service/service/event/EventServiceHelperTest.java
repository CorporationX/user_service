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
import school.faang.user_service.test_data.event.TestDataEvent;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceHelperTest {
    @Mock
    private EventRepository eventRepository;
    @InjectMocks
    private EventServiceHelper eventServiceHelper;

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
            assertDoesNotThrow(() -> eventServiceHelper.eventDatesValidation(eventDto));
        }


        @Test
        public void testRelatedSkillsValidation_Success() {
            HashSet<SkillDto> ownerSkills = new HashSet<>(eventDto.getRelatedSkills());

            assertDoesNotThrow(() -> eventServiceHelper.relatedSkillsValidation(eventDto, ownerSkills));
        }

        @Test
        public void testEventExistByIdValidation_Success() {
            when(eventRepository.existsById(eventDto.getId())).thenReturn(true);

            assertDoesNotThrow(() -> eventServiceHelper.eventExistByIdValidation(eventDto.getId()));
        }

        @Test
        void testAsyncDeletePastEvents_Success() {
            List<Long> sublistPastEventsIds = List.of(1L, 2L, 103L, 44L, 555L);

            eventServiceHelper.asyncDeletePastEvents(sublistPastEventsIds);

            verify(eventRepository, atLeastOnce()).deleteAllByIdInBatch(sublistPastEventsIds);
        }

        @Test
        void testBatchDeletePastEvents_Success() {
            List<Long> sublistPastEventsIds = List.of(1L, 2L, 103L, 44L, 555L);

            eventServiceHelper.batchDeletePastEvents(sublistPastEventsIds);

            verify(eventRepository, atLeastOnce()).deleteAllByIdInBatch(sublistPastEventsIds);
        }
    }

    @Nested
    class NegativeTest {
        @Test
        void testCreateEvent_startDateAfterEndDate_throwDataValidationException() {
            eventDto.setStartDate(LocalDateTime.now().plusDays(2));
            eventDto.setEndDate(LocalDateTime.now().plusDays(1));

            var exception = assertThrows(DataValidationException.class,
                    () -> eventServiceHelper.eventDatesValidation(eventDto)
            );

            assertEquals("Start_date cannot be after end_date.", exception.getMessage());
        }

        @Test
        void testCreateEvent_invalidSkills_throwDataValidationException() {
            SkillDto invalidSkillDto = testDataEvent.getInvalidSkillDto();
            Set<SkillDto> invalidSkillDtoList = Set.of(invalidSkillDto);

            var exception = assertThrows(DataValidationException.class,
                    () -> eventServiceHelper.relatedSkillsValidation(eventDto, invalidSkillDtoList)
            );

            assertEquals("Owner must have valid skills.", exception.getMessage());
        }

        @Test
        public void testEventExistByIdValidation_NotFound_throwDataValidationException() {
            when(eventRepository.existsById(eventDto.getId())).thenReturn(false);

            var exception = assertThrows(DataValidationException.class,
                    () -> eventServiceHelper.eventExistByIdValidation(eventDto.getId())
            );

            assertEquals("Event by ID: 1 dont exist.", exception.getMessage());

            verify(eventRepository, atLeastOnce()).existsById(1L);
        }
    }
}
