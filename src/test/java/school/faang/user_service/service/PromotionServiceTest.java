package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.model.enums.Currency;
import school.faang.user_service.model.dto.PaymentRequest;
import school.faang.user_service.model.dto.PaymentResponse;
import school.faang.user_service.model.enums.PaymentStatus;
import school.faang.user_service.model.dto.PromotionDto;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.entity.Promotion;
import school.faang.user_service.model.enums.PromotionType;
import school.faang.user_service.exception.ExistingPurchaseException;
import school.faang.user_service.exception.PaymentFailureException;
import school.faang.user_service.mapper.PromotionMapper;
import school.faang.user_service.repository.PromotionRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.impl.PromotionServiceImpl;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PromotionServiceTest {
    private User user;
    private final PromotionType promotionType = PromotionType.GENERAL;
    private final String promotionTarget = "profile";

    private PaymentResponse paymentResponse;
    private PaymentResponse unsuccessfulPaymentResponse;
    private Promotion promotion;
    private PromotionDto promotionDto;

    @Mock
    private PromotionRepository promotionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @Mock
    private PromotionMapper promotionMapper;

    @InjectMocks
    private PromotionServiceImpl promotionService;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        paymentResponse = new PaymentResponse(
                PaymentStatus.SUCCESS,
                12345,
                67890L,
                new BigDecimal("10.00"),
                Currency.USD,
                "Payment successful");
        unsuccessfulPaymentResponse = new PaymentResponse(
                PaymentStatus.FAIL,
                12345,
                67890L,
                new BigDecimal("10.00"),
                Currency.USD,
                "Payment unsuccessful");
        promotionDto = PromotionDto.builder()
                .id(1L)
                .promotedUserId(user.getId())
                .priorityLevel(1)
                .remainingShows(10)
                .promotionTarget("profile")
                .build();
        promotion = new Promotion();
        promotion.setPromotionTarget(promotionTarget);
    }

    @Test
    @DisplayName("Should successfully complete the promotion purchase")
    void testBuyPromotion_Success() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(paymentServiceClient.sendPayment(any(PaymentRequest.class)))
                .thenReturn(ResponseEntity.ok(paymentResponse));
        when(promotionRepository.getPromotionPaymentNumber()).thenReturn(12345L);
        when(promotionMapper.toPromotionDto(any(Promotion.class))).thenReturn(promotionDto);
        when(promotionRepository.save(any(Promotion.class))).thenReturn(promotion);

        PromotionDto result = promotionService.buyPromotion(1L, promotionType, promotionTarget);

        assertNotNull(result);
        assertEquals(promotionDto, result);

        verify(userRepository).findById(1L);
        verify(paymentServiceClient).sendPayment(any(PaymentRequest.class));
        verify(promotionRepository).save(any(Promotion.class));
        verify(promotionMapper).toPromotionDto(any(Promotion.class));
    }

    @Test
    @DisplayName("Should throw exception when user is not found")
    void testBuyPromotion_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                promotionService.buyPromotion(1L, promotionType, promotionTarget));

        verify(userRepository).findById(1L);
        verifyNoInteractions(paymentServiceClient, promotionRepository, promotionMapper);
    }

    @Test
    @DisplayName("Should throw exception if user already has an active promotion subscription")
    void testBuyPromotion_ExistingPremiumSubscription() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(promotionRepository.findByUserId(user.getId())).thenReturn(Optional.of(promotion));

        Exception thrownException = assertThrows(ExistingPurchaseException.class, () -> {
            promotionService.buyPromotion(user.getId(), promotionType, promotionTarget);
        });

        assertEquals("User already has an active promotion subscription" +
                " with the same target", thrownException.getMessage());

        verify(userRepository).findById(1L);
        verify(promotionRepository).findByUserId(1L);
        verifyNoMoreInteractions(paymentServiceClient, promotionMapper, promotionRepository);
    }

    @Test
    @DisplayName("Should throw PaymentFailureException when payment fails")
    void testBuyPromotion_Failure() {
        user.setUsername("testUser");

        ResponseEntity<PaymentResponse> failedResponseEntity = new ResponseEntity<>(unsuccessfulPaymentResponse, HttpStatus.OK);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(paymentServiceClient.sendPayment(any(PaymentRequest.class))).thenReturn(failedResponseEntity);
        when(promotionRepository.getPromotionPaymentNumber()).thenReturn(12345L);

        PaymentFailureException exception = assertThrows(PaymentFailureException.class, () ->
                promotionService.buyPromotion(1L, promotionType, promotionTarget));

        assertEquals("Failure to effect profile promotion payment for user testUser.",
                exception.getMessage());

        verify(promotionRepository, never()).save(any(Promotion.class));
        verify(promotionMapper, never()).toPromotionDto(any(Promotion.class));
    }
}
