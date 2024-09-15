package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.filter.event.EventTitleFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.thread.ThreadPoolDistributor;
import school.faang.user_service.validator.event.EventValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.entity.event.EventStatus.COMPLETED;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private ThreadPoolDistributor threadPool;
    @Mock
    private EventMapper eventMapper;
    @Mock
    private EventValidator eventValidator;
    @Mock
    private EventTitleFilter eventTitleFilter;

    private List<EventFilter> filter;

    private final int quantityThreadPollSize = 2;

    private EventService eventService;

    @BeforeEach
    public void setUp() {
        // Инициализируем список filter
        filter = new ArrayList<>();
        ThreadPoolTaskExecutor executor = Mockito.mock(ThreadPoolTaskExecutor.class);

        // Мокируем поведение executor
        when(threadPool.customThreadPool()).thenReturn(executor);
        when(executor.submit(any(Runnable.class))).thenReturn(Mockito.mock(Future.class)); // Возвращаем мокаемый Future
        when(executor.getPoolSize()).thenReturn(quantityThreadPollSize);

        // Добавляем фильтр
        filter.add(eventTitleFilter);

        // Инициализируем тестируемый класс
        eventService = new EventService(eventRepository, threadPool, eventMapper, filter, eventValidator);
    }

    @Test
    @DisplayName("Success test method deletingAllPastEvents")
    public void testDeletingAllPastEvents() {
        var event = Event.builder().id(1L).status(COMPLETED).build();
        List<Event> completedEvents = List.of(event);
        when(eventRepository.findByStatus(COMPLETED)).thenReturn(completedEvents);

        eventService.deletingAllPastEvents();

        verify(eventRepository, times(1)).findByStatus(COMPLETED);
        verify(threadPool.customThreadPool(), times(quantityThreadPollSize)).submit(any(Runnable.class));
    }

    @Test
    @DisplayName("Test of the behavior of the method deletingAllPastEvents in case of receiving an incorrect list of completed events")
    public void testDeletingAllPastEventsWithNoEvents() {
        when(eventRepository.findByStatus(COMPLETED)).thenReturn(new ArrayList<>());

        eventService.deletingAllPastEvents();

        verify(eventRepository, times(1)).findByStatus(COMPLETED);
    }

    @Test
    @DisplayName("Test for obtaining an odd size sheet of past events")
    public void testDeletingAllPastEventsWithUnevenDistribution() {
        List<Event> events = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            Event event = new Event();
            event.setId((long) i);
            event.setStatus(COMPLETED);
            events.add(event);
        }
        when(eventRepository.findByStatus(COMPLETED)).thenReturn(events);

        eventService.deletingAllPastEvents();

        verify(eventRepository, times(1)).findByStatus(COMPLETED);
        verify(threadPool.customThreadPool(), times(quantityThreadPollSize)).submit(any(Runnable.class));
    }
}