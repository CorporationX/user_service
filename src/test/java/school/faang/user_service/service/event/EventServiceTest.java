package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    @BeforeEach
    void setUp() {
        ExecutorService realExecutor = Executors.newFixedThreadPool(10);
        ReflectionTestUtils.setField(eventService, "executorService", realExecutor);
        ReflectionTestUtils.setField(eventService, "batchSize", 100);
    }

    @Test
    void testRemovePastEventsWhenNoExpiredEventsShouldNotDelete() {
        // Given
        when(eventRepository.findExpiredEventIds(any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // When
        eventService.removePastEvents();

        // Then
        verify(eventRepository).findExpiredEventIds(any(LocalDateTime.class));
        verify(eventRepository, never()).deleteAllById(anyList());
    }

    @Test
    void testRemovePastEventsWhenExpiredEventsExistShouldDeleteThem() {
        // Given
        List<Long> expiredEventIds = List.of(1L, 2L, 3L);

        when(eventRepository.findExpiredEventIds(any(LocalDateTime.class)))
                .thenReturn(expiredEventIds);

        // When
        eventService.removePastEvents();

        // Then
        verify(eventRepository).findExpiredEventIds(any(LocalDateTime.class));
        verify(eventRepository).deleteAllById(List.of(1L, 2L, 3L));
    }

    @Test
    void testRemovePastEventsWithMultipleBatchesShouldDeleteInBatches() {
        // Given
        ReflectionTestUtils.setField(eventService, "batchSize", 2);

        List<Long> expiredEventIds = List.of(1L, 2L, 3L, 4L, 5L);

        when(eventRepository.findExpiredEventIds(any(LocalDateTime.class)))
                .thenReturn(expiredEventIds);

        // When
        eventService.removePastEvents();

        // Then
        verify(eventRepository).findExpiredEventIds(any(LocalDateTime.class));
        verify(eventRepository).deleteAllById(List.of(1L, 2L));
        verify(eventRepository).deleteAllById(List.of(3L, 4L));
        verify(eventRepository).deleteAllById(List.of(5L));
    }

    @Test
    void testRemovePastEventsWithExactlyOneBatchShouldDeleteOnce() {
        // Given
        ReflectionTestUtils.setField(eventService, "batchSize", 3);

        List<Long> expiredEventIds = List.of(1L, 2L, 3L);

        when(eventRepository.findExpiredEventIds(any(LocalDateTime.class)))
                .thenReturn(expiredEventIds);

        // When
        eventService.removePastEvents();

        // Then
        verify(eventRepository).findExpiredEventIds(any(LocalDateTime.class));
        verify(eventRepository, times(1)).deleteAllById(anyList());
        verify(eventRepository).deleteAllById(List.of(1L, 2L, 3L));
    }

    @Test
    void testRemovePastEventsWithLargeBatchShouldPartitionCorrectly() {
        // Given
        ReflectionTestUtils.setField(eventService, "batchSize", 100);

        List<Long> expiredEventIds = IntStream.rangeClosed(1, 250)
                .mapToObj(Long::valueOf)
                .toList();

        when(eventRepository.findExpiredEventIds(any(LocalDateTime.class)))
                .thenReturn(expiredEventIds);

        // When
        eventService.removePastEvents();

        // Then
        verify(eventRepository).findExpiredEventIds(any(LocalDateTime.class));
        verify(eventRepository, times(3)).deleteAllById(anyList());
    }
}