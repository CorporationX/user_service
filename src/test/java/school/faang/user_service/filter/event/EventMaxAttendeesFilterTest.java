package school.faang.user_service.filter.event;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.jpa.domain.Specification;
import school.faang.user_service.model.filter_dto.EventFilterDto;
import school.faang.user_service.model.entity.Event;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EventMaxAttendeesFilterTest {
    private final EventMaxAttendeesFilter eventMaxAttendeesFilter = new EventMaxAttendeesFilter();

    @Test
    public void testIsApplicable_MaxAttendeesPresent() {
        EventFilterDto filterDto = new EventFilterDto();
        filterDto.setMaxAttendees(100);

        assertTrue(eventMaxAttendeesFilter.isApplicable(filterDto),
                "Filter should be applicable when maxAttendees field is present");
    }

    @Test
    public void testIsApplicable_MaxAttendeesAbsent() {
        EventFilterDto filterDto = new EventFilterDto();
        filterDto.setMaxAttendees(null);

        assertFalse(eventMaxAttendeesFilter.isApplicable(filterDto),
                "Filter should not be applicable when maxAttendees field is absent");
    }

    @Test
    public void testApply_FilterByMaxAttendees() {
        EventFilterDto filterDto = new EventFilterDto();
        filterDto.setMaxAttendees(100);

        Event event1 = new Event();
        event1.setMaxAttendees(100);

        Event event2 = new Event();
        event2.setMaxAttendees(200);

        Event event3 = new Event();
        event3.setMaxAttendees(50);

        Stream<Event> events = Stream.of(event1, event2, event3);

        List<Event> result = eventMaxAttendeesFilter.apply(events, filterDto).toList();

        assertAll(
                () -> assertEquals(2, result.size(), "Only two events should match"),
                () -> assertTrue(result.contains(event1)),
                () -> assertTrue(result.contains(event2)),
                () -> assertFalse(result.contains(event3))
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testToSpecification() {
        EventFilterDto filterDto = new EventFilterDto();
        filterDto.setMaxAttendees(100);

        Root<Event> root = Mockito.mock(Root.class);

        CriteriaQuery<?> query = Mockito.mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder = Mockito.mock(CriteriaBuilder.class);

        when(criteriaBuilder.greaterThanOrEqualTo(root.get("maxAttendees"), 100))
                .thenReturn(Mockito.mock(Predicate.class));

        Specification<Event> specification = eventMaxAttendeesFilter.toSpecification(filterDto);
        assertNotNull(specification);

        specification.toPredicate(root, query, criteriaBuilder);

        verify(criteriaBuilder).greaterThanOrEqualTo(root.get("maxAttendees"), filterDto.getMaxAttendees());
    }
}
