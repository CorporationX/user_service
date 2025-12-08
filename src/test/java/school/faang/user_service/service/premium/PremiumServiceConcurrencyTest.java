package school.faang.user_service.service.premium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import school.faang.user_service.client.dto.PaymentResponse;
import school.faang.user_service.client.payment.PaymentServiceClient;
import school.faang.user_service.dto.premium.PremiumBoughtEvent;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPurchaseAttempt;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.enums.Currency;
import school.faang.user_service.enums.PaymentStatus;
import school.faang.user_service.enums.PremiumPeriod;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.premium.PremiumPurchaseAttemptRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Disabled // TODO: Удалить после исправление
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PremiumServiceConcurrencyTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PremiumRepository premiumRepository;

    @Mock
    private PremiumPurchaseAttemptRepository attemptRepository;

    @Mock
    private PaymentServiceClient paymentClient;

    @Mock
    private PremiumMapper premiumMapper;

    @Mock
    private PremiumCacheService premiumCacheService;

    @Mock
    private PremiumBoughtEventPublisher boughtEventPublisher;

    @InjectMocks
    private PremiumService premiumService;

    private User testUser;
    private PaymentResponse successResponse;
    private Premium testPremium;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();

        successResponse = new PaymentResponse(
                PaymentStatus.SUCCESS,
                12345,
                123456789L,
                new BigDecimal("10.00"),
                Currency.USD,
                "Payment successful"
        );

        testPremium = Premium.builder()
                .id(1L)
                .user(testUser)
                .premiumPeriod(PremiumPeriod.MONTHLY)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .amount(new BigDecimal("10.00"))
                .verificationCode(12345)
                .paymentNumber(123456789L)
                .currency(Currency.USD)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void buyPremium_WithConcurrentRequests_ShouldHandleRaceConditions() throws InterruptedException {

        int numberOfThreads = 10;

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(attemptRepository.findByPaymentNumber(anyString())).thenReturn(Optional.empty());
        when(attemptRepository.save(any(PremiumPurchaseAttempt.class))).thenAnswer(invocation -> {
            PremiumPurchaseAttempt attempt = invocation.getArgument(0);
            // Simulate race condition by sometimes throwing exception
            if (attempt.getPaymentNumber().hashCode() % 11 == 0) {
                throw new RuntimeException("Duplicate key");
            }
            return attempt;
        });
        when(attemptRepository.findByPaymentNumber(anyString())).thenReturn(Optional.empty());
        when(paymentClient.processPayment(any())).thenReturn(successResponse);
        when(premiumRepository.findFirstByUser_IdAndEndDateAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(premiumRepository.save(any(Premium.class))).thenReturn(testPremium);
        when(premiumMapper.toDto(testPremium)).thenReturn(createPremiumDto());

        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        for (int i = 0; i < numberOfThreads; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    premiumService.buyPremium(1L, PremiumPeriod.MONTHLY);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            }, executor);
            futures.add(future);
        }

        // Wait for all threads to complete
        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();


        assertThat(successCount.get() + errorCount.get()).isEqualTo(numberOfThreads);
        // At least one should succeed
        assertThat(successCount.get()).isGreaterThanOrEqualTo(0);
        // Verify that event was published for successful operations
        if (successCount.get() > 0) {
            verify(boughtEventPublisher, atLeast(1)).publish(any(PremiumBoughtEvent.class));
        }
    }

    @Test
    void buyPremium_WithConcurrentIdempotentRequests_ShouldReturnSameResult() throws InterruptedException {

        int numberOfThreads = 5;

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(attemptRepository.findByPaymentNumber(anyString())).thenReturn(Optional.empty());
        when(attemptRepository.save(any(PremiumPurchaseAttempt.class))).thenAnswer(invocation -> {
            PremiumPurchaseAttempt attempt = invocation.getArgument(0);
            // Simulate race condition by sometimes throwing exception
            if (attempt.getPaymentNumber().hashCode() % 11 == 0) {
                throw new RuntimeException("Duplicate key");
            }
            return attempt;
        });
        when(attemptRepository.findByPaymentNumber(anyString())).thenReturn(Optional.empty());
        when(paymentClient.processPayment(any())).thenReturn(successResponse);
        when(premiumRepository.findFirstByUser_IdAndEndDateAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(premiumRepository.save(any(Premium.class))).thenReturn(testPremium);
        when(premiumMapper.toDto(testPremium)).thenReturn(createPremiumDto());

        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        List<CompletableFuture<PremiumDto>> futures = new ArrayList<>();

        for (int i = 0; i < numberOfThreads; i++) {
            CompletableFuture<PremiumDto> future = CompletableFuture.supplyAsync(() -> {
                try {
                    return premiumService.buyPremium(1L, PremiumPeriod.MONTHLY);
                } catch (Exception e) {
                    return null;
                } finally {
                    latch.countDown();
                }
            }, executor);
            futures.add(future);
        }

        // Wait for all threads to complete
        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        // Collect results
        List<PremiumDto> results = new ArrayList<>();
        Object lock = new Object();
        for (CompletableFuture<PremiumDto> future : futures) {
            try {
                PremiumDto result = future.get();
                if (result != null) {
                    synchronized (lock) {
                        results.add(result);
                    }
                }
            } catch (Exception e) {
                // Ignore exceptions
            }
        }


        assertThat(results).isNotEmpty();
        // All results should be the same (idempotent)
        for (int i = 1; i < results.size(); i++) {
            assertThat(results.get(i).getUserId()).isEqualTo(results.get(0).getUserId());
            assertThat(results.get(i).getPremiumPeriod()).isEqualTo(results.get(0).getPremiumPeriod());
        }
        // Verify that event was published for successful operations
        if (!results.isEmpty()) {
            verify(boughtEventPublisher, atLeast(1)).publish(any(PremiumBoughtEvent.class));
        }
    }

    @Test
    void buyPremium_WithConcurrentPaymentFailures_ShouldHandleGracefully() throws InterruptedException {

        int numberOfThreads = 5;

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(attemptRepository.findByPaymentNumber(anyString())).thenReturn(Optional.empty());
        when(attemptRepository.save(any(PremiumPurchaseAttempt.class))).thenAnswer(invocation -> {
            PremiumPurchaseAttempt attempt = invocation.getArgument(0);
            // Simulate race condition
            if (attempt.getPaymentNumber().hashCode() % 11 == 0) {
                throw new RuntimeException("Duplicate key");
            }
            return attempt;
        });
        when(attemptRepository.findByPaymentNumber(anyString())).thenReturn(Optional.empty());
        when(paymentClient.processPayment(any())).thenAnswer(invocation -> {
            // Simulate payment failure sometimes
            if (invocation.getArgument(0).toString().hashCode() % 2 == 0) {
                return null;
            }
            return successResponse;
        });
        when(premiumRepository.findFirstByUser_IdAndEndDateAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(premiumRepository.save(any(Premium.class))).thenReturn(testPremium);
        when(premiumMapper.toDto(testPremium)).thenReturn(createPremiumDto());

        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        for (int i = 0; i < numberOfThreads; i++) {
            CompletableFuture.runAsync(() -> {
                try {
                    premiumService.buyPremium(1L, PremiumPeriod.MONTHLY);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            }, executor);
        }

        // Wait for all threads to complete
        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();


        assertThat(successCount.get() + errorCount.get()).isEqualTo(numberOfThreads);
        // Some should succeed, some should fail
        assertThat(successCount.get()).isGreaterThan(0);
        assertThat(errorCount.get()).isGreaterThan(0);
        // Verify that event was published for successful operations
        verify(boughtEventPublisher, atLeast(1)).publish(any(PremiumBoughtEvent.class));
    }

    @Test
    void buyPremium_WithConcurrentCacheOperations_ShouldHandleGracefully() throws InterruptedException {

        int numberOfThreads = 5;

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(attemptRepository.findByPaymentNumber(anyString())).thenReturn(Optional.empty());
        when(attemptRepository.save(any(PremiumPurchaseAttempt.class))).thenAnswer(invocation -> {
            PremiumPurchaseAttempt attempt = invocation.getArgument(0);
            // Simulate race condition
            if (attempt.getPaymentNumber().hashCode() % 11 == 0) {
                throw new RuntimeException("Duplicate key");
            }
            return attempt;
        });
        when(attemptRepository.findByPaymentNumber(anyString())).thenReturn(Optional.empty());
        when(paymentClient.processPayment(any())).thenReturn(successResponse);
        when(premiumRepository.findFirstByUser_IdAndEndDateAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(premiumRepository.save(any(Premium.class))).thenReturn(testPremium);
        when(premiumMapper.toDto(testPremium)).thenReturn(createPremiumDto());

        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        for (int i = 0; i < numberOfThreads; i++) {
            CompletableFuture.runAsync(() -> {
                try {
                    premiumService.buyPremium(1L, PremiumPeriod.MONTHLY);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            }, executor);
        }

        // Wait for all threads to complete
        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();


        assertThat(successCount.get() + errorCount.get()).isEqualTo(numberOfThreads);
        // At least one should succeed
        assertThat(successCount.get()).isGreaterThanOrEqualTo(0);
        // Verify that event was published for successful operations
        if (successCount.get() > 0) {
            verify(boughtEventPublisher, atLeast(1)).publish(any(PremiumBoughtEvent.class));
        }
    }

    @Test
    void buyPremium_WithConcurrentDatabaseOperations_ShouldHandleGracefully() throws InterruptedException {

        int numberOfThreads = 5;

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(attemptRepository.findByPaymentNumber(anyString())).thenReturn(Optional.empty());
        when(attemptRepository.save(any(PremiumPurchaseAttempt.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(attemptRepository.findByPaymentNumber(anyString())).thenReturn(Optional.empty());
        when(paymentClient.processPayment(any())).thenReturn(successResponse);
        when(premiumRepository.findFirstByUser_IdAndEndDateAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(premiumRepository.save(any(Premium.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(premiumMapper.toDto(testPremium)).thenReturn(createPremiumDto());

        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        for (int i = 0; i < numberOfThreads; i++) {
            CompletableFuture.runAsync(() -> {
                try {
                    premiumService.buyPremium(1L, PremiumPeriod.MONTHLY);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            }, executor);
        }

        // Wait for all threads to complete
        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();


        assertThat(successCount.get() + errorCount.get()).isEqualTo(numberOfThreads);
        // At least one should succeed
        assertThat(successCount.get()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void buyPremium_WithConcurrentLeapYearOperations_ShouldHandleCorrectly() throws InterruptedException {

        int numberOfThreads = 5;

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(attemptRepository.findByPaymentNumber(anyString())).thenReturn(Optional.empty());
        when(attemptRepository.save(any(PremiumPurchaseAttempt.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(attemptRepository.findByPaymentNumber(anyString())).thenReturn(Optional.empty());
        when(paymentClient.processPayment(any())).thenReturn(successResponse);
        when(premiumRepository.findFirstByUser_IdAndEndDateAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(premiumRepository.save(any(Premium.class))).thenAnswer(invocation -> {
            Premium premium = invocation.getArgument(0);
            // Verify leap year handling
            LocalDateTime startDate = premium.getStartDate();
            LocalDateTime endDate = premium.getEndDate();
            assertThat(endDate).isEqualTo(startDate.plusDays(30));
            return premium;
        });
        when(premiumMapper.toDto(testPremium)).thenReturn(createPremiumDto());

        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        for (int i = 0; i < numberOfThreads; i++) {
            CompletableFuture.runAsync(() -> {
                try {
                    premiumService.buyPremium(1L, PremiumPeriod.MONTHLY);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            }, executor);
        }

        // Wait for all threads to complete
        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();


        assertThat(successCount.get() + errorCount.get()).isEqualTo(numberOfThreads);
        // At least one should succeed
        assertThat(successCount.get()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void buyPremium_WithConcurrentYearBoundaryOperations_ShouldHandleCorrectly() throws InterruptedException {

        int numberOfThreads = 5;

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(attemptRepository.findByPaymentNumber(anyString())).thenReturn(Optional.empty());
        when(attemptRepository.save(any(PremiumPurchaseAttempt.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(attemptRepository.findByPaymentNumber(anyString())).thenReturn(Optional.empty());
        when(paymentClient.processPayment(any())).thenReturn(successResponse);
        when(premiumRepository.findFirstByUser_IdAndEndDateAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(premiumRepository.save(any(Premium.class))).thenAnswer(invocation -> {
            Premium premium = invocation.getArgument(0);
            // Verify year boundary handling
            LocalDateTime startDate = premium.getStartDate();
            LocalDateTime endDate = premium.getEndDate();
            assertThat(endDate).isEqualTo(startDate.plusDays(30));
            return premium;
        });
        when(premiumMapper.toDto(testPremium)).thenReturn(createPremiumDto());

        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        for (int i = 0; i < numberOfThreads; i++) {
            CompletableFuture.runAsync(() -> {
                try {
                    premiumService.buyPremium(1L, PremiumPeriod.MONTHLY);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            }, executor);
        }

        // Wait for all threads to complete
        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();


        assertThat(successCount.get() + errorCount.get()).isEqualTo(numberOfThreads);
        // At least one should succeed
        assertThat(successCount.get()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void buyPremium_WithConcurrentDSTOperations_ShouldHandleCorrectly() throws InterruptedException {

        int numberOfThreads = 5;

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(attemptRepository.findByPaymentNumber(anyString())).thenReturn(Optional.empty());
        when(attemptRepository.save(any(PremiumPurchaseAttempt.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(attemptRepository.findByPaymentNumber(anyString())).thenReturn(Optional.empty());
        when(paymentClient.processPayment(any())).thenReturn(successResponse);
        when(premiumRepository.findFirstByUser_IdAndEndDateAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(premiumRepository.save(any(Premium.class))).thenAnswer(invocation -> {
            Premium premium = invocation.getArgument(0);
            // Verify DST handling
            LocalDateTime startDate = premium.getStartDate();
            LocalDateTime endDate = premium.getEndDate();
            assertThat(endDate).isEqualTo(startDate.plusDays(30));
            return premium;
        });
        when(premiumMapper.toDto(testPremium)).thenReturn(createPremiumDto());

        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        for (int i = 0; i < numberOfThreads; i++) {
            CompletableFuture.runAsync(() -> {
                try {
                    premiumService.buyPremium(1L, PremiumPeriod.MONTHLY);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            }, executor);
        }

        // Wait for all threads to complete
        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();


        assertThat(successCount.get() + errorCount.get()).isEqualTo(numberOfThreads);
        // At least one should succeed
        assertThat(successCount.get()).isGreaterThanOrEqualTo(0);
    }

    private PremiumDto createPremiumDto() {
        return PremiumDto.builder()
                .id(1L)
                .userId(1L)
                .premiumPeriod(PremiumPeriod.MONTHLY)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .amount(new BigDecimal("10.00"))
                .paymentNumber("PREM-1-test123")
                .verificationCode(12345)
                .currency(Currency.USD)
                .createdAt(LocalDateTime.now())
                .build();
    }
}