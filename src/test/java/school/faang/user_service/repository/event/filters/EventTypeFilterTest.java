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
import school.faang.user_service.entity.event.EventType;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventTypeFilterTest {
    @Mock
    private Event event;
    @Mock
    private EventFilterDto filter;
    @InjectMocks
    private EventTypeFilter eventTypeFilter;


    @BeforeEach
    void setUp() {
        eventTypeFilter = new EventTypeFilter();
    }

    @Nested
    class PositiveTests {
        @Test
        public void testIsApplicable_Success() {
            when(filter.getTypePattern()).thenReturn("meeting");

            boolean result = eventTypeFilter.isApplicable(filter);
            assertTrue(result);

            verify(filter, atLeastOnce()).getTypePattern();
        }

        @Test
        public void testApplyFilter_Success() {
            when(event.getType()).thenReturn(EventType.MEETING);
            when(filter.getTypePattern()).thenReturn("meeting");

            boolean result = eventTypeFilter.applyFilter(event, filter);
            assertTrue(result);

            verify(event, atLeastOnce()).getType();
            verify(filter, atLeastOnce()).getTypePattern();
        }
    }

    @Nested
    class NegativeTest {
        @Test
        public void testIsApplicable_NullPattern_AssertFalse() {
            filter.setTypePattern(null);

            assertFalse(eventTypeFilter.isApplicable(filter));
        }

        @Test
        public void testIsApplicable_BlankPattern_AssertFalse() {
            filter.setTypePattern(" ");

            assertFalse(eventTypeFilter.isApplicable(filter));
        }

        @Test
        public void testApplyFilter_NoMatch_AssertFalse() {
            when(event.getType()).thenReturn(EventType.MEETING);
            when(filter.getTypePattern()).thenReturn("not-meeting");

            boolean result = eventTypeFilter.applyFilter(event, filter);
            assertFalse(result);

            verify(event, atLeastOnce()).getType();
            verify(filter, atLeastOnce()).getTypePattern();
        }
    }
}
