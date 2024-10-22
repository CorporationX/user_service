package school.faang.user_service.service.impl;

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
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.model.enums.Currency;
import school.faang.user_service.model.dto.PaymentRequest;
import school.faang.user_service.model.dto.PaymentResponse;
import school.faang.user_service.model.enums.PaymentStatus;
import school.faang.user_service.model.dto.PremiumDto;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.entity.Premium;
import school.faang.user_service.model.enums.PremiumPeriod;
import school.faang.user_service.exception.ExistingPurchaseException;
import school.faang.user_service.exception.PaymentFailureException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.publisher.PremiumBoughtEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.PremiumRepository;
import school.faang.user_service.scheduler.PremiumRemoverTransactions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PremiumServiceTest {
    private User user;
    private PremiumPeriod premiumPeriod;
    private PaymentResponse paymentResponse;
    private PaymentResponse unsuccessfulPaymentResponse;
    private Premium premium;
    private PremiumDto premiumDto;

    @Mock
    private PremiumRepository premiumRepository;

    @Mock
    private PremiumRemoverTransactions premiumRemoverTransactions;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @Mock
    private PremiumMapper premiumMapper;

    @Mock
    private PremiumBoughtEventPublisher premiumBoughtEventPublisher;

    @InjectMocks
    private PremiumServiceImpl premiumService;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        premiumPeriod = PremiumPeriod.fromDays(30);
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
        premiumDto = PremiumDto.builder()
                .id(1L)
                .userId(user.getId())
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(3))
                .build();
        premium = new Premium();

        ReflectionTestUtils.setField(premiumService, "batchSize", 2);
    }

    @Test
    @DisplayName("Should successfully complete the premium purchase")
    void testBuyPremium_Success() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(paymentServiceClient.sendPayment(any(PaymentRequest.class)))
                .thenReturn(ResponseEntity.ok(paymentResponse));
        when(premiumRepository.getPremiumPaymentNumber()).thenReturn(12345L);
        when(premiumMapper.toPremiumDto(any(Premium.class))).thenReturn(premiumDto);
        when(premiumRepository.save(any(Premium.class))).thenReturn(premium);

        PremiumDto result = premiumService.buyPremium(1L, premiumPeriod);

        assertNotNull(result);
        assertEquals(premiumDto, result);

        verify(userRepository).findById(1L);
        verify(paymentServiceClient).sendPayment(any(PaymentRequest.class));
        verify(premiumRepository).save(any(Premium.class));
        verify(premiumMapper).toPremiumDto(any(Premium.class));
    }

    @Test
    @DisplayName("Should throw exception when user is not found")
    void testBuyPremium_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> premiumService.buyPremium(1L, premiumPeriod));

        verify(userRepository).findById(1L);
        verifyNoInteractions(paymentServiceClient, premiumRepository, premiumMapper);
    }

    @Test
    @DisplayName("Should throw exception if user already has an active premium subscription")
    void testBuyPremium_ExistingPremiumSubscription() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(premiumRepository.existsByUserId(anyLong())).thenReturn(true);

        Exception thrownException = assertThrows(ExistingPurchaseException.class, () -> {
            premiumService.buyPremium(1L, premiumPeriod);
        });

        assertEquals("User already has an active premium subscription", thrownException.getMessage());

        verify(userRepository).findById(1L);
        verify(premiumRepository).existsByUserId(1L);
        verifyNoMoreInteractions(paymentServiceClient, premiumMapper, premiumRepository);
    }

    @Test
    @DisplayName("Should throw PaymentFailureException when payment fails")
    void testBuyPremium_Failure() {
        user.setUsername("testUser");

        ResponseEntity<PaymentResponse> failedResponseEntity = new ResponseEntity<>(unsuccessfulPaymentResponse, HttpStatus.OK);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(paymentServiceClient.sendPayment(any(PaymentRequest.class))).thenReturn(failedResponseEntity);
        when(premiumRepository.getPremiumPaymentNumber()).thenReturn(12345L);

        PaymentFailureException exception = assertThrows(PaymentFailureException.class, () ->
                premiumService.buyPremium(1L, PremiumPeriod.ONE_MONTH));

        assertEquals("Failure to effect premium payment for user testUser for requested period of 30 days.",
                exception.getMessage());

        verify(premiumRepository, never()).save(any(Premium.class));
        verify(premiumMapper, never()).toPremiumDto(any(Premium.class));
    }

    @Test
    @DisplayName("Should return split list of expired premium IDs")
    void testFindAndSplitExpiredPremiums_Success() {
        List<Premium> expiredPremiums = List.of(
                new Premium(1L, user, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1)),
                new Premium(2L, user, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1)),
                new Premium(3L, user, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1))
        );

        when(premiumRepository.findAllByEndDateBefore(any(LocalDateTime.class)))
                .thenReturn(expiredPremiums);

        List<List<Long>> result = premiumService.findAndSplitExpiredPremiums();

        assertEquals(2, result.size());
        assertEquals(2, result.get(0).size());
        assertEquals(1, result.get(1).size());

        verify(premiumRepository, times(1)).findAllByEndDateBefore(any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Should return empty list when there are no expired premiums")
    void testFindAndSplitExpiredPremiums_EmptyList() {
        when(premiumRepository.findAllByEndDateBefore(any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        List<List<Long>> result = premiumService.findAndSplitExpiredPremiums();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should delete expired premiums by IDs and return the count of deleted records")
    void testDeleteExpiredPremiumsByIds_Success() {
        List<Long> premiumIds = List.of(1L, 2L, 3L);
        int expectedDeletedCount = premiumIds.size();

        doReturn(expectedDeletedCount).when(premiumRemoverTransactions).deletePremiums(premiumIds);

        CompletableFuture<Integer> future = premiumService.deleteExpiredPremiumsByIds(premiumIds);

        Integer totalDeletedRecords = future.join();

        assertEquals(expectedDeletedCount, totalDeletedRecords);
        verify(premiumRemoverTransactions, times(1)).deletePremiums(premiumIds);
    }
}
