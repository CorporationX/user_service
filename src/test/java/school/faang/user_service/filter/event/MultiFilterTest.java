package school.faang.user_service.filter.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.filter.EventFilterDto;
import school.faang.user_service.filter.impl.DescriptionPatternFilter;
import school.faang.user_service.filter.impl.LocationPatternFilter;
import school.faang.user_service.filter.impl.OwnerPatternFilter;
import school.faang.user_service.filter.impl.TitlePatternFilter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class MultiFilterTest {
    private EventFilterDto eventFilterDto;
    private List<EventFilter> eventFilters;
    private List<Event> events;
    private int sizeFilteredListExp;
    private Event eventExpectation;

    @BeforeEach
    void setUp() {
        eventFilterDto = new EventFilterDto();
        eventFilters = List.of(new TitlePatternFilter(), new DescriptionPatternFilter(), new LocationPatternFilter(),
                new OwnerPatternFilter());
        events = prepareEvents();

        // фильтруем события по title и description
        eventFilterDto.setTitlePattern("filter");
        eventFilterDto.setDescriptionPattern("filter");

        sizeFilteredListExp = 1;
        eventExpectation = events.get(2);
    }

    @Test
    public void testMultiFilterApply() {
        List<Event> filteredList = multiApply(events, eventFilterDto);
        Event eventActual = filteredList.get(0);
        int sizeFilteredListActual = filteredList.size();

        assertEquals(sizeFilteredListExp, sizeFilteredListActual);
        assertEquals(eventExpectation, eventActual);
    }

    @Test
    public void testMultiFilterTest() {
        List<Event> filteredList = multiTest(events, eventFilterDto);
        Event eventActual = filteredList.get(0);
        int sizeFilteredListActual = filteredList.size();

        assertEquals(sizeFilteredListExp, sizeFilteredListActual);
        assertEquals(eventExpectation, eventActual);
    }

    private List<Event> multiApply(List<Event> events, EventFilterDto filters) {
        eventFilters = eventFilters.stream()
                .filter(f-> f.isApplicable(filters))
                .toList();

        for (EventFilter eventFilter : eventFilters) {
            events = eventFilter.apply(events.stream(), filters).toList();
        }
        return events;
    }

    private List<Event> multiTest(List<Event> events, EventFilterDto filters) {
        eventFilters = eventFilters.stream()
                .filter(f-> f.isApplicable(filters))
                .toList();

        return events.stream()
                .filter(e -> eventFilters.stream()
                        .allMatch(f -> f.test(e, filters)))
                .toList();
    }

    private List<Event> prepareEvents() {
        Event eventFirst = new Event();
        eventFirst.setTitle("title first");
        eventFirst.setDescription("description first filter");
        Event eventSecond = new Event();
        eventSecond.setTitle("title second filter");
        eventSecond.setDescription("description second");
        Event eventThird = new Event();
        eventThird.setTitle("title third filter");
        eventThird.setDescription("description third filter");
        Event eventFourth = new Event();
        eventFourth.setTitle("title fourth filter");
        eventFourth.setDescription("description fourth");

        return List.of(eventFirst, eventSecond, eventThird, eventFourth);
    }
}
