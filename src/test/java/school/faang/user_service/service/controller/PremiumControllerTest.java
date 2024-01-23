package school.faang.user_service.service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.controller.PremiumController;
import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.service.PremiumService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PremiumControllerTest {

    @Mock
    private PremiumService premiumService;

    @Spy
    UserContext userContext;

    @InjectMocks
    private PremiumController premiumController;

    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        userContext.setUserId(1L);
        mockMvc = MockMvcBuilders.standaloneSetup(premiumController).build();
    }

    @Test
    public void testBuyPremium() {
        premiumController.buyPremium(30);
        Mockito.verify(premiumService, Mockito.times(1))
                .buyPremium(1L, PremiumPeriod.ONE_MONTH);
    }

    @Test
    public void testValidPremiumReturnsPremiumDto() throws Exception {
        PremiumDto premiumDto = new PremiumDto();
        premiumDto.setUserId(1L);
        premiumDto.setId(1L);
        Mockito.when(premiumService.buyPremium(1L, PremiumPeriod.ONE_MONTH))
                .thenReturn(premiumDto);
        Mockito.when(userContext.getUserId()).thenReturn(1L);

        mockMvc.perform((post("/premium/buy/30", 1L, PremiumPeriod.ONE_MONTH))
                        .contentType("application/json"))
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("userId").value(1L))
                .andExpect(status().isOk());
    }
}
