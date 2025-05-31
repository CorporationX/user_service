package school.faang.user_service.service.promotion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dao.promotion.PromotionDao;
import school.faang.user_service.exception.BatchUpdateProcessingException;
import school.faang.user_service.kafka.events.AnalyticsEventType;
import school.faang.user_service.redis.promotion.PromotionAnalyticsCacheService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromotionBatchProcessorTest {
    @Mock
    private PromotionAnalyticsCacheService cacheService;

    @Mock
    private PromotionDao samplePromotionDao;

    @Captor
    private ArgumentCaptor<List<Long>> longListCaptor;

    private PromotionBatchProcessor batchProcessor;

    private final AnalyticsEventType EVENT_TYPE = AnalyticsEventType.EVENT_VIEW;
    private final AnalyticsEventType PROFILE_TYPE = AnalyticsEventType.PROFILE_VIEW;

    @BeforeEach
    void setUp() {
        when(samplePromotionDao.getEventType()).thenReturn(EVENT_TYPE);
        batchProcessor = new PromotionBatchProcessor(cacheService, List.of(samplePromotionDao));
    }


    @Test
    void process_NoIdsAboveThreshold_DoesNothing() {
        when(cacheService.getIdsScoreAboveThreshold(EVENT_TYPE))
                .thenReturn(Collections.emptyMap());

        batchProcessor.process(EVENT_TYPE);

        verify(samplePromotionDao, never()).batchUpdatePromotions(anyMap());
        verify(cacheService, never()).removeProcessedKeys(any(), anyList());
    }

    @Test
    void process_WithIdsAboveThreshold_UpdatesAndRemovesKeys() {
        Map<Long, Long> idsToScores = Map.of(1L, 10L, 2L, 20L);
        when(cacheService.getIdsScoreAboveThreshold(EVENT_TYPE))
                .thenReturn(idsToScores);

        List<Long> updatedIds = List.of(1L, 2L);
        when(samplePromotionDao.batchUpdatePromotions(idsToScores)).thenReturn(updatedIds);

        batchProcessor.process(EVENT_TYPE);

        verify(samplePromotionDao, times(1)).batchUpdatePromotions(idsToScores);

        verify(cacheService, times(1)).removeProcessedKeys(eq(EVENT_TYPE), longListCaptor.capture());
        assertEquals(updatedIds, longListCaptor.getValue());
    }

    @Test
    void process_DaoThrowsException_ThrowsBatchUpdateProcessingException() {
        Map<Long, Long> idsToScores = Map.of(3L, 30L);
        when(cacheService.getIdsScoreAboveThreshold(EVENT_TYPE))
                .thenReturn(idsToScores);

        when(samplePromotionDao.batchUpdatePromotions(idsToScores))
                .thenThrow(new RuntimeException("DB error"));

        BatchUpdateProcessingException ex = assertThrows(
                BatchUpdateProcessingException.class,
                () -> batchProcessor.process(EVENT_TYPE)
        );
        assertTrue(ex.getMessage().contains("Batch update promotions failed"));

        verify(cacheService, never()).removeProcessedKeys(any(), anyList());
    }

    @Test
    void process_UnsupportedEvent_ThrowsBatchUpdateProcessingException() {
        Map<Long, Long> idsToScores = Map.of(4L, 40L);
        when(cacheService.getIdsScoreAboveThreshold(PROFILE_TYPE))
                .thenReturn(idsToScores);

        BatchUpdateProcessingException ex = assertThrows(
                BatchUpdateProcessingException.class,
                () -> batchProcessor.process(PROFILE_TYPE)
        );
        assertTrue(ex.getCause() instanceof IllegalArgumentException);
        assertTrue(ex.getCause().getMessage().contains("No processor for " + PROFILE_TYPE));

        verify(cacheService, never()).removeProcessedKeys(any(), anyList());
    }
}