package school.faang.user_service.repository.event.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.filters.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventEndDateBeforeFilterTest {
    @Mock
    private Event event;
    @Mock
    private EventFilterDto filter;
    @InjectMocks
    private EventEndDateBeforeFilter eventEndDateBeforeFilter;

    @BeforeEach
    void setUp() {
        eventEndDateBeforeFilter = new EventEndDateBeforeFilter();
    }

    @Nested
    class PositiveTests {
        @Test
        public void testIsApplicable_Success() {
            when(filter.getEndDateBeforePattern()).thenReturn(LocalDateTime.now().plusDays(1));

            boolean result = eventEndDateBeforeFilter.isApplicable(filter);
            assertTrue(result);

            verify(filter, atLeastOnce()).getEndDateBeforePattern();
        }

        @Test
        public void testApplyFilter_Success() {
            when(event.getEndDate()).thenReturn(LocalDateTime.now().plusDays(1));
            when(filter.getEndDateBeforePattern()).thenReturn(LocalDateTime.now().plusDays(2));

            boolean result = eventEndDateBeforeFilter.applyFilter(event, filter);
            assertTrue(result);

            verify(event, atLeastOnce()).getEndDate();
            verify(filter, atLeastOnce()).getEndDateBeforePattern();
        }
    }

    @Nested
    class NegativeTest {
        @Test
        public void testIsApplicable_NullPattern_AssertFalse() {
            filter.setEndDateBeforePattern(null);

            assertFalse(eventEndDateBeforeFilter.isApplicable(filter));
        }

        @Test
        public void testApplyFilter_AfterDate_AssertFalse() {
            when(event.getEndDate()).thenReturn(LocalDateTime.now().plusDays(1));
            when(filter.getEndDateBeforePattern()).thenReturn(LocalDateTime.now().plusHours(1));

            boolean result = eventEndDateBeforeFilter.applyFilter(event, filter);
            assertFalse(result);

            verify(event, atLeastOnce()).getEndDate();
            verify(filter, atLeastOnce()).getEndDateBeforePattern();
        }
    }
}
