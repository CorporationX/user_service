package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.dto.premium.PremiumPeriod;
import school.faang.user_service.service.PremiumService;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PremiumControllerTest {
    private MockMvc mockMvc;

    @Mock
    private UserContext userContext;

    @Mock
    private PremiumService premiumService;

    @InjectMocks
    private PremiumController premiumController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(premiumController).build();
    }

    @Test
    public void buyPremium_whenValidBuyPremium_thenReturnsPremiumDto() throws Exception {
        PremiumDto premiumDto = new PremiumDto(1L, 1L);
        when(premiumService.buyPremium(1L, PremiumPeriod.ONE_MONTH)).thenReturn(premiumDto);
        when(userContext.getUserId()).thenReturn(1L);

        mockMvc.perform(post("/premium/buy")
                        .header("id", 1)
                        .param("day", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.userId", is(1)));
    }
}
