package school.faang.user_service.service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.context.ContextConfiguration;
import school.faang.user_service.config.AppConfig;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {AppConfig.class})
public class EventServiceTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private AppConfig appConfig;
    @InjectMocks
    private EventService eventService;

    @Test
    public void removeAllPastEvents_success1ThreadsRun() {
        List<Event> events = new ArrayList<>();

        for (int i = 1; i < 500; i++) {
            events.add(Event.builder()
                    .id(i)
                    .status(EventStatus.COMPLETED)
                    .build());
        }
        when(appConfig.getMaxDataGroupSize()).thenReturn(1000);

        Page<Event> page = new PageImpl<>(events, PageRequest.of(0, 1000), 0);
        when(eventRepository.findAllByStatusIs(any(EventStatus.class), any(Pageable.class))).thenReturn(page);

        ExecutorService threadPool = mock(ExecutorService.class);
        when(appConfig.getThreadPool()).thenReturn(threadPool);
        when(appConfig.getMaxDataGroupSize()).thenReturn(1000);

        eventService.removeAllPastEvents();

        verify(threadPool, times(1)).submit(any(Runnable.class));
    }

    @Test
    public void removeAllPastEvents_zeroPostsToPublish() {
        Page<Event> emptyPage = new PageImpl<>(new ArrayList<>());

        when(eventRepository.findAllByStatusIs(any(EventStatus.class), any(Pageable.class))).thenReturn(emptyPage);
        when(appConfig.getMaxDataGroupSize()).thenReturn(1000);

        ExecutorService threadPool = mock(ExecutorService.class);
        when(appConfig.getThreadPool()).thenReturn(threadPool);

        eventService.removeAllPastEvents();

        verify(appConfig, times(1)).getThreadPool();
    }
}
