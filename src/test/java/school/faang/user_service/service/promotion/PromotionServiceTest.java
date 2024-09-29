package school.faang.user_service.service.promotion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.client.*;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.dto.promotion.PromotionTarget;
import school.faang.user_service.dto.promotion.PromotionType;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.exception.PaymentFailureException;
import school.faang.user_service.mapper.promotion.PromotionMapper;
import school.faang.user_service.repository.promotion.PromotionRepository;
import school.faang.user_service.validator.promotion.PromotionValidator;
import school.faang.user_service.validator.user.UserValidator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromotionServiceTest {

    private static final Long USER_ID = 1L;
    private static final PromotionType PROMOTION_TYPE = PromotionType.BASIC;
    private static final PromotionTarget PROMOTION_TARGET = PromotionTarget.EVENTS;
    private static final long PAYMENT_NUMBER = 123456789L;
    private static final BigDecimal AMOUNT = BigDecimal.valueOf(PROMOTION_TYPE.getPrice());

    @InjectMocks
    private PromotionService promotionService;
    @Mock
    private PaymentServiceClient paymentServiceClient;
    @Mock
    private PromotionMapper promotionMapper;
    @Mock
    private PromotionRepository promotionRepository;
    @Mock
    private PromotionValidator promotionValidator;
    @Mock
    private UserValidator userValidator;

    private PromotionDto promotionDto;
    private PaymentResponse paymentResponse;
    private Promotion promotion;

    @BeforeEach
    void setUp() {
        promotionDto = new PromotionDto(
                USER_ID,
                PROMOTION_TYPE.getPriority(),
                PROMOTION_TYPE.getShowCount(),
                PROMOTION_TARGET
        );
        promotion = new Promotion();
        paymentResponse = new PaymentResponse(
                PaymentStatus.SUCCESS,
                12345,
                PAYMENT_NUMBER,
                AMOUNT,
                Currency.USD,
                Currency.EUR,
                "Payment successful"
        );
    }

    @Nested
    class BuyPromotionTests {

        @Test
        @DisplayName("Successfully buy a promotion")
        void whenBuyPromotionThenSuccess() {
            when(promotionRepository.getPromotionPaymentNumber()).thenReturn(PAYMENT_NUMBER);
            when(paymentServiceClient.sendPayment(any(PaymentRequest.class))).thenReturn(ResponseEntity.ok(paymentResponse));
            when(promotionMapper.toEntity(any(PromotionDto.class))).thenReturn(promotion);

            PromotionDto result = promotionService.buyPromotion(USER_ID, PROMOTION_TYPE, PROMOTION_TARGET);

            assertNotNull(result);
            assertEquals(USER_ID, result.getUserId());
            verify(userValidator).validateUserIsExisted(USER_ID);
            verify(promotionValidator).validatePromotionAlreadyExistsByUserId(USER_ID);
            verify(promotionRepository).save(any(Promotion.class));
        }

        @Test
        @DisplayName("Throws exception when no response from payment service")
        void whenNoPaymentResponseThenThrowException() {
            when(promotionRepository.getPromotionPaymentNumber()).thenReturn(PAYMENT_NUMBER);
            when(paymentServiceClient.sendPayment(any(PaymentRequest.class))).thenReturn(ResponseEntity.of(Optional.empty()));

            assertThrows(PaymentFailureException.class, () -> promotionService.buyPromotion(USER_ID, PROMOTION_TYPE, PROMOTION_TARGET));
            verify(promotionRepository, never()).save(any());
        }

        @Test
        @DisplayName("Throws exception when payment fails")
        void whenPaymentFailureThenThrowException() {
            PaymentResponse failedPaymentResponse = new PaymentResponse(
                    PaymentStatus.FAILURE,
                    12345,
                    PAYMENT_NUMBER,
                    AMOUNT,
                    Currency.USD,
                    Currency.EUR,
                    "Payment failed"
            );
            when(promotionRepository.getPromotionPaymentNumber()).thenReturn(PAYMENT_NUMBER);
            when(paymentServiceClient.sendPayment(any(PaymentRequest.class))).thenReturn(ResponseEntity.ok(failedPaymentResponse));

            assertThrows(PaymentFailureException.class, () -> promotionService.buyPromotion(USER_ID, PROMOTION_TYPE, PROMOTION_TARGET));
            verify(promotionRepository, never()).save(any());
        }
    }

    @Nested
    class RemoveExpiredPromotionsTests {

        @Test
        @DisplayName("Successfully remove expired promotions")
        void whenRemoveExpiredPromotionsThenSuccess() {
            List<Promotion> expiredPromotions = List.of(promotion);
            when(promotionRepository.findAllExpiredPromotions()).thenReturn(expiredPromotions);

            promotionService.removeExpiredPromotions();

            verify(promotionRepository).deleteAll(expiredPromotions);
        }
    }

    @Nested
    class MarkAsShownPromotionsTests {

        @Test
        @DisplayName("Does not decrease show count for empty promotion IDs")
        void whenEmptyPromotionIdsThenNoDecreaseShowCount() {
            promotionService.markAsShowPromotions(List.of());

            verify(promotionRepository, never()).decreaseShowCount(anyList());
        }

        @Test
        @DisplayName("Successfully decrease show count for promotions")
        void whenPromotionIdsProvidedThenDecreaseShowCount() {
            List<Long> promotionIds = List.of(1L, 2L);
            promotionService.markAsShowPromotions(promotionIds);

            verify(promotionRepository).decreaseShowCount(promotionIds);
        }
    }
}
