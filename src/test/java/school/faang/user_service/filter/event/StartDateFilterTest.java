package school.faang.user_service.filter.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StartDateFilterTest {

    @InjectMocks
    private StartDateFilter startDateFilter;

    private final LocalDateTime startDate = LocalDateTime.of(2024, 4, 1, 12, 5);

    private EventFilterDto eventFilterDto;

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Если у EventFilterDto поле startDate не null тогда возвращаем true")
        void whenFieldNotNullThenReturnTrue() {

            eventFilterDto = eventFilterDto.builder()
                    .startDate(startDate)
                    .build();

            assertTrue(startDateFilter.isApplicable(eventFilterDto));
        }

        @Test
        @DisplayName("Если у EventFilterDto заполнено поле startDate, тогда возвращаем отфильтрованный список")
        void whenFieldFilledThenReturnFilteredList() {
            Stream<Event> eventStream = Stream.of(
                    Event.builder()
                            .startDate(startDate)
                            .build(),
                    Event.builder()
                            .startDate(LocalDateTime.of(2024, 10, 1, 12, 2))
                            .build());

            eventFilterDto = EventFilterDto.builder()
                    .startDate(startDate)
                    .build();

            Stream<Event> resultEventStream = Stream.of(
                    Event.builder()
                            .startDate(startDate)
                            .build());
            assertEquals(resultEventStream.toList(), startDateFilter.apply(eventStream, eventFilterDto).toList());
        }
    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("Если у EventFilterDto поле startDate null тогда возвращаем false")
        void whenFieldNullThenReturnFalse() {

            eventFilterDto = EventFilterDto.builder()
                    .startDate(null)
                    .build();

            assertFalse(startDateFilter.isApplicable(eventFilterDto));
        }
    }
}