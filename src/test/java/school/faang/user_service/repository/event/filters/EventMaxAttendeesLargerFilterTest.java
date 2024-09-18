package school.faang.user_service.repository.event.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import school.faang.user_service.dto.event.filters.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventMaxAttendeesLargerFilterTest {
    @Mock
    private Event event;
    @Mock
    private EventFilterDto filter;
    @InjectMocks
    private EventMaxAttendeesLargerFilter eventMaxAttendeesLargerFilter;

    @BeforeEach
    void setUp() {
        eventMaxAttendeesLargerFilter = new EventMaxAttendeesLargerFilter();
    }

    @Nested
    class PositiveTests {
        @Test
        public void testIsApplicable_Success() {
            when(filter.getMaxAttendeesLargerPattern()).thenReturn(10);

            boolean result = eventMaxAttendeesLargerFilter.isApplicable(filter);
            assertTrue(result);

            verify(filter).getMaxAttendeesLargerPattern();
        }

        @Test
        public void testApply_Success() {
            when(event.getMaxAttendees()).thenReturn(15);
            when(filter.getMaxAttendeesLargerPattern()).thenReturn(10);

            boolean result = eventMaxAttendeesLargerFilter.applyFilter(event, filter);
            assertTrue(result);

            verify(event, atLeastOnce()).getMaxAttendees();
            verify(filter, atLeastOnce()).getMaxAttendeesLargerPattern();
        }
    }

    @Nested
    class NegativeTest {
        @Test
        public void testIsApplicable_NullPattern_AssertFalse() {
            filter.setMaxAttendeesLargerPattern(0);

            assertFalse(eventMaxAttendeesLargerFilter.isApplicable(filter));
        }

        @Test
        void testApplyFilter_LessThanCriteria_AssertFalse() {
            when(event.getMaxAttendees()).thenReturn(5);
            when(filter.getMaxAttendeesLargerPattern()).thenReturn(10);

            boolean result = eventMaxAttendeesLargerFilter.applyFilter(event, filter);
            assertFalse(result);

            verify(event).getMaxAttendees();
            verify(filter).getMaxAttendeesLargerPattern();
        }
    }
}
