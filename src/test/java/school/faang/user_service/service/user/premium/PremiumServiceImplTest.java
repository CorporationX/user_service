package school.faang.user_service.service.user.premium;

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
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.dto.payment.types.Currency;
import school.faang.user_service.dto.payment.types.PaymentStatus;
import school.faang.user_service.dto.types.PremiumPeriod;
import school.faang.user_service.exception.PaymentException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.premium.PremiumRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PremiumServiceImplTest {

    @Mock
    private PremiumRepository premiumRepository;
    @Spy
    private PremiumMapper premiumMapper = Mappers.getMapper(PremiumMapper.class);
    @Mock
    private PaymentServiceClient paymentServiceClient;

    @InjectMocks
    private PremiumServiceImpl premiumServiceImpl;

    @Test
    void successBuyPremium() {
        PremiumPeriod premiumPeriod = PremiumPeriod.MONTH;
        Long userId = 1L;
        PaymentRequest paymentRequest = new PaymentRequest(1, premiumPeriod.getPrice(), Currency.USD);

        when(paymentServiceClient.sendPayment(paymentRequest)).thenReturn(ResponseEntity.ok(new PaymentResponse(
                PaymentStatus.SUCCESS,
                0,
                0,
                null,
                null,
                null
        )));

        when(premiumRepository.existsByUserId(userId)).thenReturn(false);

        premiumServiceImpl.buyPremium(userId, premiumPeriod);

        verify(premiumRepository).save(any());
    }

    @Test
    void buyPremiumThrowException() {
        PremiumPeriod premiumPeriod = PremiumPeriod.MONTH;
        Long userId = 1L;
        PaymentRequest paymentRequest = new PaymentRequest(1, premiumPeriod.getPrice(), Currency.USD);

        when(paymentServiceClient.sendPayment(paymentRequest)).thenReturn(ResponseEntity.ok(new PaymentResponse(
                PaymentStatus.FAILURE,
                0,
                0,
                null,
                null,
                null
        )));

        assertThrows(PaymentException.class, () -> premiumServiceImpl.buyPremium(userId, premiumPeriod));
    }
}
