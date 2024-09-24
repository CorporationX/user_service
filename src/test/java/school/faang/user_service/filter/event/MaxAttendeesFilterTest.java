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
class MaxAttendeesFilterTest {

    @InjectMocks
    private MaxAttendeesFilter maxAttendeesFilter;

    private final int maxAttendees = 5;

    private EventFilterDto eventFilterDto;

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Если у EventFilterDto поле maxAttendees > 0, тогда возвращаем true")
        void whenMaxAttendeesMoreZeroThenReturnTrue() {

            eventFilterDto = EventFilterDto.builder()
                    .maxAttendees(maxAttendees)
                    .build();

            assertTrue(maxAttendeesFilter.isApplicable(eventFilterDto));
        }

        @Test
        @DisplayName("Если у EventFilterDto заполнено поле maxAttendees, тогда возвращаем отфильтрованный список")
        void whenFieldFilledThenReturnFilteredList() {
            Stream<Event> eventStream = Stream.of(
                    Event.builder()
                            .maxAttendees(maxAttendees)
                            .build(),
                    Event.builder()
                            .maxAttendees(8)
                            .build());

            eventFilterDto = EventFilterDto.builder()
                    .maxAttendees(maxAttendees)
                    .build();

            Stream<Event> resultEventStream = Stream.of(
                    Event.builder()
                            .maxAttendees(maxAttendees)
                            .build());
            assertEquals(resultEventStream.toList(), maxAttendeesFilter.apply(eventStream, eventFilterDto).toList());
        }

    }

    @Nested
    class NegativeTests {

            @Test
            @DisplayName("Если у EventFilterDto поле maxAttendees равно 0 или меньше 0, тогда возвращаем false")
            void whenFielIsEmptyThenReturnFalse() {
                eventFilterDto = EventFilterDto.builder()
                        .maxAttendees(0)
                        .build();

                assertFalse(maxAttendeesFilter.isApplicable(eventFilterDto));
            }
    }
}