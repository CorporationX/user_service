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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventTitleFilterTest {
    @Mock
    private Event event;
    @Mock
    private EventFilterDto filter;
    @InjectMocks
    private EventTitleFilter eventTitleFilter;

    @BeforeEach
    void setUp() {
        eventTitleFilter = new EventTitleFilter();
    }

    @Nested
    class PositiveTests {
        @Test
        public void testIsApplicable_Success() {
            when(filter.getTitlePattern()).thenReturn("test");

            boolean result = eventTitleFilter.isApplicable(filter);
            assertTrue(result);

            verify(filter, atLeastOnce()).getTitlePattern();
        }

        @Test
        public void testApply_Success() {
            when(event.getTitle()).thenReturn("test");
            when(filter.getTitlePattern()).thenReturn("test");

            boolean result = eventTitleFilter.applyFilter(event, filter);
            assertTrue(result);

            verify(event, atLeastOnce()).getTitle();
            verify(filter, atLeastOnce()).getTitlePattern();
        }
    }

    @Nested
    class NegativeTest {
        @Test
        public void testIsApplicable_NullPattern_AssertFalse() {
            filter.setTitlePattern(null);

            assertFalse(eventTitleFilter.isApplicable(filter));
        }

        @Test
        public void testIsApplicable_BlankPattern_AssertFalse() {
            filter.setTitlePattern(" ");

            assertFalse(eventTitleFilter.isApplicable(filter));
        }

        @Test
        public void testApply_NoMatch_AssertFalse() {
            when(event.getTitle()).thenReturn("test");
            when(filter.getTitlePattern()).thenReturn("not-test");

            boolean result = eventTitleFilter.applyFilter(event, filter);
            assertFalse(result);

            verify(event, atLeastOnce()).getTitle();
            verify(filter, atLeastOnce()).getTitlePattern();
        }
    }
}
