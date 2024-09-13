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
class EventStartDateBeforeFilterTest {
    @Mock
    private Event event;
    @Mock
    private EventFilterDto filter;
    @InjectMocks
    private EventStartDateBeforeFilter eventStartDateBeforeFilter;

    @BeforeEach
    void setUp() {
        eventStartDateBeforeFilter = new EventStartDateBeforeFilter();
    }

    @Nested
    class PositiveTests {
        @Test
        public void testIsApplicable_Success() {
            when(filter.getStartDateBeforePattern()).thenReturn(LocalDateTime.now().plusDays(1));

            boolean result = eventStartDateBeforeFilter.isApplicable(filter);
            assertTrue(result);

            verify(filter, atLeastOnce()).getStartDateBeforePattern();
        }

        @Test
        public void testApplyFilter_Success() {
            when(event.getStartDate()).thenReturn(LocalDateTime.now().plusDays(1));
            when(filter.getStartDateBeforePattern()).thenReturn(LocalDateTime.now().plusDays(2));

            boolean result = eventStartDateBeforeFilter.applyFilter(event, filter);
            assertTrue(result);

            verify(event, atLeastOnce()).getStartDate();
            verify(filter, atLeastOnce()).getStartDateBeforePattern();
        }
    }

    @Nested
    class NegativeTest {
        @Test
        public void testIsApplicable_NullPattern_AssertFalse() {
            filter.setStartDateBeforePattern(null);

            assertFalse(eventStartDateBeforeFilter.isApplicable(filter));
        }

        @Test
        public void testApplyFilter_afterDate_AssertFalse() {
            when(event.getStartDate()).thenReturn(LocalDateTime.now().plusDays(1));
            when(filter.getStartDateBeforePattern()).thenReturn(LocalDateTime.now().plusHours(1));

            boolean result = eventStartDateBeforeFilter.applyFilter(event, filter);
            assertFalse(result);

            verify(event, atLeastOnce()).getStartDate();
            verify(filter, atLeastOnce()).getStartDateBeforePattern();
        }
    }
}
