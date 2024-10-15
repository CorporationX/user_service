package school.faang.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.model.dto.PremiumDto;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.enums.PremiumPeriod;
import school.faang.user_service.service.impl.PremiumServiceImpl;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PremiumControllerTest {
    private static final PremiumPeriod PREMIUM_PERIOD = PremiumPeriod.THREE_MONTHS;
    private static final LocalDateTime START_DATE = LocalDateTime.of(2024, 9, 15, 0, 0);
    private static final LocalDateTime END_DATE = START_DATE.plusDays(PREMIUM_PERIOD.getDays());

    private User user;
    private PremiumDto premiumDto;
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;

    @Mock
    private PremiumServiceImpl premiumService;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private PremiumController premiumController;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);

        premiumDto = PremiumDto.builder()
                .id(1L)
                .userId(user.getId())
                .startDate(START_DATE)
                .endDate(END_DATE)
                .build();

        mockMvc = MockMvcBuilders.standaloneSetup(premiumController).build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Should return PremiumDto with status CREATED when buyPremium is successful")
    public void testBuyPremium_Success() throws Exception {
        when(premiumService.buyPremium(anyLong(), eq(PREMIUM_PERIOD))).thenReturn(premiumDto);
        when(userContext.getUserId()).thenReturn(user.getId());

        mockMvc.perform(post("/premium")
                        .param("days", String.valueOf(PREMIUM_PERIOD.getDays())))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(premiumDto)));

        verify(premiumService, times(1)).buyPremium(user.getId(),
                PremiumPeriod.fromDays(PREMIUM_PERIOD.getDays()));
    }
}
