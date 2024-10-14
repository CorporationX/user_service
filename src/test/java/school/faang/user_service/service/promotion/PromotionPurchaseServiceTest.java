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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromotionPurchaseServiceTest {

    private static final Long USER_ID = 1L;
    private static final PromotionType PROMOTION_TYPE = PromotionType.BASIC;
    private static final PromotionTarget PROMOTION_TARGET = PromotionTarget.EVENTS;
    private static final long PAYMENT_NUMBER = 123456789L;

    @InjectMocks
    private PromotionPurchaseService promotionPurchaseService;
    @Mock
    private PromotionMapper promotionMapper;
    @Mock
    private PromotionRepository promotionRepository;
    @Mock
    private PromotionValidator promotionValidator;
    @Mock
    private UserService userService;
    @Mock
    private PaymentService paymentService;

    private PromotionDto promotionDto;
    private User user;

    @BeforeEach
    void setUp() {
        promotionDto = PromotionDto.builder()
                .userId(USER_ID)
                .priority(PROMOTION_TYPE.getPriority())
                .showCount(PROMOTION_TYPE.getShowCount())
                .target(PROMOTION_TARGET)
                .build();
        user = User.builder().build();
    }

    @Nested
    class BuyPromotionTests {

        @Test
        @DisplayName("Successfully buy a promotion")
        void whenBuyPromotionThenSuccess() {
            when(userService.getUserById(USER_ID)).thenReturn(user);
            when(promotionRepository.getPromotionPaymentNumber()).thenReturn(PAYMENT_NUMBER);
            when(promotionMapper.toPromotionDto(any(Promotion.class))).thenReturn(promotionDto);

            PromotionDto result = promotionPurchaseService.buyPromotion(USER_ID, PROMOTION_TYPE, PROMOTION_TARGET);

            assertNotNull(result);
            assertEquals(USER_ID, result.getUserId());
            verify(promotionValidator).validatePromotionAlreadyExistsByUserId(USER_ID);
            verify(paymentService).sendPayment(PROMOTION_TYPE.getPrice(), PAYMENT_NUMBER);
            verify(promotionRepository).save(any(Promotion.class));
        }

        @Test
        @DisplayName("Throws exception when payment fails")
        void whenPaymentFailsThenThrowException() {
            when(userService.getUserById(USER_ID)).thenReturn(user);
            when(promotionRepository.getPromotionPaymentNumber()).thenReturn(PAYMENT_NUMBER);
            doThrow(new RuntimeException("Payment failure")).when(paymentService).sendPayment(any(), any());

            assertThrows(RuntimeException.class, () -> promotionPurchaseService.buyPromotion(USER_ID, PROMOTION_TYPE, PROMOTION_TARGET));
            verify(promotionRepository).save(any());
        }
    }
}
