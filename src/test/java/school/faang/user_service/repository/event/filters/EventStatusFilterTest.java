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
import school.faang.user_service.entity.event.EventStatus;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class EventStatusFilterTest {
    @Mock
    private Event event;
    @Mock
    private EventFilterDto filter;
    @InjectMocks
    private EventStatusFilter eventStatusFilter;


    @BeforeEach
    void setUp() {
        eventStatusFilter = new EventStatusFilter();
    }

    @Nested
    class PositiveTests {
        @Test
        public void testIsApplicable_Success() {
            when(filter.getStatusPattern()).thenReturn("planned");

            boolean result = eventStatusFilter.isApplicable(filter);
            assertTrue(result);

            verify(filter, atLeastOnce()).getStatusPattern();
        }

        @Test
        public void testApplyFilter_Success() {
            when(event.getStatus()).thenReturn(EventStatus.PLANNED);
            when(filter.getStatusPattern()).thenReturn("planned");

            boolean result = eventStatusFilter.applyFilter(event, filter);
            assertTrue(result);

            verify(event, atLeastOnce()).getStatus();
            verify(filter, atLeastOnce()).getStatusPattern();
        }
    }

    @Nested
    class NegativeTest {
        @Test
        public void testIsApplicable_NullPattern_AssertFalse() {
            filter.setStatusPattern(null);

            assertFalse(eventStatusFilter.isApplicable(filter));
        }

        @Test
        public void testIsApplicable_BlankPattern_AssertFalse() {
            filter.setStatusPattern(" ");

            assertFalse(eventStatusFilter.isApplicable(filter));
        }

        @Test
        public void testApplyFilter_NoMatch_AssertFalse() {
            when(event.getStatus()).thenReturn(EventStatus.PLANNED);
            when(filter.getStatusPattern()).thenReturn("not-planned");

            boolean result = eventStatusFilter.applyFilter(event, filter);
            assertFalse(result);

            verify(event, atLeastOnce()).getStatus();
            verify(filter, atLeastOnce()).getStatusPattern();
        }
    }
}
