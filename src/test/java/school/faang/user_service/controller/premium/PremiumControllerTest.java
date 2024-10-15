package school.faang.user_service.controller.premium;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.model.dto.premium.PremiumDto;
import school.faang.user_service.model.entity.premium.PremiumPeriod;
import school.faang.user_service.service.PremiumService;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PremiumControllerTest {

    @InjectMocks
    private PremiumController premiumController;

    @Mock
    private UserContext userContext;

    @Mock
    private PremiumService premiumService;

    @Test
    void buyPremium_whenValid_thenCorrect() throws Exception {
        // given
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(premiumController).build();
        when(userContext.getUserId()).thenReturn(1L);
        PremiumDto premiumDto = PremiumDto
                .builder()
                .build();
        when(premiumService.buyPremium(any(Long.class), any(PremiumPeriod.class))).thenReturn(premiumDto);
        // when
        mockMvc.perform(post("/api/v1/premium/30")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        // then
        verify(premiumService, times(1)).buyPremium(any(Long.class), any(PremiumPeriod.class));
    }
}