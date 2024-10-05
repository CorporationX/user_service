package school.faang.user_service.service.promotion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.dto.promotion.PromotionTarget;
import school.faang.user_service.dto.promotion.PromotionType;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.mapper.promotion.PromotionMapper;
import school.faang.user_service.repository.promotion.PromotionRepository;
import school.faang.user_service.service.payment.PaymentService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.promotion.PromotionValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromotionManagementServiceTest {

    private static final Long USER_ID = 1L;
    private static final PromotionType PROMOTION_TYPE = PromotionType.BASIC;
    private static final PromotionTarget PROMOTION_TARGET = PromotionTarget.EVENTS;
    private static final long PAYMENT_NUMBER = 123456789L;

    @InjectMocks
    private PromotionManagementService promotionManagementService;
    @Mock
    private PromotionRepository promotionRepository;

    private Promotion promotion;

    @BeforeEach
    void setUp() {
        promotion = Promotion.builder().build();
    }

    @Nested
    class RemoveExpiredPromotionsTests {

        @Test
        @DisplayName("Successfully remove expired promotions")
        void whenRemoveExpiredPromotionsThenSuccess() {
            List<Promotion> expiredPromotions = List.of(promotion);
            when(promotionRepository.findAllExpiredPromotions()).thenReturn(expiredPromotions);

            promotionManagementService.removeExpiredPromotions();

            verify(promotionRepository).deleteAll(expiredPromotions);
        }

        @Test
        @DisplayName("Does nothing if no expired promotions found")
        void whenNoExpiredPromotionsThenDoNothing() {
            when(promotionRepository.findAllExpiredPromotions()).thenReturn(List.of());

            promotionManagementService.removeExpiredPromotions();

            verify(promotionRepository, never()).deleteAll(anyList());
        }
    }

    @Nested
    class MarkAsShownPromotionsTests {

        @Test
        @DisplayName("Does not decrease show count for empty promotion IDs")
        void whenEmptyPromotionIdsThenNoDecreaseShowCount() {
            promotionManagementService.markAsShowPromotions(List.of());

            verify(promotionRepository, never()).decreaseShowCount(anyList());
        }

        @Test
        @DisplayName("Successfully decrease show count for promotions")
        void whenPromotionIdsProvidedThenDecreaseShowCount() {
            List<Long> promotionIds = List.of(1L, 2L);
            promotionManagementService.markAsShowPromotions(promotionIds);

            verify(promotionRepository).decreaseShowCount(promotionIds);
        }
    }
}
