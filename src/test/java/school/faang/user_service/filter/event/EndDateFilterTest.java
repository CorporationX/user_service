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
class EndDateFilterTest {

    private final LocalDateTime endDate = LocalDateTime.of(2024, 9, 1, 12, 5);
    @InjectMocks
    private EndDateFilter endDateFilter;
    private EventFilterDto eventFilterDto;

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Если у EventFilterDto поле endDate не null тогда возвращаем true")
        void whenFieldNotNullThenReturnTrue() {

            eventFilterDto = EventFilterDto.builder()
                    .endDate(endDate.toLocalDate())
                    .build();

            assertTrue(endDateFilter.isApplicable(eventFilterDto));
        }

        @Test
        @DisplayName("Если у EventFilterDto заполнено поле endDate, тогда возвращаем отфильтрованный список")
        void whenFieldFilledThenReturnFilteredList() {
            Stream<Event> eventStream = Stream.of(
                    Event.builder()
                            .endDate(endDate)
                            .build(),
                    Event.builder()
                            .endDate(LocalDateTime.of(2024, 10, 1, 12, 2))
                            .build());

            eventFilterDto = EventFilterDto.builder()
                    .endDate(endDate.toLocalDate())
                    .build();

            Stream<Event> resultEventStream = Stream.of(
                    Event.builder()
                            .endDate(endDate)
                            .build());
            assertEquals(resultEventStream.toList(), endDateFilter.apply(eventStream, eventFilterDto).toList());
        }
    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("Если у EventFilterDto поле endDate null тогда возвращаем false")
        void whenFieldNullThenReturnFalse() {

            eventFilterDto = EventFilterDto.builder()
                    .endDate(null)
                    .build();

            assertFalse(endDateFilter.isApplicable(eventFilterDto));
        }
    }
}