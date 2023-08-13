package school.faang.user_service.service.eventTest.filtersTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.event.LocationFilter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class LocationFilterTest {
    private LocationFilter locationFilter;

    @BeforeEach
    void setUp() {
        locationFilter = new LocationFilter();
    }

    @ParameterizedTest
    @CsvSource({
            "New York, true",
            ", false",
            " , false",
            "     , false",
            "  city   , true"
    })
    void testIsApplicable(String location, boolean expectedResult) {
        EventFilterDto filterDto = new EventFilterDto();
        filterDto.setLocation(location);
        Assertions.assertEquals(expectedResult, locationFilter.isApplicable(filterDto));
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void testApplyFilter(List<Event> events) {
        EventFilterDto filterDto = new EventFilterDto();
        filterDto.setLocation("New York");

        List<Event> filteredEvents = locationFilter.applyFilter(events.stream(), filterDto)
                .collect(Collectors.toList());

        Assertions.assertEquals(events.get(0).getId(), filteredEvents.size());
        Assertions.assertTrue(filteredEvents.stream().allMatch(event -> event.getLocation().equalsIgnoreCase("New York")));
    }

    private static Stream<List<Event>> testCases() {
        return Stream.of(
                List.of(
                        Event.builder().id(2).title("Event1").description("Description1").location("New York").build(),
                        Event.builder().id(2).title("Event3").description("Description3").location("New York").build()
                ),
                List.of(
                        Event.builder().id(0).title("Event2").description("Description2").location("London").build()
                ),
                List.of(
                        Event.builder().id(0).title("Event4").description("Description4").location("Sydney").build()
                )
        );
    }
}
