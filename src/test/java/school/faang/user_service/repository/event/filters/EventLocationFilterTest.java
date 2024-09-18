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
@SpringBootTest
@ActiveProfiles("test")
class EventLocationFilterTest {
    @Mock
    private Event event;
    @Mock
    private EventFilterDto filter;
    @InjectMocks
    private EventLocationFilter eventLocationFilter;

    @BeforeEach
    void setUp() {
        eventLocationFilter = new EventLocationFilter();
    }

    @Nested
    class PositiveTests {
        @Test
        public void testIsApplicable_Success() {
            when(filter.getLocationPattern()).thenReturn("test");

            boolean result = eventLocationFilter.isApplicable(filter);
            assertTrue(result);

            verify(filter, atLeastOnce()).getLocationPattern();
        }

        @Test
        public void testApplyFilter_Success() {
            when(event.getLocation()).thenReturn("test");
            when(filter.getLocationPattern()).thenReturn("test");

            boolean result = eventLocationFilter.applyFilter(event, filter);
            assertTrue(result);

            verify(event, atLeastOnce()).getLocation();
            verify(filter, atLeastOnce()).getLocationPattern();
        }
    }

    @Nested
    class NegativeTest {
        @Test
        public void testIsApplicable_NullPattern_AssertFalse() {
            filter.setLocationPattern(null);

            assertFalse(eventLocationFilter.isApplicable(filter));
        }

        @Test
        public void testIsApplicable_BlankPattern_AssertFalse() {
            filter.setLocationPattern(" ");

            assertFalse(eventLocationFilter.isApplicable(filter));
        }

        @Test
        public void testApplyFilter_NoMatch_AssertFalse() {
            when(event.getLocation()).thenReturn("test");
            when(filter.getLocationPattern()).thenReturn("not-test");

            boolean result = eventLocationFilter.applyFilter(event, filter);
            assertFalse(result);

            verify(event, atLeastOnce()).getLocation();
            verify(filter, atLeastOnce()).getLocationPattern();
        }
    }
}
