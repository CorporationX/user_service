package school.faang.user_service.service.event.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.filters.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.test_data.event.TestDataEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class EventTypeFilterTest {
    @InjectMocks
    private EventTypeFilter eventTypeFilter;

    private TestDataEvent testDataEvent;
    private EventFilterDto eventFilterDto;

    @BeforeEach
    void setUp() {
        testDataEvent = new TestDataEvent();

        eventFilterDto = testDataEvent.getEventFilterDto();
    }

    @Nested
    class PositiveTests {
        @Test
        public void testIsApplicable_Success() {
            assertTrue(eventTypeFilter.isApplicable(eventFilterDto));
        }

        @Test
        public void testApply_Success_returnEvent1() {
            Event event1 = testDataEvent.getEvent();
            Event event2 = testDataEvent.getEvent2();
            List<Event> eventList = List.of(event1, event2);

            Stream<Event> result = eventTypeFilter.apply(eventList, eventFilterDto);
            List<Event> resultList = result.toList();

            String resultMsg = resultList.get(0)
                    .getType()
                    .getMessage();

            assertEquals(1, resultList.size());
            assertEquals("Meeting", resultMsg);
        }
    }

    @Nested
    class NegativeTest {
        @Test
        public void testIsApplicable_NullPattern_returnFalse() {
            eventFilterDto.setTypePattern(null);

            assertFalse(eventTypeFilter.isApplicable(eventFilterDto));
        }

        @Test
        public void testApply_NoMatch_returnEmpty() {
            List<Event> eventList = new ArrayList<>();

            Stream<Event> result = eventTypeFilter.apply(eventList, eventFilterDto);
            List<Event> resultList = result.toList();

            assertTrue(resultList.isEmpty());
        }
    }
}