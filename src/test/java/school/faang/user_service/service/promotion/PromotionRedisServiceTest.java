package school.faang.user_service.service.promotion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.SyncTaskExecutor;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.mapper.promotion.PromotionRedisMapperImpl;
import school.faang.user_service.model.promotion.PromotionRedisModel;
import school.faang.user_service.repository.promotion.PromotionRedisRepository;
import school.faang.user_service.utils.redis.RedisKeyUtil;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PromotionRedisServiceTest {
    @Mock
    private PromotionRedisRepository promotionRedisRepository;
    @Spy
    private PromotionRedisMapperImpl promotionRedisMapper;
    private Executor executor = new SyncTaskExecutor();
    @Captor
    private ArgumentCaptor<PromotionRedisModel> promotionRedisModelCaptor;
    private PromotionRedisService promotionRedisService;
    private Promotion promotion;
    private PromotionRedisModel promotionRedisModel;

    @BeforeEach
    void setUp() {
        promotion = new Promotion();
        promotion.setId(14L);

        promotionRedisModel = new PromotionRedisModel();
        promotionRedisModel.setId(promotion.getId());
        promotionRedisModel.setKey(RedisKeyUtil.getSmallKeyById(promotion.getId()));

        promotionRedisService = new PromotionRedisService(
                promotionRedisRepository,
                promotionRedisMapper,
                executor);
    }

    @Test
    void testSavePromotion_successfully() {
        assertDoesNotThrow(() -> promotionRedisService.savePromotion(promotion));

        verify(promotionRedisRepository).save(promotionRedisModelCaptor.capture());

        PromotionRedisModel capturedModel = promotionRedisModelCaptor.getValue();
        assertNotNull(capturedModel);
        assertEquals(RedisKeyUtil.getSmallKeyById(promotion.getId()), capturedModel.getKey());
        assertEquals(promotion.getId(), capturedModel.getId());
    }

    @Test
    void testDeletePromotionByKey_successfully() {
        String key = "PROMOTION:42";

        assertDoesNotThrow(() -> promotionRedisService.deletePromotionByKey(key));

        verify(promotionRedisRepository).deleteById(key);
    }

    @Test
    void testGetAllPromotions_returnListOfPromotions() {
        PromotionRedisModel model1 = new PromotionRedisModel();
        PromotionRedisModel model2 = new PromotionRedisModel();
        Iterable<PromotionRedisModel> iterable = List.of(model1, model2);

        when(promotionRedisRepository.findAll()).thenReturn(iterable);

        List<PromotionRedisModel> result = promotionRedisService.getAllPromotions();

        assertEquals(2, result.size());
        assertTrue(result.contains(model1));
        assertTrue(result.contains(model2));
        verify(promotionRedisRepository).findAll();
    }

    @Test
    void testDecrementCountViewByEventIds_promotionFound() {
        long eventId = 123L;


        when(promotionRedisRepository.findByEventId(eventId)).thenReturn(Optional.of(promotionRedisModel));

        assertDoesNotThrow(() -> promotionRedisService.decrementCountViewByEventIds(List.of(eventId)));

        verify(promotionRedisRepository).decrementCountView(promotionRedisModel.getKey());
    }

    @Test
    void testDecrementCountViewByUserIds_promotionNotFound() {
        long userId = 999L;

        promotionRedisService.decrementCountViewByEventIds(List.of(userId));

        verify(promotionRedisRepository, never()).decrementCountView(any());
    }

    @Test
    void testDecrementCountViewByUserIds_promotionFound() {
        long userId = 123L;


        when(promotionRedisRepository.findByUserId(userId)).thenReturn(Optional.of(promotionRedisModel));

        assertDoesNotThrow(() -> promotionRedisService.decrementCountViewByUserIds(List.of(userId)));

        verify(promotionRedisRepository).decrementCountView(promotionRedisModel.getKey());
    }

    @Test
    void testDecrementCountViewByEventIds_promotionNotFound() {
        long userId = 999L;

        promotionRedisService.decrementCountViewByUserIds(List.of(userId));

        verify(promotionRedisRepository, never()).decrementCountView(any());
    }
}