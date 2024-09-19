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
class EventDescriptionFilterTest {
    @Mock
    private Event event;
    @Mock
    private EventFilterDto filter;
    @InjectMocks
    private EventDescriptionFilter eventDescriptionFilter;

    @BeforeEach
    void setUp() {
        eventDescriptionFilter = new EventDescriptionFilter();
    }

    @Nested
    class PositiveTests {
        @Test
        public void testIsApplicable_Success() {
            when(filter.getDescriptionPattern()).thenReturn("test");

            boolean result = eventDescriptionFilter.isApplicable(filter);
            assertTrue(result);

            verify(filter, atLeastOnce()).getDescriptionPattern();
        }

        @Test
        public void testApplyFilter_Success() {
            when(event.getDescription()).thenReturn("test");
            when(filter.getDescriptionPattern()).thenReturn("test");

            boolean result = eventDescriptionFilter.applyFilter(event, filter);
            assertTrue(result);

            verify(event, atLeastOnce()).getDescription();
            verify(filter, atLeastOnce()).getDescriptionPattern();
        }
    }

    @Nested
    class NegativeTest {
        @Test
        public void testIsApplicable_NullPattern_AssertFalse() {
            filter.setDescriptionPattern(null);

            assertFalse(eventDescriptionFilter.isApplicable(filter));
        }

        @Test
        public void testIsApplicable_BlankPattern_AssertFalse() {
            filter.setDescriptionPattern(" ");

            assertFalse(eventDescriptionFilter.isApplicable(filter));
        }

        @Test
        public void testApplyFilter_NoMatch_AssertFalse() {
            when(event.getDescription()).thenReturn("test");
            when(filter.getDescriptionPattern()).thenReturn("not-test");

            boolean result = eventDescriptionFilter.applyFilter(event, filter);
            assertFalse(result);

            verify(event, atLeastOnce()).getDescription();
            verify(filter, atLeastOnce()).getDescriptionPattern();
        }
    }
}
