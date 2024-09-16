package school.faang.user_service.filter.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventType;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TypeFilterTest {

    @InjectMocks
    private TypeFilter typeFilter;

    private final EventType type = EventType.GIVEAWAY;

    private EventFilterDto eventFilterDto;

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Если у EventFilterDto поле type не null, тогда возвращаем true")
        void whenFieldValidThenReturnTrue() {

            eventFilterDto = EventFilterDto.builder()
                    .type(type)
                    .build();

            assertTrue(typeFilter.isApplicable(eventFilterDto));
        }

        @Test
        @DisplayName("Если у EventFilterDto заполнено поле type, тогда возвращаем отфильтрованный список")
        void whenFieldFilledThenReturnFilteredList() {
            Stream<Event> eventStream = Stream.of(
                    Event.builder()
                            .type(type)
                            .build(),
                    Event.builder()
                            .type(EventType.MEETING)
                            .build());

            eventFilterDto = EventFilterDto.builder()
                    .type(type)
                    .build();

            Stream<Event> resultEventStream = Stream.of(
                    Event.builder()
                            .type(type)
                            .build());
            assertEquals(resultEventStream.toList(), typeFilter.apply(eventStream, eventFilterDto).toList());
        }

    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("Если у EventFilterDto поле type null тогда возвращаем false")
        void whenFieldNullThenReturnFalse() {

            eventFilterDto = EventFilterDto.builder()
                    .type(null)
                    .build();

            assertFalse(typeFilter.isApplicable(eventFilterDto));
        }
    }
}