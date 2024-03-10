package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.controller.premium.PremiumController;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.service.premium.PremiumService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PremiumControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PremiumService premiumService;
    
    @InjectMocks
    private PremiumController premiumController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(premiumController).build();
    }

    @Test
    void testShouldBuyPremium() throws Exception {
        Long userId = 1L;
        Integer days = 30;
        PremiumDto mockPremiumDto = PremiumDto.builder().build();

        when(premiumService.buyPremiumSubscription(userId, days)).thenReturn(mockPremiumDto);

        mockMvc.perform(post("/api/v1/premium/buy")
                        .header("x-user-id", userId)
                        .param("days", days.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
  }

}