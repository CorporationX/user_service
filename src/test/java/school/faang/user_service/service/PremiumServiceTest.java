package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.client.payment_service.Currency;
import school.faang.user_service.client.payment_service.PaymentServiceClient;
import school.faang.user_service.client.payment_service.PaymentStatus;
import school.faang.user_service.client.payment_service.PaymentPostPayRequestDto;
import school.faang.user_service.client.payment_service.PaymentPostPayResponseDto;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.premium.PremiumMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.premium.PremiumService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PremiumServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PremiumRepository premiumRepository;
    @Mock
    private PaymentServiceClient paymentServiceClient;
    @Spy
    private PremiumMapper premiumMapper = Mappers.getMapper(PremiumMapper.class);

    @InjectMocks
    private PremiumService service;

    private final long userId = 1L;
    private final int days = 30;
    private final LocalDateTime startDate = LocalDateTime.now();
    private final LocalDateTime endDate = LocalDateTime.now().plusDays(days);
    private final BigDecimal amount = new BigDecimal(10);

    private User generateUser() {
        return User.builder()
                .id(userId)
                .build();
    }

    private PaymentPostPayRequestDto getPostPaymentRequestDto() {
        return new PaymentPostPayRequestDto(1, amount, Currency.USD);
    }

    private ResponseEntity<PaymentPostPayResponseDto> getPostPaymentResponseDto() {
        PaymentPostPayResponseDto responseDto = new PaymentPostPayResponseDto(
                PaymentStatus.SUCCESS,
                1234,
                1,
                amount,
                Currency.USD,
                "Thank you!"
        );

        return ResponseEntity.ok(responseDto);
    }

    private Premium getPremium() {
        return new Premium(1L, generateUser(), startDate, endDate);
    }

    private PremiumDto getExpectedPremiumDto() {
        return premiumMapper.toDto(getPremium());
    }

    @Test
    public void testBuyPremiumValidation() {
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());
        Assert.assertThrows("User not found", DataValidationException.class, () -> service.buyPremium(userId, days));

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(generateUser()));
        Mockito.when(premiumRepository.existsByUserId(userId)).thenReturn(true);
        Assert.assertThrows("User already has premium", DataValidationException.class, () -> service.buyPremium(userId, days));

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(generateUser()));
        Mockito.when(premiumRepository.existsByUserId(userId)).thenReturn(false);
        Assert.assertThrows("No premium period found for 1000 days", IllegalArgumentException.class, () -> service.buyPremium(userId, 1000));

        Mockito.when(paymentServiceClient.pay(getPostPaymentRequestDto())).thenReturn(ResponseEntity.badRequest().build());
        Assert.assertThrows("Payment failed", RuntimeException.class, () -> service.buyPremium(userId, days));
    }

    @Test
    public void testBuyPremium() {
        // given
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(generateUser()));
        Mockito.when(premiumRepository.existsByUserId(userId)).thenReturn(false);
        Mockito.when(premiumRepository.save(Mockito.any(Premium.class))).thenReturn(getPremium());
        Mockito.when(paymentServiceClient.pay(getPostPaymentRequestDto())).thenReturn(getPostPaymentResponseDto());
        PremiumDto expectedPremiumDto = getExpectedPremiumDto();

        // when
        PremiumDto actualPremiumDto = service.buyPremium(userId, days);

        // then
        Mockito.verify(paymentServiceClient, Mockito.times(1)).pay(getPostPaymentRequestDto());
        Mockito.verify(premiumRepository, Mockito.times(1)).save(Mockito.any(Premium.class));
        Assertions.assertEquals(expectedPremiumDto, actualPremiumDto);
    }

    @Test
    public void testRemovePremium() {
        service.removePremium(userId);

        Mockito.verify(premiumRepository, Mockito.times(1)).deleteByUserId(userId);
    }

    @Test
    public void testRemoveExpiredPremium() {
        // given
        Mockito.when(premiumRepository.findAllByEndDateBefore(Mockito.any())).thenReturn(List.of(getPremium()));

        // when
        service.removeExpiredUsers();

        // then
        Mockito.verify(premiumRepository, Mockito.times(1)).deleteByUserId(userId);
    }

    @Test
    public void testDontRemoveExpiredPremiumIfNoUsers() {
        // given
        Mockito.when(premiumRepository.findAllByEndDateBefore(Mockito.any())).thenReturn(List.of());

        // when
        service.removeExpiredUsers();

        // then
        Mockito.verify(premiumRepository, Mockito.times(0)).deleteByUserId(userId);
    }
}
