package school.faang.user_service.filter.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.Rating;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EventRateFilterTest {
    @InjectMocks
    private EventRateFilter filter;

    @Test
    public void testIsApplicableTrue() {
        EventFilterDto filterDto = new EventFilterDto();
        filterDto.setAverageRate(4L);

        assertTrue(filter.isApplicable(filterDto));
    }

    @Test
    public void testIsApplicableFalse() {
        EventFilterDto filterDto = new EventFilterDto();

        assertFalse(filter.isApplicable(filterDto));
    }

    @Test
    public void testApply() {
        EventFilterDto filterDto = new EventFilterDto();
        filterDto.setAverageRate(3L);
        Rating rating1ForEvent1 = Rating.builder().rate(3).build();
        Rating rating2ForEvent1 = Rating.builder().rate(4).build();
        Rating rating3ForEvent1 = Rating.builder().rate(5).build();

        Rating rating1ForEvent2 = Rating.builder().rate(3).build();
        Rating rating2ForEvent2 = Rating.builder().rate(4).build();

        Rating rating1ForEvent3 = Rating.builder().rate(1).build();
        Rating rating2ForEvent3 = Rating.builder().rate(2).build();

        Event event1 = Event.builder().id(1L).ratings(List.
                of(rating1ForEvent1, rating2ForEvent1, rating3ForEvent1)).build();
        Event event2 = Event.builder().id(2L).ratings(List.of(rating1ForEvent2, rating2ForEvent2)).build();
        Event event3 = Event.builder().id(3L).ratings(List.of(rating1ForEvent3, rating2ForEvent3)).build();

        List<Event> result = filter.apply(List.of(event1, event2, event3).stream(), filterDto).toList();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());

    }

    @Test
    public void testApplyEventRateFilterWithoutAnyRates() {
        EventFilterDto filterDto = new EventFilterDto();
        filterDto.setAverageRate(3L);
        Rating rating1ForEvent2 = Rating.builder().rate(3).build();
        Rating rating2ForEvent2 = Rating.builder().rate(4).build();
        Event event1 = Event.builder().id(1L).build();
        Event event2 = Event.builder().id(2L).ratings(List.of(rating1ForEvent2, rating2ForEvent2)).build();

        List<Event> result = filter.apply(List.of(event1, event2).stream(), filterDto).toList();

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());
    }

    @Test
    public void testApplyEventRateFilterWithNegativeRate() {
        EventFilterDto filterDto = new EventFilterDto();
        filterDto.setAverageRate(3L);
        Rating rating1ForEvent1 = Rating.builder().rate(3).build();
        Rating rating2ForEvent1 = Rating.builder().rate(4).build();
        Event event1 = Event.builder().id(1L).ratings(List.of(rating1ForEvent1, rating2ForEvent1)).build();
        Rating rating1ForEvent2 = Rating.builder().rate(-5).build();
        Rating rating2ForEvent2 = Rating.builder().rate(0).build();
        Event event2 = Event.builder().id(2L).ratings(List.of(rating1ForEvent2, rating2ForEvent2)).build();

        List<Event> result = filter.apply(List.of(event1, event2).stream(), filterDto).toList();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }
}