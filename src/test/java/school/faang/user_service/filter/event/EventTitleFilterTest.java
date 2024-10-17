package school.faang.user_service.filter.event;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EventTitleFilterTest {
    private final EventTitleFilter eventTitleFilter = new EventTitleFilter();

    @Test
    public void testIsApplicable_TitlePatternPresent() {
        EventFilterDto filterDto = new EventFilterDto();
        filterDto.setTitlePattern("Some Title");

        assertTrue(eventTitleFilter.isApplicable(filterDto),
                "Filter should be applicable when titlePattern is present");
    }

    @Test
    public void testIsApplicable_TitlePatternAbsent() {
        EventFilterDto filterDto = new EventFilterDto();
        filterDto.setTitlePattern(null);

        assertFalse(eventTitleFilter.isApplicable(filterDto),
                "Filter should not be applicable when titlePattern is absent");
    }


    @Test
    public void testApply_FilterByTitle() {
        EventFilterDto filterDto = new EventFilterDto();
        filterDto.setTitlePattern("Event");

        Event event1 = new Event();
        event1.setTitle("Event One");

        Event event2 = new Event();
        event2.setTitle("Another Event");

        Event event3 = new Event();
        event3.setTitle("Unrelated");

        Stream<Event> events = Stream.of(event1, event2, event3);

        List<Event> result = eventTitleFilter.apply(events, filterDto).toList();

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
        filterDto.setTitlePattern("Event");

        Root<Event> root = Mockito.mock(Root.class);

        CriteriaQuery<?> query = Mockito.mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder = Mockito.mock(CriteriaBuilder.class);

        when(criteriaBuilder.lower(root.get("title"))).thenReturn(Mockito.mock(Expression.class));
        when(criteriaBuilder.like(Mockito.any(Expression.class), Mockito.anyString()))
                .thenReturn(Mockito.mock(Predicate.class));

        Specification<Event> specification = eventTitleFilter.toSpecification(filterDto);
        assertNotNull(specification);

        specification.toPredicate(root, query, criteriaBuilder);

        verify(criteriaBuilder).lower(root.get("title"));
        verify(criteriaBuilder).like(Mockito.any(Expression.class), eq("%event%"));
    }
}
