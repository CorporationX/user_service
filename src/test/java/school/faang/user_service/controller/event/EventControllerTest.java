package school.faang.user_service.controller.event;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class EventControllerTest {

    @Mock
    EventService eventService;

    @InjectMocks
    private EventController eventController;

    @Test
    public void testValidEvent() {
        EventDto event = EventDto.builder()
                .title("Valid title")
                .startDate(LocalDateTime.now())
                .ownerId(1L)
                .build();

        eventController.create(event);
        verify(eventService, times(1))
                .create(event);
    }

    @Test
    public void testNullTitleIsInvalid() {
        assertThrows(DataValidationException.class, () -> {
            EventDto event = EventDto.builder()
                    .startDate(LocalDateTime.now())
                    .ownerId(1L)
                    .build();

            eventController.create(event);
        });
    }

    @Test
    public void testEmptyTitleIsInvalid() {
        assertThrows(DataValidationException.class, () -> {
            EventDto event = EventDto.builder()
                    .title("")
                    .startDate(LocalDateTime.now())
                    .ownerId(1L)
                    .build();

            eventController.create(event);
        });
    }

    @Test
    public void testSpacesTitleIsInvalid() {
        assertThrows(DataValidationException.class, () -> {
            EventDto event = EventDto.builder()
                    .title("   ")
                    .startDate(LocalDateTime.now())
                    .ownerId(1L)
                    .build();

            eventController.create(event);
        });
    }

    @Test
    public void testNullStartDateIsInvalid() {
        assertThrows(DataValidationException.class, () -> {
            EventDto event = EventDto.builder()
                    .title("Valid title")
                    .ownerId(1L)
                    .build();

            eventController.create(event);
        });
    }

    @Test
    public void testNullOwnerIdIsInvalid() {
        assertThrows(DataValidationException.class, () -> {
            EventDto event = EventDto.builder()
                    .title("Valid title")
                    .startDate(LocalDateTime.now())
                    .build();

            eventController.create(event);
        });
    }
}
