package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.thread.ThreadPoolDistributor;
import school.faang.user_service.validator.event.EventValidator;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.entity.event.EventStatus.COMPLETED;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventMapper eventMapper;
    @Mock
    private EventValidator eventValidator;
    @Mock
    private ThreadPoolDistributor threadPoolDistributor;
    @Mock
    private List<EventFilter> filter;
    @Mock
    private ThreadPoolTaskExecutor customThreadPool;

    private EventService eventService;

    @BeforeEach
    void setUp() {
        eventService = new EventService(eventRepository, eventMapper,
                filter, eventValidator, threadPoolDistributor, 2);
        when(threadPoolDistributor.customThreadPool()).thenReturn(customThreadPool);
    }

    @Test
    void deletingAllPastEvents_ShouldDistributeTasksAmongThreads() {
        Event event1 = Event.builder().id(1).build();
        Event event2 = Event.builder().id(2).build();
        Event event3 = Event.builder().id(3).build();
        Event event4 = Event.builder().id(4).build();
        List<Event> events = List.of(event1, event2, event3, event4);
        var firstSubListEvents = List.of(event1, event2);
        var secondSubListEvents = List.of(event3, event4);
        when(eventRepository.findByStatus(COMPLETED)).thenReturn(events);
        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);

        eventService.deletingAllPastEvents();

        verify(eventRepository).findByStatus(COMPLETED);
        verify(threadPoolDistributor.customThreadPool(), times(2)).submit(runnableCaptor.capture());
        runnableCaptor.getAllValues().forEach(Runnable::run);
        verify(eventRepository).deleteAllInBatch(firstSubListEvents);
        verify(eventRepository).deleteAllInBatch(secondSubListEvents);
    }

    @Test
    void deletingAllPastEvents_ShouldHandleEmptyList() {
        when(eventRepository.findByStatus(COMPLETED)).thenReturn(Collections.emptyList());

        eventService.deletingAllPastEvents();

        verify(eventRepository).findByStatus(COMPLETED);
    }
}