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
class EventMaxAttendeesLowerFilterTest {
    @Mock
    private Event event;
    @Mock
    private EventFilterDto filter;
    @InjectMocks
    private EventMaxAttendeesLowerFilter eventMaxAttendeesLowerFilter;

    @BeforeEach
    void setUp() {
        eventMaxAttendeesLowerFilter = new EventMaxAttendeesLowerFilter();
    }

    @Nested
    class PositiveTests {
        @Test
        public void testIsApplicable_Success() {
            when(filter.getMaxAttendeesLowerPattern()).thenReturn(10);

            boolean result = eventMaxAttendeesLowerFilter.isApplicable(filter);
            assertTrue(result);

            verify(filter).getMaxAttendeesLowerPattern();
        }

        @Test
        public void testApply_Success() {
            when(event.getMaxAttendees()).thenReturn(15);
            when(filter.getMaxAttendeesLowerPattern()).thenReturn(20);

            boolean result = eventMaxAttendeesLowerFilter.applyFilter(event, filter);
            assertTrue(result);

            verify(event, atLeastOnce()).getMaxAttendees();
            verify(filter, atLeastOnce()).getMaxAttendeesLowerPattern();
        }
    }

    @Nested
    class NegativeTest {
        @Test
        public void testIsApplicable_NullPattern_AssertFalse() {
            filter.setMaxAttendeesLargerPattern(0);

            assertFalse(eventMaxAttendeesLowerFilter.isApplicable(filter));
        }

        @Test
        void testApplyFilter_MoreThanCriteria_AssertFalse() {
            when(event.getMaxAttendees()).thenReturn(10);
            when(filter.getMaxAttendeesLowerPattern()).thenReturn(5);

            boolean result = eventMaxAttendeesLowerFilter.applyFilter(event, filter);
            assertFalse(result);

            verify(event).getMaxAttendees();
            verify(filter).getMaxAttendeesLowerPattern();
        }
    }
}
