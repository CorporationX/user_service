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
class EventTitleFilterTest {

    @InjectMocks
    private EventTitleFilter eventTitleFilter;

    private final String title = "title";

    private EventFilterDto eventFilterDto;

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Если у EventFilterDto поле title не null и не пустое, тогда возвращаем true")
        void whenFieldValidThenReturnTrue() {

            eventFilterDto = EventFilterDto.builder()
                    .title(title)
                    .build();

            assertTrue(eventTitleFilter.isApplicable(eventFilterDto));
        }

        @Test
        @DisplayName("Если у EventFilterDto заполнено поле title, тогда возвращаем отфильтрованный список")
        void whenFieldFilledThenReturnFilteredList() {
            Stream<Event> eventStream = Stream.of(
                    Event.builder()
                            .title(title)
                            .build(),
                    Event.builder()
                            .title("test")
                            .build());

            eventFilterDto = EventFilterDto.builder()
                    .title(title)
                    .build();

            Stream<Event> resultEventStream = Stream.of(
                    Event.builder()
                            .title(title)
                            .build());
            assertEquals(resultEventStream.toList(), eventTitleFilter.apply(eventStream, eventFilterDto).toList());
        }

    }

    @Nested
    class NegativeTests {

        @Nested
        class IsApplicable {

            @Test
            @DisplayName("Если у EventFilterDto поле title null тогда возвращаем false")
            void whenFieldNullThenReturnFalse() {

                eventFilterDto = EventFilterDto.builder()
                        .title(null)
                        .build();

                assertFalse(eventTitleFilter.isApplicable(eventFilterDto));
            }

            @Test
            @DisplayName("Если у EventFilterDto поле title пустое, тогда возвращаем false")
            void whenFielIsEmptyThenReturnFalse() {
                eventFilterDto = EventFilterDto.builder()
                        .title("   ")
                        .build();

                assertFalse(eventTitleFilter.isApplicable(eventFilterDto));
            }
        }
    }
}