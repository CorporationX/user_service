package school.faang.user_service.filter.event;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EventStartEndDateFilterTest {
    @InjectMocks
    private EventStartEndDateFilter filter;

    private static final LocalDateTime now = LocalDateTime.of(2025, 1, 1, 0, 0);

    @Test
    public void testIsApplicableStartDateNotNullAndEndDateNotNull() {
        EventFilterDto filterDto = new EventFilterDto();
        filterDto.setStartDate(now);
        filterDto.setEndDate(now.plusDays(1));
        boolean result = filter.isApplicable(filterDto);
        assertTrue(result);
    }

    @Test
    public void testIsApplicableStartDateNotNullAndEndDateNull() {
        EventFilterDto filterDto = new EventFilterDto();
        filterDto.setStartDate(now);
        boolean result = filter.isApplicable(filterDto);
        assertTrue(result);
    }

    @Test
    public void testIsApplicableStartDateNullAndEndDateNotNull() {
        EventFilterDto filterDto = new EventFilterDto();
        filterDto.setEndDate(now);
        boolean result = filter.isApplicable(filterDto);
        assertTrue(result);
    }

    @Test
    public void testIsApplicableStartDateNullAndEndDateNull() {
        EventFilterDto filterDto = new EventFilterDto();
        boolean result = filter.isApplicable(filterDto);
        assertFalse(result);
    }

    @Test
    public void testApplyStartDateAndEndDateNotNull() {
        List<Event> events = createTestEvents();
        EventFilterDto filterDto = new EventFilterDto();
        filterDto.setStartDate(now);
        filterDto.setEndDate(now.plusDays(10));

        Stream<Event> filtered = filter.apply(events.stream(), filterDto);
        List<Event> result = filtered.toList();

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());
    }

    @Test
    public void testApplyStartDateNotNullAndEndDateNull() {
        EventFilterDto filterDto = new EventFilterDto();
        List<Event> events = createTestEvents();
        filterDto.setStartDate(now);

        Stream<Event> filtered = filter.apply(events.stream(), filterDto);
        List<Event> result = filtered.toList();

        assertEquals(2, result.size());
        List<Long> ids = result.stream()
                .map(Event::getId)
                .toList();

        assertTrue(ids.containsAll(List.of(2L, 5L)));
    }

    private List<Event> createTestEvents() {
        Event event1 = Event.builder()
                .id(1L)
                .startDate(LocalDateTime.of(2024, 12, 30, 12, 0)) // до now (на 2 дня раньше)
                .endDate(LocalDateTime.of(2025, 1, 2, 10, 0))     // после now
                .build();

        Event event2 = Event.builder()
                .id(2L)
                .startDate(LocalDateTime.of(2025, 1, 1, 0, 5))    // чуть позже now
                .endDate(LocalDateTime.of(2025, 1, 6, 14, 0))     // ближнее будущее
                .build();

        Event event3 = Event.builder()
                .id(3L)
                .startDate(LocalDateTime.of(2024, 1, 1, 8, 0))    // год назад
                .endDate(LocalDateTime.of(2024, 12, 31, 23, 59))  // чуть до now
                .build();

        Event event4 = Event.builder()
                .id(4L)
                .startDate(LocalDateTime.of(2025, 1, 1, 0, 0))    // ровно now
                .endDate(LocalDateTime.of(2025, 1, 2, 0, 0))     // чрез день
                 .build();

        Event event5 = Event.builder()
                .id(5L)
                .startDate(LocalDateTime.of(2025, 1, 31, 9, 0))   // через 30 дней
                .endDate(LocalDateTime.of(2025, 2, 10, 18, 0))    // через 40 дней
                .build();

        return List.of(event2, event4, event1, event3, event5);
    }
}