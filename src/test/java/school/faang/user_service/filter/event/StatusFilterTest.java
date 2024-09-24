package school.faang.user_service.filter.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StatusFilterTest {

    @InjectMocks
    private StatusFilter statusFilter;

    private final EventStatus status = EventStatus.IN_PROGRESS;

    private EventFilterDto eventFilterDto;

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Если у EventFilterDto поле status не null, тогда возвращаем true")
        void whenFieldValidThenReturnTrue() {

            eventFilterDto = EventFilterDto.builder()
                    .status(status)
                    .build();

            assertTrue(statusFilter.isApplicable(eventFilterDto));
        }

        @Test
        @DisplayName("Если у EventFilterDto заполнено поле status, тогда возвращаем отфильтрованный список")
        void whenFieldFilledThenReturnFilteredList() {
            Stream<Event> eventStream = Stream.of(
                    Event.builder()
                            .status(status)
                            .build(),
                    Event.builder()
                            .status(EventStatus.CANCELED)
                            .build());

            eventFilterDto = EventFilterDto.builder()
                    .status(status)
                    .build();

            Stream<Event> resultEventStream = Stream.of(
                    Event.builder()
                            .status(status)
                            .build());
            assertEquals(resultEventStream.toList(), statusFilter.apply(eventStream, eventFilterDto).toList());
        }

    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("Если у EventFilterDto поле status null тогда возвращаем false")
        void whenFieldNullThenReturnFalse() {

            eventFilterDto = EventFilterDto.builder()
                    .status(null)
                    .build();

            assertFalse(statusFilter.isApplicable(eventFilterDto));
        }
    }
}