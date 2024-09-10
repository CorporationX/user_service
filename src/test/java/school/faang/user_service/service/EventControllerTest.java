package school.faang.user_service.service;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import school.faang.user_service.controller.event.EventController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
public class EventControllerTest {

    @Mock
    EventService eventService;

    @InjectMocks
    EventController eventController;

//    private AutoCloseable closeable;
//
//    @BeforeEach
//    public void setUp() throws Exception {
//        closeable = openMocks(this);
//    }
//
//    @AfterEach
//    public void tearDown() throws Exception {
//        closeable.close();
//    }

    @Test
    public void testAllGood() {
        EventDto eventDto = new EventDto(98L, "title", LocalDateTime.now(),
                LocalDateTime.now(), 1L, "description",
                List.of(), "location", 10, EventType.WEBINAR, EventStatus.COMPLETED);
        eventController.create(eventDto);
        verify(eventService, times(1)).create(eventDto);
    }

//    @Test
//    public void testNullTitleInvalid() {
//        Assert.assertThrows(
//                DataValidationException.class,
//                () -> eventController.create(new EventDto(3L, null, LocalDateTime.now(),
//                        LocalDateTime.now(), 1L, "description",
//                        List.of(), "location", 10, EventType.WEBINAR, EventStatus.COMPLETED)));
//
//    }
//
//    @Test
//    public void testEmptyTitleInvalid() {
//        Assert.assertThrows(
//                DataValidationException.class,
//                () -> eventController.create(new EventDto(3L, "  ", LocalDateTime.now(),
//                        LocalDateTime.now(), 1L, "description",
//                        List.of(), "location", 10, EventType.WEBINAR, EventStatus.COMPLETED))
//        );
//    }
//
//    @Test
//    public void testAllGood2() {
//        EventDto eventDto = new EventDto(3L, "title", LocalDateTime.now(),
//                LocalDateTime.now(), 1L, "description",
//                List.of(), "location", 10, EventType.WEBINAR, EventStatus.COMPLETED);
//        System.out.println(eventController);
//        eventController.create(eventDto);
//        Mockito.verify(eventService, times(1)).create(eventDto);
//    }
}
