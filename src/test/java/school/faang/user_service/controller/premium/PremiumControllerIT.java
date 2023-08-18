package school.faang.user_service.controller.premium;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.dto.premium.PremiumRequestDto;
import school.faang.user_service.dto.premium.PremiumResponseDto;
import school.faang.user_service.model.Payment;
import school.faang.user_service.model.TariffPlan;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


//@SpringBootTest
//@AutoConfigureMockMvc
class PremiumControllerIT {
//    private static final String URL = "http://localhost:8080/api/v1/premium";
//    private static final String STATUS = "SUCCESS";
//    private static final long USER_ID = 11112L;
//    private static final long PAYMENT_NUMBER = 1111_1111_1111_1111L;
//    private static final String MESSAGES = "Payment successful ^_^ !!!";
//
//    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private PremiumController premiumController;
//
//    @Test
//    public void testMakePayment() throws Exception {
//        PremiumRequestDto request = new PremiumRequestDto();
//        request.setPayment(new Payment(PAYMENT_NUMBER, BigDecimal.TEN, "USD"));
//        request.setTariffPlan(TariffPlan.MONTHLY);
//
//        PremiumResponseDto mockResponse = new PremiumResponseDto(
//            STATUS,
//            200,
//            request.getPayment().getPaymentNumber(),
//            request.getPayment().getAmount(),
//            request.getPayment().getCurrency(),
//            MESSAGES,
//            new PremiumDto(
//                USER_ID,
//                TariffPlan.MONTHLY,
//                LocalDateTime.now(),
//                request.getTariffPlan().getEndDate()
//            )
//        );
//
//        when(premiumController.buyPremium(request)).thenReturn(mockResponse);
//
//        mockMvc.perform(MockMvcRequestBuilders.post(URL)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(new ObjectMapper().writeValueAsString(request)))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.status").value(STATUS))
//            .andExpect(jsonPath("$.message").value(MESSAGES))
//            .andExpect(jsonPath("$.verificationCode").value(200))
//            .andExpect(jsonPath("$.paymentNumber").value(PAYMENT_NUMBER))
//            .andExpect(jsonPath("$.amount").value(BigDecimal.TEN))
//            .andExpect(jsonPath("$.currency").value("USD"))
//            .andExpect(jsonPath("$.tariffPlan.userId").value(USER_ID))
//            .andExpect(jsonPath("$.tariffPlan.tariffPlan").value(TariffPlan.MONTHLY.name()));
//    }
}