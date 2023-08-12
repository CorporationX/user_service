package school.faang.user_service.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import school.faang.user_service.client.PaymentFeignClient;
import school.faang.user_service.dto.premium.PremiumResponseDto;
import school.faang.user_service.mapper.premium.PremiumResponseMapper;
import school.faang.user_service.model.Payment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentFeignClient paymentFeignClient;

    @Mock
    private PremiumResponseMapper premiumResponseMapper;

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = new Payment();
    }

    @Test
    void makePaymentSuccessfullyTest() {
        PaymentResponse paymentResponse = new PaymentResponse();

        when(paymentFeignClient.makePayment(payment))
            .thenReturn(new ResponseEntity<>(paymentResponse, HttpStatus.OK));

        PremiumResponseDto premiumResponseDto = new PremiumResponseDto();

        when(premiumResponseMapper.toDto(paymentResponse)).thenReturn(premiumResponseDto);

        PremiumResponseDto result = paymentService.makePayment(payment);

        assertEquals(premiumResponseDto, result);
    }

    @Nested
    class MakePaymentFailureTest {

        @Test
        void whenStatusCodeNotOkThenThrowExc() {
            when(paymentFeignClient.makePayment(payment)).thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

            assertThrows(IllegalArgumentException.class, () -> paymentService.makePayment(payment));
        }

        @Test
        void whenFailedToConnectThenThrowExc() {
            when(paymentFeignClient.makePayment(payment)).thenThrow(new ResourceAccessException("Failed to connect"));

            assertThrows(IllegalStateException.class, () -> paymentService.makePayment(payment));
        }
    }
}