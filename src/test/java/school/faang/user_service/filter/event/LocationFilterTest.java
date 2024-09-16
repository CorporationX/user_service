package school.faang.user_service.filter.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LocationFilterTest {

    @InjectMocks
    private LocationFilter locationFilter;

    private final String location = "title";

    private EventFilterDto eventFilterDto;

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Если у EventFilterDto поле location не null и не пустое, тогда возвращаем true")
        void whenFieldValidThenReturnTrue() {

            eventFilterDto = EventFilterDto.builder()
                    .location(location)
                    .build();

            assertTrue(locationFilter.isApplicable(eventFilterDto));
        }

        @Test
        @DisplayName("Если у EventFilterDto заполнено поле location, тогда возвращаем отфильтрованный список")
        void whenFieldFilledThenReturnFilteredList() {
            Stream<Event> eventStream = Stream.of(
                    Event.builder()
                            .location(location)
                            .build(),
                    Event.builder()
                            .location("test")
                            .build());

            eventFilterDto = EventFilterDto.builder()
                    .location(location)
                    .build();

            Stream<Event> resultEventStream = Stream.of(
                    Event.builder()
                            .location(location)
                            .build());
            assertEquals(resultEventStream.toList(), locationFilter.apply(eventStream, eventFilterDto).toList());
        }

    }

    @Nested
    class NegativeTests {

            @Test
            @DisplayName("Если у EventFilterDto поле location null тогда возвращаем false")
            void whenFieldNullThenReturnFalse() {

                eventFilterDto = EventFilterDto.builder()
                        .location(null)
                        .build();

                assertFalse(locationFilter.isApplicable(eventFilterDto));
            }

            @Test
            @DisplayName("Если у EventFilterDto поле location пустое, тогда возвращаем false")
            void whenFielIsEmptyThenReturnFalse() {
                eventFilterDto = EventFilterDto.builder()
                        .location("   ")
                        .build();

                assertFalse(locationFilter.isApplicable(eventFilterDto));
            }
    }
}