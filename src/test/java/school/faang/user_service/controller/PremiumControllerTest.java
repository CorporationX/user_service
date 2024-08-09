package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.controller.premium.PremiumController;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.enums.PremiumPeriod;
import school.faang.user_service.service.premium.PremiumService;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PremiumControllerTest {

    private MockMvc mockMvc;
    @Mock
    private PremiumService service;
    @InjectMocks
    private PremiumController controller;
    private long premiumId = 1;
    private long userId = 1;
    private int days = 30;

    private PremiumDto resultOfService = new PremiumDto(premiumId, userId, LocalDateTime.now(), LocalDateTime.now().plusDays(days));

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void buyPremium() throws Exception {
        controller.buyPremium(userId, days);
        Mockito.when(service.buyPremium(userId, PremiumPeriod.fromDays(days))).thenReturn(resultOfService);
        Mockito.verify(service, Mockito.times(1)).buyPremium(userId, PremiumPeriod.fromDays(days));
        MvcResult requestResult = mockMvc.perform(post("/premium/buyPremium?days={days}", days).header("x-user-id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json")).andReturn();
    }
}