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
class CreatedAtFilterTest {

    @InjectMocks
    private CreatedAtFilter createdAtFilter;

    private final LocalDateTime createdAt = LocalDateTime.of(2022, 9, 1, 12, 2);

    private EventFilterDto eventFilterDto;

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Если у EventFilterDto поле createdAt не null тогда возвращаем true")
        void whenFieldNotNullThenReturnTrue() {

            eventFilterDto = EventFilterDto.builder()
                    .createdAt(createdAt.toLocalDate())
                    .build();

            assertTrue(createdAtFilter.isApplicable(eventFilterDto));
        }

        @Test
        @DisplayName("Если у EventFilterDto заполнено поле createdAt, тогда возвращаем отфильтрованный список")
        void whenFieldFilledThenReturnFilteredList() {
            Stream<Event> eventStream = Stream.of(
                    Event.builder()
                            .createdAt(createdAt)
                            .build(),
                    Event.builder()
                            .createdAt(LocalDateTime.of(2024, 10, 1, 12, 2))
                            .build());

            eventFilterDto = EventFilterDto.builder()
                    .createdAt(createdAt.toLocalDate())
                    .build();

            Stream<Event> resultEventStream = Stream.of(
                    Event.builder()
                            .createdAt(createdAt)
                            .build());
            assertEquals(resultEventStream.toList(), createdAtFilter.apply(eventStream, eventFilterDto).toList());
        }
    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("Если у EventFilterDto поле createdAt null тогда возвращаем false")
        void whenFieldNullThenReturnFalse() {

            eventFilterDto = EventFilterDto.builder()
                    .createdAt(null)
                    .build();

            assertFalse(createdAtFilter.isApplicable(eventFilterDto));
        }
    }
}