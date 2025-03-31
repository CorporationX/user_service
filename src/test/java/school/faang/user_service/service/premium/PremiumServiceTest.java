package school.faang.user_service.service.premium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.premium.PaymentResponse;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.enums.PaymentStatus;
import school.faang.user_service.enums.PremiumPeriod;
import school.faang.user_service.exception.PaymentPayException;
import school.faang.user_service.exception.PaymentServiceException;
import school.faang.user_service.exception.PremiumAlreadyExistsException;
import school.faang.user_service.kafka.producer.PremiumBoughtEventProducer;
import school.faang.user_service.mapper.PremiumMapperImpl;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.PremiumService;
import school.faang.user_service.service.UserService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PremiumServiceTest {

    @Mock
    private PremiumRepository premiumRepository;
    @Mock
    private PaymentServiceClient paymentServiceClient;
    @Spy
    private PremiumMapperImpl premiumMapper;
    @Mock
    private UserService userService;
    @Mock
    private PremiumBoughtEventProducer premiumBoughtEventProducer;

    @InjectMocks
    private PremiumService premiumService;

    private User user;
    private Premium premium;
    private long userId = 1L;


    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(premiumService, "removerBatchSize", 2);
        user = User.builder()
                .id(userId)
                .build();

        premium = Premium.builder()
                .user(user)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(90))
                .build();
    }

    @Test
    public void buyPremium_Success() {

        when(userService.getUserById(userId)).thenReturn(user);
        when(premiumRepository.existsByUserId(userId)).thenReturn(false);
        when(paymentServiceClient.processPayment(any())).thenReturn(new PaymentResponse(
                PaymentStatus.SUCCESS, 6345, 1L,
                new BigDecimal(30), Currency.getInstance("USD"), "success"
        ));
        when(premiumRepository.save(any())).thenReturn(premium);

        var premiumPeriod = PremiumPeriod.fromDays(90);

        PremiumDto premiumDto = premiumService.buyPremium(userId, premiumPeriod);

        assertEquals(premiumDto.userName(), premium.getUser().getUsername());
    }

    @Test
    void buyPremium_AlreadyExists() {

        when(premiumRepository.existsByUserId(userId)).thenReturn(true);

        PremiumAlreadyExistsException exception = assertThrows(PremiumAlreadyExistsException.class, () ->
                premiumService.buyPremium(userId, PremiumPeriod.fromDays(90))
        );

        assertEquals("The user is already available in the premium", exception.getMessage());

        verify(premiumRepository, times(1)).existsByUserId(userId);
        verify(premiumMapper, never()).toEntity(any());
        verify(premiumRepository, never()).save(any());
        verify(premiumMapper, never()).toDto(any());
    }

    @Test
    void buyPremium_PaymentPayFailed() {

        when(userService.getUserById(userId)).thenReturn(user);
        when(premiumRepository.existsByUserId(userId)).thenReturn(false);
        when(paymentServiceClient.processPayment(any())).thenReturn(new PaymentResponse(
                PaymentStatus.ERROR, 6345, 1L,
                new BigDecimal(25), Currency.getInstance("USD"), "success"
        ));


        PaymentPayException exception = assertThrows(PaymentPayException.class, () ->
                premiumService.buyPremium(userId, PremiumPeriod.fromDays(90))
        );

        assertEquals("Payment failed.", exception.getMessage());

        verify(premiumRepository, times(1)).existsByUserId(userId);
        verify(premiumMapper, never()).toEntity(any());
        verify(premiumRepository, never()).save(any());
        verify(premiumMapper, never()).toDto(any());
    }

    @Test
    void buyPremium_PaymentServiceFailed() {

        when(userService.getUserById(userId)).thenReturn(user);
        when(premiumRepository.existsByUserId(userId)).thenReturn(false);
        when(paymentServiceClient.processPayment(any())).thenThrow(new PaymentServiceException("Payment service not working."));

        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () ->
                premiumService.buyPremium(userId, PremiumPeriod.fromDays(90))
        );

        assertEquals("Payment service not working.", exception.getMessage());

        verify(premiumRepository, times(1)).existsByUserId(userId);
        verify(premiumMapper, never()).toEntity(any());
        verify(premiumRepository, never()).save(any());
        verify(premiumMapper, never()).toDto(any());
    }

    @Test
    void removeExpiredPremiums_noExpiredPremiums_shouldNotDeleteAnything() {
        // Мокаем вызов репозитория, возвращая пустой список (нет просроченных премиумов)
        when(premiumRepository.findAllByEndDateBefore(any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        premiumService.removeExpiredPremiums();

        // Убеждаемся, что метод удаления не был вызван
        verify(premiumRepository, never()).deleteAll(anyList());
    }

    @Test
    void removeExpiredPremiums_withExpiredPremiums_shouldDeleteInBatches() {
        // Создаем список из 5 просроченных премиумов (endDate в прошлом)
        List<Premium> expiredPremiums = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Premium expiredPremium = Premium.builder()
                    .user(user)
                    .startDate(LocalDateTime.now().minusDays(100))
                    .endDate(LocalDateTime.now().minusDays(1))
                    .build();
            expiredPremiums.add(expiredPremium);
        }

        // Мокаем репозиторий, возвращающий список просроченных премиумов
        when(premiumRepository.findAllByEndDateBefore(any(LocalDateTime.class)))
                .thenReturn(expiredPremiums);

        premiumService.removeExpiredPremiums();

        // При размере батча = 2, ожидаем разбивку на 3 батча: [2, 2, 1]
        ArgumentCaptor<List<Premium>> captor = ArgumentCaptor.forClass(List.class);
        verify(premiumRepository, times(3)).deleteAll(captor.capture());

        List<List<Premium>> capturedBatches = captor.getAllValues();
        assertEquals(2, capturedBatches.get(0).size());
        assertEquals(2, capturedBatches.get(1).size());
        assertEquals(1, capturedBatches.get(2).size());
    }

    @Test
    void removeExpiredPremiumsByBatches_emptyList_shouldNotDeleteAnything() {
        // Передаем пустой список – метод не должен пытаться удалить премиумы
        premiumService.removeExpiredPremiumsByBatches(Collections.emptyList());

        verify(premiumRepository, never()).deleteAll(anyList());
    }

    @Test
    void removeExpiredPremiumsByBatches_nonEmptyList_shouldDeleteExpiredPremiums() {
        // Создаем один просроченный премиум
        List<Premium> expiredBatch = new ArrayList<>();
        expiredBatch.add(Premium.builder()
                .user(user)
                .startDate(LocalDateTime.now().minusDays(100))
                .endDate(LocalDateTime.now().minusDays(1))
                .build());

        premiumService.removeExpiredPremiumsByBatches(expiredBatch);

        // Проверяем, что метод deleteAll вызван ровно один раз с переданным батчем
        verify(premiumRepository, times(1)).deleteAll(expiredBatch);
    }
}