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

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class EventStartDateAfterFilterTest {
    @Mock
    private Event event;
    @Mock
    private EventFilterDto filter;
    @InjectMocks
    private EventStartDateAfterFilter eventStartDateAfterFilter;

    @BeforeEach
    void setUp() {
        eventStartDateAfterFilter = new EventStartDateAfterFilter();
    }

    @Nested
    class PositiveTests {
        @Test
        public void testIsApplicable_Success() {
            when(filter.getStartDateAfterPattern()).thenReturn(LocalDateTime.now().plusDays(1));

            boolean result = eventStartDateAfterFilter.isApplicable(filter);
            assertTrue(result);

            verify(filter, atLeastOnce()).getStartDateAfterPattern();
        }

        @Test
        public void testApplyFilter_Success() {
            when(event.getStartDate()).thenReturn(LocalDateTime.now().plusDays(2));
            when(filter.getStartDateAfterPattern()).thenReturn(LocalDateTime.now().plusDays(1));

            boolean result = eventStartDateAfterFilter.applyFilter(event, filter);
            assertTrue(result);

            verify(event, atLeastOnce()).getStartDate();
            verify(filter, atLeastOnce()).getStartDateAfterPattern();
        }
    }

    @Nested
    class NegativeTest {
        @Test
        public void testIsApplicable_NullPattern_assertFalse() {
            filter.setStartDateAfterPattern(null);

            assertFalse(eventStartDateAfterFilter.isApplicable(filter));
        }

        @Test
        public void testApplyFilter_beforeDate_assertFalse() {
            when(event.getStartDate()).thenReturn(LocalDateTime.now().plusDays(1));
            when(filter.getStartDateAfterPattern()).thenReturn(LocalDateTime.now().plusDays(2));

            boolean result = eventStartDateAfterFilter.applyFilter(event, filter);
            assertFalse(result);

            verify(event, atLeastOnce()).getStartDate();
            verify(filter, atLeastOnce()).getStartDateAfterPattern();
        }
    }
}
