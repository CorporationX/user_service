package school.faang.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.service.PremiumService;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PremiumControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PremiumService premiumService;

    @InjectMocks
    private PremiumController premiumController;

    @Spy
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(premiumController).build();
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testByPremium() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        PremiumDto premiumDto = PremiumDto.builder()
                .id(1L)
                .userId(1L)
                .startDate(now)
                .endDate(now.plusMonths(3))
                .build();
        Mockito.when(premiumService.buyPremium(1L, PremiumPeriod.THREE_MONTH)).thenReturn(premiumDto);
        mockMvc.perform(post("/premium/buy/1")
                        .param("days", "90"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(premiumDto)));
    }
}